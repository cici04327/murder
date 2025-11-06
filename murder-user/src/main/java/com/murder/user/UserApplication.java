package com.murder.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 用户服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.murder.common.feign")  // 启用OpenFeign客户端，明确指定扫描路径
@EnableScheduling
@ComponentScan(basePackages = {"com.murder.user", "com.murder.common"})
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
        System.out.println("==========================================");
        System.out.println("用户服务启动成功！");
        System.out.println("访问地址: http://localhost:8082");
        System.out.println("API文档: http://localhost:8082/doc.html");
        System.out.println("消息通知定时任务已启用");
        System.out.println("==========================================");
    }
}
