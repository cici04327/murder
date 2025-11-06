package com.murder.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 预约服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.murder.common.feign")  // 启用OpenFeign客户端，明确指定扫描路径
@EnableScheduling
@ComponentScan(basePackages = {"com.murder.reservation", "com.murder.common"})
public class ReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationApplication.class, args);
        System.out.println("==========================================");
        System.out.println("预约服务启动成功！");
        System.out.println("访问地址: http://localhost:8085");
        System.out.println("API文档: http://localhost:8085/doc.html");
        System.out.println("定时任务：自动完成订单（每小时执行）");
        System.out.println("定时任务：自动取消超时未支付（每30分钟执行）");
        System.out.println("==========================================");
    }
}
