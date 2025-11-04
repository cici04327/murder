package com.murder.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * 预约服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.murder.reservation", "com.murder.common"})
public class ReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationApplication.class, args);
        System.out.println("==========================================");
        System.out.println("预约服务启动成功！");
        System.out.println("访问地址: http://localhost:8085");
        System.out.println("API文档: http://localhost:8085/doc.html");
        System.out.println("==========================================");
    }
}
