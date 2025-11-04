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
}

