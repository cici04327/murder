package com.murder.reservation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.murder.common.context.BaseContext;
import com.murder.common.result.PageResult;
import com.murder.pojo.dto.ReviewDTO;
import com.murder.pojo.entity.Review;
import com.murder.pojo.vo.ReviewStatisticsVO;
import com.murder.pojo.vo.ReviewVO;
import com.murder.reservation.mapper.ReviewMapper;
import com.murder.reservation.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 评价服务实现类
 */
@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;
    
    @Autowired(required = false)
    private RestTemplate restTemplate;
    
    private static final String USER_SERVICE_URL = "http://localhost:8082";

    /**
     * 创建评价
     */
    @Override
    @Transactional
    public void create(ReviewDTO reviewDTO) {
        Review review = new Review();
        BeanUtils.copyProperties(reviewDTO, review);
        
        // 设置用户ID
        Long userId = BaseContext.getCurrentId();
        if (userId != null) {
            review.setUserId(userId);
        }
        
        // 默认状态：待审核
        review.setStatus(1);
        
        // 设置奖励积分 - 发表评价获得50积分
        int rewardPoints = 50; // 基础积分
        if (reviewDTO.getImages() != null && !reviewDTO.getImages().isEmpty()) {
            rewardPoints += 10; // 上传图片额外积分
        }
        review.setRewardPoints(rewardPoints);
        
        reviewMapper.insert(review);
        
        // 发放积分到用户账户
        if (userId != null) {
            try {
                addPointsForReview(userId, rewardPoints);
                log.info("用户{}发表评价，获得{}积分", userId, rewardPoints);
            } catch (Exception e) {
                log.error("发放评价积分失败", e);
                // 积分发放失败不影响评价创建
            }
        }
        
        // TODO: 更新门店和剧本的平均评分
    }
    
    /**
     * 发放评价积分
     */
    private void addPointsForReview(Long userId, Integer points) {
        if (restTemplate == null) {
            log.warn("RestTemplate未配置，无法调用积分服务");
            return;
        }
        
        try {
            String url = USER_SERVICE_URL + "/user/points/add?userId=" + userId 
                    + "&points=" + points + "&reason=发表评价";
            restTemplate.postForObject(url, null, String.class);
        } catch (Exception e) {
            log.error("调用积分服务失败", e);
            throw e;
        }
    }

    /**
     * 分页查询评价列表
     */
    @Override
    public PageResult<ReviewVO> pageQuery(Integer page, Integer pageSize, Long storeId, Long scriptId, Long userId, Integer status) {
        // 先查询总数
        LambdaQueryWrapper<Review> countWrapper = new LambdaQueryWrapper<>();
        if (storeId != null) {
            countWrapper.eq(Review::getStoreId, storeId);
        }
        if (scriptId != null) {
            countWrapper.eq(Review::getScriptId, scriptId);
        }
        if (userId != null) {
            countWrapper.eq(Review::getUserId, userId);
        }
        if (status != null) {
            countWrapper.eq(Review::getStatus, status);
        }
        Long total = reviewMapper.selectCount(countWrapper);
        
        // 再查询分页数据
        Page<Review> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        if (storeId != null) {
            wrapper.eq(Review::getStoreId, storeId);
        }
        if (scriptId != null) {
            wrapper.eq(Review::getScriptId, scriptId);
        }
        if (userId != null) {
            wrapper.eq(Review::getUserId, userId);
        }
        if (status != null) {
            wrapper.eq(Review::getStatus, status);
        }
        wrapper.orderByDesc(Review::getCreateTime);
        
        reviewMapper.selectPage(pageInfo, wrapper);
        
        List<ReviewVO> voList = new ArrayList<>();
        for (Review review : pageInfo.getRecords()) {
            try {
                ReviewVO vo = convertToVO(review);
                if (vo != null) {
                    voList.add(vo);
                }
            } catch (Exception e) {
                // 记录错误但不中断处理
                log.error("转换ReviewVO失败: reviewId={}, error={}", review.getId(), e.getMessage(), e);
            }
        }
        
        return new PageResult<>(total, voList);
    }

    /**
     * 根据ID查询评价详情
     */
    @Override
    public ReviewVO getById(Long id) {
        Review review = reviewMapper.selectById(id);
        return convertToVO(review);
    }

    /**
     * 根据预约ID查询评价
     */
    @Override
    public Review getByReservationId(Long reservationId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getReservationId, reservationId);
        return reviewMapper.selectOne(wrapper);
    }

    /**
     * 删除评价
     */
    @Override
    public void delete(Long id) {
        reviewMapper.deleteById(id);
    }

    /**
     * 审核评价
     */
    @Override
    @Transactional
    public void audit(Long id, Integer status, String reason) {
        Review review = new Review();
        review.setId(id);
        review.setStatus(status);
        review.setAuditReason(reason);
        review.setAuditTime(LocalDateTime.now());
        
        Long auditUserId = BaseContext.getCurrentId();
        if (auditUserId != null) {
            review.setAuditUserId(auditUserId);
        }
        
        reviewMapper.updateById(review);
    }

    /**
     * 回复评价
     */
    @Override
    public void reply(Long id, String replyContent) {
        Review review = new Review();
        review.setId(id);
        review.setReplyContent(replyContent);
        review.setReplyTime(LocalDateTime.now());
        reviewMapper.updateById(review);
    }

    /**
     * 设置精选评价
     */
    @Override
    public void setFeatured(Long id, Integer isFeatured) {
        Review review = new Review();
        review.setId(id);
        review.setIsFeatured(isFeatured);
        reviewMapper.updateById(review);
        
        // 如果设置为精选，额外奖励
        if (isFeatured == 1) {
            Review existingReview = reviewMapper.selectById(id);
            if (existingReview != null) {
                review.setRewardPoints(existingReview.getRewardPoints() + 50);
                reviewMapper.updateById(review);
                // TODO: 发放精选奖励优惠券
            }
        }
    }

    /**
     * 获取评价统计信息
     */
    @Override
    public ReviewStatisticsVO getStatistics(Long storeId, Long scriptId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        if (storeId != null) {
            wrapper.eq(Review::getStoreId, storeId);
        }
        if (scriptId != null) {
            wrapper.eq(Review::getScriptId, scriptId);
        }
        wrapper.eq(Review::getStatus, 2); // 只统计已通过的评价
        
        List<Review> reviews = reviewMapper.selectList(wrapper);
        
        if (reviews.isEmpty()) {
            return ReviewStatisticsVO.builder()
                    .totalCount(0L)
                    .averageRating(BigDecimal.ZERO)
                    .goodReviews(0)
                    .mediumReviews(0)
                    .badReviews(0)
                    .goodRate(BigDecimal.ZERO)
                    .build();
        }
        
        int total = reviews.size();
        
        // 计算平均评分
        double avgRating = reviews.stream()
                .mapToInt(Review::getOverallRating)
                .average()
                .orElse(0.0);
        
        // 统计好评、中评、差评
        int goodReviews = (int) reviews.stream().filter(r -> r.getOverallRating() >= 4).count();
        int mediumReviews = (int) reviews.stream().filter(r -> r.getOverallRating() == 3).count();
        int badReviews = (int) reviews.stream().filter(r -> r.getOverallRating() < 3).count();
        
        // 计算好评率
        BigDecimal goodRate = total > 0 ? 
                BigDecimal.valueOf(goodReviews)
                    .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)) 
                : BigDecimal.ZERO;
        
        return ReviewStatisticsVO.builder()
                .totalCount((long) total)
                .averageRating(BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP))
                .goodReviews(goodReviews)
                .mediumReviews(mediumReviews)
                .badReviews(badReviews)
                .goodRate(goodRate)
                .build();
    }

    /**
     * 转换为VO
     */
    private ReviewVO convertToVO(Review review) {
        if (review == null) {
            return null;
        }
        
        ReviewVO vo = new ReviewVO();
        
        // 简单复制基本属性
        org.springframework.beans.BeanUtils.copyProperties(review, vo);
        
        // 映射ID字段
        vo.setReservationId(review.getReservationId());
        vo.setIsFeatured(review.getIsFeatured());
        
        // TODO: 查询用户昵称、门店名称、剧本名称等
        
        return vo;
    }
}
