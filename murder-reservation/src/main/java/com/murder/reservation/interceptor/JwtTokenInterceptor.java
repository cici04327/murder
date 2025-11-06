package com.murder.reservation.interceptor;

import com.murder.common.context.BaseContext;
import com.murder.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT Token 拦截器
 */
@Component
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头中获取token
        String token = request.getHeader("token");
        
        if (token != null && !token.isEmpty()) {
            try {
                // 解析token
                Claims claims = JwtUtil.parseJWT("murder-user-secret-key-2024-secure-key-for-jwt", token);
                
                // 获取用户ID
                Long userId = Long.valueOf(claims.get("userId").toString());
                
                // 设置到ThreadLocal中
                BaseContext.setCurrentId(userId);
                
                log.debug("JWT拦截器：设置当前用户ID={}", userId);
                
                return true;
            } catch (Exception e) {
                log.error("JWT解析失败: {}", e.getMessage());
                // 解析失败也继续，让业务层判断是否需要登录
                return true;
            }
        }
        
        // 没有token也继续，让业务层判断
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清除ThreadLocal，防止内存泄漏
        BaseContext.removeCurrentId();
    }
}

