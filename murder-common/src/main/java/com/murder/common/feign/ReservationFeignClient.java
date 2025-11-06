package com.murder.common.feign;

import com.murder.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 预约服务Feign客户端
 * 
 * 用于其他服务调用预约服务的接口
 * 
 * @author Murder System
 * @date 2025-11-05
 */
@FeignClient(name = "murder-reservation", path = "/reservation")
public interface ReservationFeignClient {
    
    /**
     * 根据预约ID获取预约信息
     * 
     * @param reservationId 预约ID
     * @return 预约信息
     */
    @GetMapping("/{id}")
    Result getReservationById(@PathVariable("id") Long reservationId);
    
    /**
     * 获取用户的预约数量
     * 
     * @param userId 用户ID
     * @return 预约数量
     */
    @GetMapping("/count/user/{userId}")
    Result<Integer> getUserReservationCount(@PathVariable("userId") Long userId);
    
    /**
     * 检查用户是否有未完成的预约
     * 
     * @param userId 用户ID
     * @return 是否有未完成预约
     */
    @GetMapping("/check/pending/{userId}")
    Result<Boolean> hasPendingReservation(@PathVariable("userId") Long userId);
}

