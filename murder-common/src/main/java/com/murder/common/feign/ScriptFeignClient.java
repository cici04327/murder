package com.murder.common.feign;

import com.murder.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * 剧本服务Feign客户端
 * 
 * 用于其他服务调用剧本服务的接口
 * 
 * @author Murder System
 * @date 2025-11-05
 */
@FeignClient(name = "murder-script", path = "/script")
public interface ScriptFeignClient {
    
    /**
     * 根据剧本ID获取剧本信息
     * 
     * @param scriptId 剧本ID
     * @return 剧本信息
     */
    @GetMapping("/{id}")
    Result getScriptById(@PathVariable("id") Long scriptId);
    
    /**
     * 增加剧本预约次数
     * 
     * @param scriptId 剧本ID
     * @return 是否成功
     */
    @PutMapping("/{id}/increment-booking")
    Result<Boolean> incrementBookingCount(@PathVariable("id") Long scriptId);
    
    /**
     * 检查剧本是否可用
     * 
     * @param scriptId 剧本ID
     * @return 是否可用
     */
    @GetMapping("/{id}/available")
    Result<Boolean> checkScriptAvailable(@PathVariable("id") Long scriptId);
}

