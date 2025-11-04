package com.murder.reservation.service;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 创建支付宝支付订单
     * @param reservationId 预约ID
     * @param paymentMethod 支付方式（alipay/wechat/mock）
     * @return 支付表单HTML或支付URL
     */
    String createPayment(Long reservationId, String paymentMethod);
    
    /**
     * 支付宝异步通知处理
     * @param params 支付宝回调参数
     * @return 处理结果
     */
    String handleAlipayNotify(java.util.Map<String, String> params);
    
    /**
     * 查询支付状态
     * @param reservationId 预约ID
     * @return 支付状态
     */
    Integer queryPaymentStatus(Long reservationId);
}

