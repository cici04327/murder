package com.murder.script.config;

import com.murder.script.interceptor.JwtTokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private JwtTokenInterceptor jwtTokenInterceptor;

    /**
     * 注册自定义拦截器
     */
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/script/page")              // 排除剧本列表（公开访问）
                .excludePathPatterns("/script/{id}")              // 排除剧本详情（公开访问）
                .excludePathPatterns("/script/category")          // 排除分类列表（公开访问）
                .excludePathPatterns("/script/{id}/roles")        // 排除角色列表（公开访问）
                .excludePathPatterns("/script/review/page")       // 排除评价列表（公开访问）
                .excludePathPatterns("/script/list/hot")          // 排除热门剧本（公开访问）
                .excludePathPatterns("/script/list/recommended")  // 排除推荐剧本（公开访问）
                .excludePathPatterns("/doc.html")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/v3/api-docs/**");
    }
}
