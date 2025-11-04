package com.murder.reservation.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置类
 */
@Configuration
@Slf4j
public class MybatisPlusConfig {

    /**
     * 分页插件配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        log.info("配置 MyBatis Plus 分页插件");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 使用反射创建分页插件，避免编译时找不到类
        try {
            Class<?> paginationClass = Class.forName("com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor");
            Object paginationInterceptor = paginationClass.getConstructor(DbType.class).newInstance(DbType.MYSQL);
            
            // 设置最大单页限制数量
            paginationClass.getMethod("setMaxLimit", Long.class).invoke(paginationInterceptor, 1000L);
            // 设置溢出处理
            paginationClass.getMethod("setOverflow", boolean.class).invoke(paginationInterceptor, false);
            
            // 添加到拦截器
            interceptor.addInnerInterceptor((com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor) paginationInterceptor);
            
            log.info("MyBatis Plus 分页插件配置完成");
        } catch (Exception e) {
            log.error("配置 MyBatis Plus 分页插件失败", e);
        }
        
        return interceptor;
    }
}
