package com.murder.store.interceptor;

import com.murder.common.constant.JwtClaimsConstant;
import com.murder.common.context.BaseContext;
import com.murder.common.properties.JwtProperties;
import com.murder.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT令牌校验拦截器
 */
@Component
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            // 当前拦截到的不是动态方法，直接放行
            return true;
        }

        String method = request.getMethod();
        String path = request.getRequestURI();
        
        log.info("请求方法: {}, 请求路径: {}", method, path);
        
        // GET 请求的查询接口都放行（公开访问）
        if ("GET".equals(method)) {
            log.info("GET请求，直接放行");
            return true;
        }
        
        // POST 请求的查询接口也放行（公开访问）
        if ("POST".equals(method) && (
            path.contains("/page/advanced") ||  // 高级查询接口
            path.contains("/search") ||          // 搜索接口
            path.contains("/query")              // 查询接口
        )) {
            log.info("POST 查询请求，直接放行");
            return true;
        }
        
        // POST 发布评价需要登录
        if ("POST".equals(method) && path.contains("/store/review")) {
            log.info("POST 评价请求，需要验证token");
            // 继续执行token验证
        } else if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
            // 其他增删改操作需要登录
            log.info("增删改操作，需要验证token");
        }

        // 1、从请求头中获取令牌
        String userTokenName = jwtProperties.getUserTokenName();
        String adminTokenName = jwtProperties.getAdminTokenName();
        
        log.info("期望的token请求头名称: user={}, admin={}", userTokenName, adminTokenName);
        
        String token = request.getHeader(userTokenName);
        log.info("从请求头 '{}' 获取到的token: {}", userTokenName, token);
        
        // 如果user-token为空，尝试获取admin-token
        if (token == null || token.isEmpty()) {
            token = request.getHeader(adminTokenName);
            log.info("从请求头 '{}' 获取到的token: {}", adminTokenName, token);
        }
        
        log.info("JWT拦截器执行，token: {}", token != null ? "存在" : "不存在");

        // 2、校验令牌
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("当前用户id：{}", userId);
            
            // 3、通过ThreadLocal设置当前登录用户id
            BaseContext.setCurrentId(userId);
            
            // 放行
            return true;
        } catch (Exception ex) {
            log.error("JWT令牌校验失败：{}", ex.getMessage());
            
            // 尝试使用admin密钥解析
            try {
                Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
                Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
                log.info("使用admin密钥解析成功，当前用户id：{}", userId);
                
                BaseContext.setCurrentId(userId);
                return true;
            } catch (Exception e) {
                log.error("使用admin密钥解析也失败：{}", e.getMessage());
                
                // 401 响应
                response.setStatus(401);
                return false;
            }
        }
    }
}
