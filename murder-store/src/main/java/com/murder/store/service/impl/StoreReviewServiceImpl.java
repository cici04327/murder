package com.murder.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.murder.common.result.PageResult;
import com.murder.pojo.entity.Store;
import com.murder.pojo.entity.StoreReview;
import com.murder.pojo.vo.StoreReviewVO;
import com.murder.store.mapper.StoreMapper;
import com.murder.store.mapper.StoreReviewMapper;
import com.murder.store.service.StoreReviewService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 门店评价服务实现类
 */
@Service
public class StoreReviewServiceImpl implements StoreReviewService {

    @Autowired
    private StoreReviewMapper reviewMapper;
    
    @Autowired
    private StoreMapper storeMapper;
    
    @Autowired(required = false)
    private RestTemplate restTemplate;

    @Override
    public PageResult<StoreReviewVO> pageQuery(Long storeId, Integer page, Integer pageSize) {
        Page<StoreReview> pageInfo = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<StoreReview> wrapper = new LambdaQueryWrapper<>();
        if (storeId != null) {
            wrapper.eq(StoreReview::getStoreId, storeId);
        }
        wrapper.orderByDesc(StoreReview::getCreateTime);
        
        // 手动查询总数
        Long total = reviewMapper.selectCount(wrapper);
        
        reviewMapper.selectPage(pageInfo, wrapper);
        
        List<StoreReviewVO> voList = pageInfo.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        // 使用手动查询的 total 值
        return new PageResult<>(total, voList);
    }

    @Override
    public StoreReviewVO getById(Long id) {
        StoreReview review = reviewMapper.selectById(id);
        return convertToVO(review);
    }

    @Override
    public void add(StoreReview review) {
        reviewMapper.insert(review);
    }

    @Override
    public void reply(Long id, String reply) {
        StoreReview review = new StoreReview();
        review.setId(id);
        review.setReply(reply);
        reviewMapper.updateById(review);
    }

    @Override
    public void delete(Long id) {
        reviewMapper.deleteById(id);
    }

    private StoreReviewVO convertToVO(StoreReview review) {
        if (review == null) {
            return null;
        }
        StoreReviewVO vo = new StoreReviewVO();
        BeanUtils.copyProperties(review, vo);
        
        // 查询门店名称
        if (review.getStoreId() != null) {
            Store store = storeMapper.selectById(review.getStoreId());
            if (store != null) {
                vo.setStoreName(store.getName());
            }
        }
        
        // 查询用户昵称（通过RestTemplate调用用户服务）
        if (review.getUserId() != null) {
            try {
                if (restTemplate != null) {
                    // 尝试调用用户服务获取用户信息
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
                // 如果用户服务不可用，显示默认名称
                vo.setUserNickname("用户" + review.getUserId());
            }
        } else {
            vo.setUserNickname("匿名用户");
        }
        
        return vo;
    }
}
