package com.murder.reservation.config;

import com.murder.reservation.interceptor.JwtTokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 */
@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtTokenInterceptor jwtTokenInterceptor;

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("注册JWT拦截器");
        
        registry.addInterceptor(jwtTokenInterceptor)
                .addPathPatterns("/reservation/**")
                .excludePathPatterns(
                        "/reservation/payment/notify",  // 支付宝回调接口
                        "/reservation/check-availability"  // 房间可用性检查
                );
    }
}

