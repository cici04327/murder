package com.murder.common.feign;

import com.murder.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 门店服务Feign客户端
 * 
 * 用于其他服务调用门店服务的接口
 * 
 * @author Murder System
 * @date 2025-11-05
 */
@FeignClient(name = "murder-store", path = "/store")
public interface StoreFeignClient {
    
    /**
     * 根据门店ID获取门店信息
     * 
     * @param storeId 门店ID
     * @return 门店信息
     */
    @GetMapping("/{id}")
    Result getStoreById(@PathVariable("id") Long storeId);
    
    /**
     * 检查房间是否可用
     * 
     * @param roomId 房间ID
     * @return 是否可用
     */
    @GetMapping("/room/available/{roomId}")
    Result<Boolean> checkRoomAvailable(@PathVariable("roomId") Long roomId);
    
    /**
     * 锁定房间（预约时使用）
     * 
     * @param roomId 房间ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 是否锁定成功
     */
    @PostMapping("/room/lock")
    Result<Boolean> lockRoom(
            @RequestParam("roomId") Long roomId,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime
    );
    
    /**
     * 根据房间ID获取房间详情
     * 
     * @param roomId 房间ID
     * @return 房间详情
     */
    @GetMapping("/room/detail/{roomId}")
    Result getRoomDetail(@PathVariable("roomId") Long roomId);
}

