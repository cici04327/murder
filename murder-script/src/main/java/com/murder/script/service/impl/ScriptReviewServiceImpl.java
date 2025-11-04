package com.murder.script.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.murder.common.context.BaseContext;
import com.murder.common.result.PageResult;
import com.murder.pojo.dto.ScriptReviewDTO;
import com.murder.pojo.entity.Script;
import com.murder.pojo.entity.ScriptReview;
import com.murder.pojo.vo.ScriptReviewVO;
import com.murder.script.mapper.ScriptMapper;
import com.murder.script.mapper.ScriptReviewMapper;
import com.murder.script.service.ScriptReviewService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 剧本评价服务实现类
 */
@Service
public class ScriptReviewServiceImpl implements ScriptReviewService {

    @Autowired
    private ScriptReviewMapper reviewMapper;
    
    @Autowired
    private ScriptMapper scriptMapper;
    
    @Autowired(required = false)
    private RestTemplate restTemplate;

    @Override
    @Transactional
    public void add(ScriptReviewDTO reviewDTO) {
        ScriptReview review = new ScriptReview();
        BeanUtils.copyProperties(reviewDTO, review);
        
        // 设置用户ID
        Long userId = BaseContext.getCurrentId();
        if (userId != null) {
            review.setUserId(userId);
        }
        
        // 保存评价
        reviewMapper.insert(review);
        
        // 更新剧本平均评分
        updateScriptRating(reviewDTO.getScriptId());
    }

    @Override
    public PageResult<ScriptReviewVO> pageQuery(Long scriptId, Integer page, Integer pageSize) {
        // 先查询总数
        LambdaQueryWrapper<ScriptReview> countWrapper = new LambdaQueryWrapper<>();
        if (scriptId != null) {
            countWrapper.eq(ScriptReview::getScriptId, scriptId);
        }
        Long total = reviewMapper.selectCount(countWrapper);
        
        // 再查询分页数据
        Page<ScriptReview> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<ScriptReview> wrapper = new LambdaQueryWrapper<>();
        if (scriptId != null) {
            wrapper.eq(ScriptReview::getScriptId, scriptId);
        }
        wrapper.orderByDesc(ScriptReview::getCreateTime);
        
        reviewMapper.selectPage(pageInfo, wrapper);
        
        List<ScriptReviewVO> voList = pageInfo.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return new PageResult<>(total, voList);
    }

    @Override
    public ScriptReviewVO getById(Long id) {
        ScriptReview review = reviewMapper.selectById(id);
        return convertToVO(review);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ScriptReview review = reviewMapper.selectById(id);
        if (review != null) {
            reviewMapper.deleteById(id);
            // 更新剧本平均评分
            updateScriptRating(review.getScriptId());
        }
    }

    @Override
    public ScriptReview getByReservationId(Long reservationId) {
        LambdaQueryWrapper<ScriptReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScriptReview::getReservationId, reservationId);
        return reviewMapper.selectOne(wrapper);
    }

    /**
     * 更新剧本评分
     */
    private void updateScriptRating(Long scriptId) {
        BigDecimal avgRating = reviewMapper.calculateAverageRating(scriptId);
        if (avgRating != null) {
            Script script = new Script();
            script.setId(scriptId);
            script.setRating(avgRating.setScale(2, RoundingMode.HALF_UP));
            scriptMapper.updateById(script);
        }
    }

    /**
     * 转换为VO
     */
    private ScriptReviewVO convertToVO(ScriptReview review) {
        if (review == null) {
            return null;
        }
        ScriptReviewVO vo = new ScriptReviewVO();
        BeanUtils.copyProperties(review, vo);
        
        // 查询剧本名称
        if (review.getScriptId() != null) {
            Script script = scriptMapper.selectById(review.getScriptId());
            if (script != null) {
                vo.setScriptName(script.getName());
            }
        }
        
        // 查询用户昵称（通过RestTemplate调用用户服务）
        if (review.getUserId() != null) {
            try {
                if (restTemplate != null) {
                    String url = "http://localhost:8081/user/" + review.getUserId();
                    Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                    if (response != null && response.get("data") != null) {
                        Map<String, Object> userData = (Map<String, Object>) response.get("data");
                        String nickname = (String) userData.get("nickname");
                        vo.setUserNickname(nickname != null ? nickname : "匿名用户");
                    } else {
                        vo.setUserNickname("用户" + review.getUserId());
                    }
                } else {
                    vo.setUserNickname("用户" + review.getUserId());
                }
            } catch (Exception e) {
                vo.setUserNickname("用户" + review.getUserId());
            }
        } else {
            vo.setUserNickname("匿名用户");
        }
        
        return vo;
    }
}
