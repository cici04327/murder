package com.murder.reservation.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.murder.pojo.entity.Reservation;
import com.murder.reservation.config.AlipayConfig;
import com.murder.reservation.mapper.ReservationMapper;
import com.murder.reservation.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 支付服务实现类
 */
@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private ReservationMapper reservationMapper;
    
    @Autowired
    private AlipayClient alipayClient;
    
    @Autowired
    private AlipayConfig alipayConfig;
    
    @Autowired(required = false)
    private org.springframework.web.client.RestTemplate restTemplate;
    
    @Autowired(required = false)
    private com.murder.common.feign.UserFeignClient userFeignClient;

    /**
     * 创建支付订单
     */
    @Override
    public String createPayment(Long reservationId, String paymentMethod) {
        // 查询预约信息
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        
        if (reservation.getPayStatus() == 1) {
            throw new RuntimeException("订单已支付");
        }
        
        // 根据支付方式处理
        if ("mock".equals(paymentMethod)) {
            // 模拟支付：直接标记为已支付
            return handleMockPayment(reservation);
        } else if ("alipay".equals(paymentMethod)) {
            // 支付宝支付
            if (alipayConfig.getMockPayment()) {
                // 开发环境：模拟支付宝支付
                return handleMockAlipayPayment(reservation);
            } else {
                // 生产环境：真实支付宝支付
                return createAlipayPayment(reservation);
            }
        } else if ("wechat".equals(paymentMethod)) {
            // 微信支付（暂未实现）
            return handleMockPayment(reservation);
        }
        
        throw new RuntimeException("不支持的支付方式");
    }

    /**
     * 模拟支付
     */
    private String handleMockPayment(Reservation reservation) {
        // 更新支付状态
        reservation.setPayStatus(1);
        reservation.setPayTime(LocalDateTime.now());
        reservation.setStatus(2); // 已确认
        reservationMapper.updateById(reservation);
        
        log.info("模拟支付成功: reservationId={}, orderNo={}", 
                reservation.getId(), reservation.getOrderNo());
        
        // 支付成功后奖励积分
        rewardPointsForPayment(reservation.getUserId(), reservation.getId());
        
        return "MOCK_PAY_SUCCESS";
    }
    
    /**
     * 模拟支付宝支付
     */
    private String handleMockAlipayPayment(Reservation reservation) {
        // 开发环境：直接返回成功，实际项目中可以返回一个模拟支付页面
        reservation.setPayStatus(1);
        reservation.setPayTime(LocalDateTime.now());
        reservation.setStatus(2); // 已确认
        reservationMapper.updateById(reservation);
        
        log.info("模拟支付宝支付成功: reservationId={}, orderNo={}", 
                reservation.getId(), reservation.getOrderNo());
        
        // 支付成功后奖励积分
        rewardPointsForPayment(reservation.getUserId(), reservation.getId());
        
        return "ALIPAY_MOCK_SUCCESS";
    }

    /**
     * 创建真实支付宝支付
     */
    private String createAlipayPayment(Reservation reservation) {
        try {
            // 创建API请求对象
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            
            // 设置回调地址
            request.setNotifyUrl(alipayConfig.getNotifyUrl());
            request.setReturnUrl(alipayConfig.getReturnUrl());
            
            // 设置业务参数
            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            model.setOutTradeNo(reservation.getOrderNo()); // 商户订单号
            model.setTotalAmount(reservation.getActualAmount().toString()); // 订单金额
            model.setSubject("剧本杀预约 - " + reservation.getOrderNo()); // 订单标题
            model.setBody("预约时间：" + reservation.getReservationTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))); // 订单描述
            model.setProductCode("FAST_INSTANT_TRADE_PAY"); // 产品码
            
            request.setBizModel(model);
            
            // 调用SDK生成支付表单
            String form = alipayClient.pageExecute(request).getBody();
            
            log.info("创建支付宝支付订单成功: reservationId={}, orderNo={}", 
                    reservation.getId(), reservation.getOrderNo());
            
            return form;
        } catch (AlipayApiException e) {
            log.error("创建支付宝支付订单失败", e);
            throw new RuntimeException("创建支付订单失败: " + e.getMessage());
        }
    }

    /**
     * 处理支付宝异步通知
     */
    @Override
    public String handleAlipayNotify(Map<String, String> params) {
        try {
            // TODO: 验证签名
            String tradeStatus = params.get("trade_status");
            String outTradeNo = params.get("out_trade_no");
            
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                // 查询订单
                Reservation reservation = reservationMapper.selectOne(
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Reservation>()
                                .eq(Reservation::getOrderNo, outTradeNo)
                );
                
                if (reservation != null && reservation.getPayStatus() == 0) {
                    // 更新支付状态
                    reservation.setPayStatus(1);
                    reservation.setPayTime(LocalDateTime.now());
                    reservation.setStatus(2); // 已确认
                    reservationMapper.updateById(reservation);
                    
                    log.info("支付宝支付成功: orderNo={}", outTradeNo);
                    
                    // 支付成功后奖励积分
                    rewardPointsForPayment(reservation.getUserId(), reservation.getId());
                }
            }
            
            return "success";
        } catch (Exception e) {
            log.error("处理支付宝回调失败", e);
            return "fail";
        }
    }

    /**
     * 查询支付状态
     */
    @Override
    public Integer queryPaymentStatus(Long reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        return reservation != null ? reservation.getPayStatus() : null;
    }
    
    /**
     * 支付成功后奖励积分
     */
    private void rewardPointsForPayment(Long userId, Long reservationId) {
        if (restTemplate == null) {
            log.error("RestTemplate未注入，无法调用积分服务");
            return;
        }
        
        try {
            // 调用用户服务的积分接口（用户服务端口：8082）
            if (userFeignClient != null) {
                userFeignClient.rewardPointsForReservation(userId, reservationId);
                log.info("支付成功奖励积分成功: userId={}, reservationId={}", userId, reservationId);
            } else if (restTemplate != null) {
                String url = "http://localhost:8082/user/points/reward/reservation?userId=" + userId + "&reservationId=" + reservationId;
                String result = restTemplate.postForObject(url, null, String.class);
                log.info("支付成功奖励积分成功: userId={}, reservationId={}, result={}", userId, reservationId, result);
            }
        } catch (Exception e) {
            log.error("奖励积分失败，但不影响支付流程: userId={}, reservationId={}, error={}", userId, reservationId, e.getMessage(), e);
        }
    }
    
    /**
     * 申请退款（用户端）
     */
    @Override
    public void applyRefund(Long reservationId, String reason) {
        // 查询预约信息
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        
        // 检查支付状态
        if (reservation.getPayStatus() != 1) {
            throw new RuntimeException("订单未支付或已退款，无法申请退款");
        }
        
        // 检查预约状态
        if (reservation.getStatus() == 3) {
            throw new RuntimeException("预约已完成，无法申请退款");
        }
        
        // 检查是否已经在退款中
        if (reservation.getRefundStatus() != null && reservation.getRefundStatus() > 0) {
            if (reservation.getRefundStatus() == 1) {
                throw new RuntimeException("退款申请处理中，请勿重复提交");
            } else if (reservation.getRefundStatus() == 2) {
                throw new RuntimeException("该订单已退款");
            }
        }
        
        // 更新退款信息
        reservation.setRefundReason(reason);
        reservation.setRefundApplyTime(LocalDateTime.now());
        reservation.setRefundStatus(1); // 退款中
        reservation.setPayStatus(2); // 退款中
        reservationMapper.updateById(reservation);
        
        log.info("用户申请退款: reservationId={}, reason={}", reservationId, reason);
    }
    
    /**
     * 处理退款（管理端）
     */
    @Override
    public void processRefund(Long reservationId, Integer approved, String adminRemark) {
        // 查询预约信息
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        
        // 检查退款状态
        if (reservation.getRefundStatus() != 1) {
            throw new RuntimeException("该订单未申请退款或已处理");
        }
        
        reservation.setRefundProcessTime(LocalDateTime.now());
        reservation.setAdminRemark(adminRemark);
        
        if (approved == 1) {
            // 同意退款
            reservation.setRefundStatus(2); // 退款成功
            reservation.setPayStatus(3); // 已退款
            reservation.setStatus(4); // 已取消
            
            log.info("管理员同意退款: reservationId={}", reservationId);
            
            // 扣除已奖励的积分
            deductPointsForRefund(reservation.getUserId(), reservationId);
        } else {
            // 拒绝退款
            reservation.setRefundStatus(3); // 退款失败
            reservation.setPayStatus(1); // 恢复为已支付
            
            log.info("管理员拒绝退款: reservationId={}, reason={}", reservationId, adminRemark);
        }
        
        reservationMapper.updateById(reservation);
    }
    
    /**
     * 退款时扣除积分
     */
    private void deductPointsForRefund(Long userId, Long reservationId) {
        if (restTemplate == null) {
            log.error("RestTemplate未注入，无法调用积分服务");
            return;
        }
        
        try {
            // 调用用户服务扣除积分接口（用户服务端口：8082）
            if (userFeignClient != null) {
                userFeignClient.deductPointsForRefund(userId, reservationId);
                log.info("退款扣除积分成功: userId={}, reservationId={}", userId, reservationId);
            } else if (restTemplate != null) {
                String url = "http://localhost:8082/user/points/deduct/refund?userId=" + userId + "&reservationId=" + reservationId;
                String result = restTemplate.postForObject(url, null, String.class);
                log.info("退款扣除积分成功: userId={}, reservationId={}, result={}", userId, reservationId, result);
            }
        } catch (Exception e) {
            log.error("扣除积分失败，但不影响退款流程: userId={}, reservationId={}, error={}", userId, reservationId, e.getMessage(), e);
        }
    }
}

