package com.murder.common.feign;

import com.murder.common.result.Result;
import com.murder.pojo.entity.User;
import com.murder.pojo.vo.UserLoginVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务Feign客户端
 * 
 * 用于其他服务调用用户服务的接口
 * 
 * @author Murder System
 * @date 2025-11-05
 */
@FeignClient(name = "murder-user", path = "/user")
public interface UserFeignClient {
    
    /**
     * 根据用户ID获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    Result<User> getUserById(@PathVariable("id") Long userId);
    
    /**
     * 根据用户ID获取用户基础信息（内部调用）
     * 
     * @param userId 用户ID
     * @return 用户基础信息
     */
    @GetMapping("/info/{id}")
    Result<UserLoginVO> getUserInfo(@PathVariable("id") Long userId);
    
    /**
     * 批量获取用户信息
     * 
     * @param userIds 用户ID列表
     * @return 用户信息列表
     */
    @PostMapping("/batch")
    Result getUsersByIds(@RequestBody Long[] userIds);
    
    /**
     * 计算优惠券折扣
     * 
     * @param userCouponId 用户优惠券ID
     * @param orderAmount 订单金额
     * @return 折扣金额
     */
    @GetMapping("/coupon/calculate")
    Result calculateCouponDiscount(
            @RequestParam("userCouponId") Long userCouponId,
            @RequestParam("orderAmount") java.math.BigDecimal orderAmount
    );
    
    /**
     * 使用优惠券
     * 
     * @param userCouponId 用户优惠券ID
     * @param orderId 订单ID
     * @return 是否成功
     */
    @PutMapping("/coupon/use")
    Result<Boolean> useCoupon(
            @RequestParam("userCouponId") Long userCouponId,
            @RequestParam("orderId") Long orderId
    );
    
    /**
     * 退还优惠券
     * 
     * @param orderId 订单ID
     * @return 是否成功
     */
    @PutMapping("/coupon/refund")
    Result<Boolean> refundCoupon(@RequestParam("orderId") Long orderId);
    
    /**
     * 预约奖励积分
     * 
     * @param userId 用户ID
     * @param reservationId 预约ID
     * @return 是否成功
     */
    @PostMapping("/user/points/reward/reservation")
    Result<Boolean> rewardPointsForReservation(
            @RequestParam("userId") Long userId,
            @RequestParam("reservationId") Long reservationId
    );
    
    /**
     * 退款扣除积分
     * 
     * @param userId 用户ID
     * @param reservationId 预约ID
     * @return 是否成功
     */
    @PostMapping("/user/points/deduct/refund")
    Result<Boolean> deductPointsForRefund(
            @RequestParam("userId") Long userId,
            @RequestParam("reservationId") Long reservationId
    );
    
    /**
     * 增加用户积分
     * 
     * @param userId 用户ID
     * @param points 积分数
     * @param description 描述
     * @return 是否成功
     */
    @PostMapping("/user/points/add")
    Result<Boolean> addPoints(
            @RequestParam("userId") Long userId,
            @RequestParam("points") Integer points,
            @RequestParam("description") String description
    );
    
    /**
     * 发送通知
     * 
     * @param notificationData 通知数据
     * @return 是否成功
     */
    @PostMapping("/notification/send")
    Result<Boolean> sendNotification(@RequestBody java.util.Map<String, Object> notificationData);
}

