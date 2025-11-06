package com.murder.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 门店服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.murder.common.feign")  // 启用OpenFeign客户端，明确指定扫描路径
@ComponentScan(basePackages = {"com.murder.store", "com.murder.common"})
public class StoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreApplication.class, args);
        System.out.println("==========================================");
        System.out.println("门店服务启动成功！");
        System.out.println("访问地址: http://localhost:8083");
        System.out.println("API文档: http://localhost:8083/doc.html");
        System.out.println("==========================================");
    }
}
