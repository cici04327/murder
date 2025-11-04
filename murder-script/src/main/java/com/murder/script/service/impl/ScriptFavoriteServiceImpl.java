package com.murder.script.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.murder.common.result.PageResult;
import com.murder.pojo.entity.Script;
import com.murder.pojo.entity.ScriptFavorite;
import com.murder.script.mapper.ScriptFavoriteMapper;
import com.murder.script.mapper.ScriptMapper;
import com.murder.script.service.ScriptFavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 剧本收藏服务实现类
 */
@Service
@Slf4j
public class ScriptFavoriteServiceImpl implements ScriptFavoriteService {
    
    @Autowired
    private ScriptFavoriteMapper scriptFavoriteMapper;
    
    @Autowired
    private ScriptMapper scriptMapper;
    
    @Autowired(required = false)
    private RestTemplate restTemplate;
    
    private static final String USER_SERVICE_URL = "http://localhost:8082";
    
    /**
     * 收藏剧本
     */
    @Override
    @Transactional
    public void favoriteScript(Long userId, Long scriptId) {
        // 检查是否已收藏
        LambdaQueryWrapper<ScriptFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScriptFavorite::getUserId, userId)
               .eq(ScriptFavorite::getScriptId, scriptId);
        
        Long count = scriptFavoriteMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("已收藏该剧本");
        }
        
        // 创建收藏记录
        ScriptFavorite favorite = ScriptFavorite.builder()
                .userId(userId)
                .scriptId(scriptId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        
        scriptFavoriteMapper.insert(favorite);
        
        // 获取用户当前的收藏数量
        Integer favoriteCount = getFavoriteCount(userId);
        log.info("用户{}收藏剧本{}，当前收藏数量: {}", userId, scriptId, favoriteCount);
        
        // 每收藏5个剧本奖励20积分
        if (favoriteCount % 5 == 0) {
            try {
                if (restTemplate != null) {
                    String url = USER_SERVICE_URL + "/user/points/reward-favorite?userId=" + userId;
                    restTemplate.postForObject(url, null, String.class);
                    log.info("用户{}收藏剧本达到{}个，获得20积分奖励", userId, favoriteCount);
                }
            } catch (Exception e) {
                log.error("调用积分服务失败", e);
                // 积分服务失败不影响收藏功能
            }
        }
    }
    
    /**
     * 取消收藏剧本
     */
    @Override
    @Transactional
    public void unfavoriteScript(Long userId, Long scriptId) {
        LambdaQueryWrapper<ScriptFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScriptFavorite::getUserId, userId)
               .eq(ScriptFavorite::getScriptId, scriptId);
        
        scriptFavoriteMapper.delete(wrapper);
    }
    
    /**
     * 检查是否已收藏
     */
    @Override
    public boolean isFavorited(Long userId, Long scriptId) {
        LambdaQueryWrapper<ScriptFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScriptFavorite::getUserId, userId)
               .eq(ScriptFavorite::getScriptId, scriptId);
        
        Long count = scriptFavoriteMapper.selectCount(wrapper);
        return count > 0;
    }
    
    /**
     * 获取用户收藏的剧本列表
     */
    @Override
    public PageResult<Script> getUserFavorites(Long userId, Integer page, Integer pageSize) {
        // 查询用户收藏的剧本ID列表
        LambdaQueryWrapper<ScriptFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScriptFavorite::getUserId, userId)
               .orderByDesc(ScriptFavorite::getCreateTime);
        
        Page<ScriptFavorite> pageInfo = new Page<>(page, pageSize);
        Page<ScriptFavorite> favoritePage = scriptFavoriteMapper.selectPage(pageInfo, wrapper);
        
        List<Long> scriptIds = favoritePage.getRecords().stream()
                .map(ScriptFavorite::getScriptId)
                .collect(Collectors.toList());
        
        // 查询剧本详情
        List<Script> scripts;
        if (scriptIds.isEmpty()) {
            scripts = List.of();
        } else {
            LambdaQueryWrapper<Script> scriptWrapper = new LambdaQueryWrapper<>();
            scriptWrapper.in(Script::getId, scriptIds);
            scripts = scriptMapper.selectList(scriptWrapper);
        }
        
        // 使用 Long 类型的 total
        Long total = favoritePage.getTotal();
        log.info("查询用户{}的收藏列表，总数:{}, 当前页数据:{}", userId, total, scripts.size());
        
        return new PageResult<>(total, scripts);
    }
    
    /**
     * 获取用户收藏数量
     */
    @Override
    public Integer getFavoriteCount(Long userId) {
        LambdaQueryWrapper<ScriptFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScriptFavorite::getUserId, userId);
        
        Long count = scriptFavoriteMapper.selectCount(wrapper);
        return count.intValue();
    }
}

