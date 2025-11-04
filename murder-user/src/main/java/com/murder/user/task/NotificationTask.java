package com.murder.user.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.murder.pojo.entity.Reservation;
import com.murder.pojo.entity.UserCoupon;
import com.murder.user.mapper.UserCouponMapper;
import com.murder.user.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 通知定时任务
 */
@Slf4j
@Component
public class NotificationTask {

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserCouponMapper userCouponMapper;
    
    @Autowired(required = false)
    private RestTemplate restTemplate;

    /**
     * 预约提醒任务 - 每小时执行一次
     * 提醒2小时后开始的预约
     */
    @Scheduled(cron = "0 0 * * * ?") // 每小时执行
    public void reservationReminder() {
        log.info("开始执行预约提醒任务");
        
        try {
            if (restTemplate == null) {
                log.warn("RestTemplate未配置，跳过预约提醒任务");
                return;
            }
            
            // 调用预约服务查询即将开始的预约
            String url = "http://localhost:8085/reservation/upcoming?hours=2";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.get("data") != null) {
                List<Map<String, Object>> reservations = (List<Map<String, Object>>) response.get("data");
                
                int count = 0;
                // 发送提醒通知
                for (Map<String, Object> reservation : reservations) {
                    try {
                        Long userId = Long.valueOf(reservation.get("userId").toString());
                        Long reservationId = Long.valueOf(reservation.get("id").toString());
                        String reservationTime = reservation.get("reservationTime").toString();
                        String orderNo = reservation.get("orderNo").toString();
                        
                        notificationService.sendToUsers(
                            "预约提醒",
                            String.format("温馨提醒：您的预约（订单号：%s）将在2小时内开始，预约时间：%s，请准时到场！", 
                                    orderNo, reservationTime),
                            2, // 预约提醒
                            "reservation",
                            reservationId,
                            userId
                        );
                        count++;
                    } catch (Exception e) {
                        log.error("发送预约提醒通知失败", e);
                    }
                }
                
                log.info("预约提醒任务执行完成，发送通知数: {}", count);
            }
        } catch (Exception e) {
            log.error("预约提醒任务执行失败", e);
        }
    }

    /**
     * 优惠券到期提醒 - 每天执行一次
     * 提醒3天后到期的优惠券
     */
    @Scheduled(cron = "0 0 9 * * ?") // 每天上午9点执行
    public void couponExpireReminder() {
        log.info("开始执行优惠券到期提醒任务");
        
        try {
            // 查询3天后到期的优惠券（状态为未使用）
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime threeDaysLater = now.plusDays(3);
            
            LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserCoupon::getStatus, 1) // 未使用
                   .le(UserCoupon::getExpireTime, threeDaysLater)
                   .ge(UserCoupon::getExpireTime, now);
            
            List<UserCoupon> userCoupons = userCouponMapper.selectList(wrapper);
            
            int count = 0;
            for (UserCoupon userCoupon : userCoupons) {
                try {
                    // 计算剩余天数
                    long daysLeft = java.time.Duration.between(now, userCoupon.getExpireTime()).toDays();
                    
                    String content;
                    if (daysLeft <= 0) {
                        content = "您的优惠券即将到期（今天到期），请尽快使用！";
                    } else if (daysLeft == 1) {
                        content = "您的优惠券还有1天就要到期了，请尽快使用！";
                    } else {
                        content = String.format("您的优惠券还有%d天就要到期了，请尽快使用！", daysLeft);
                    }
                    
                    notificationService.sendToUsers(
                        "优惠券即将到期",
                        content,
                        3, // 优惠券到期
                        "coupon",
                        userCoupon.getCouponId(),
                        userCoupon.getUserId()
                    );
                    count++;
                } catch (Exception e) {
                    log.error("发送优惠券到期提醒失败", e);
                }
            }
            
            log.info("优惠券到期提醒任务执行完成，发送通知数: {}", count);
        } catch (Exception e) {
            log.error("优惠券到期提醒任务执行失败", e);
        }
    }
}
