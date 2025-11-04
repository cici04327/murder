package com.murder.reservation.controller;

import com.murder.common.result.Result;
import com.murder.reservation.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/reservation/payment")
@Tag(name = "支付接口")
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    @Operation(summary = "创建支付订单")
    public Result<String> createPayment(
            @RequestParam Long reservationId,
            @RequestParam(defaultValue = "mock") String paymentMethod) {
        log.info("创建支付订单: reservationId={}, paymentMethod={}", reservationId, paymentMethod);
        String result = paymentService.createPayment(reservationId, paymentMethod);
        return Result.success(result);
    }

    /**
     * 支付宝异步通知
     */
    @PostMapping("/notify")
    @Operation(summary = "支付宝异步通知")
    public String alipayNotify(@RequestParam Map<String, String> params) {
        log.info("收到支付宝异步通知: {}", params);
        return paymentService.handleAlipayNotify(params);
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/status/{reservationId}")
    @Operation(summary = "查询支付状态")
    public Result<Integer> queryPaymentStatus(@PathVariable Long reservationId) {
        log.info("查询支付状态: reservationId={}", reservationId);
        Integer status = paymentService.queryPaymentStatus(reservationId);
        return Result.success(status);
    }
}

