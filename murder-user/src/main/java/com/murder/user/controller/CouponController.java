package com.murder.user.controller;

import com.murder.common.result.PageResult;
import com.murder.common.result.Result;
import com.murder.pojo.dto.CouponDTO;
import com.murder.pojo.entity.Coupon;
import com.murder.pojo.entity.UserCoupon;
import com.murder.pojo.vo.CouponVO;
import com.murder.pojo.vo.UserCouponVO;
import com.murder.user.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券控制器
 */
@RestController
@RequestMapping("/coupon")
@Tag(name = "优惠券接口")
@Slf4j
public class CouponController {

    @Autowired
    private CouponService couponService;

    /**
     * 分页查询优惠券列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询优惠券列表")
    public Result<PageResult<CouponVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        log.info("分页查询优惠券列表: page={}, pageSize={}, name={}, type={}, status={}", 
                page, pageSize, name, type, status);
        PageResult<CouponVO> pageResult = couponService.pageQuery(page, pageSize, name, type, status);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询优惠券详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询优惠券详情")
    public Result<CouponVO> getById(@PathVariable Long id) {
        log.info("查询优惠券详情: {}", id);
        CouponVO coupon = couponService.getById(id);
        return Result.success(coupon);
    }
    
    /**
     * 获取优惠券统计信息
     */
    @GetMapping("/{id}/statistics")
    @Operation(summary = "获取优惠券统计信息")
    public Result<CouponVO> getStatistics(@PathVariable Long id) {
        log.info("获取优惠券统计信息: {}", id);
        CouponVO statistics = couponService.getCouponStatistics(id);
        return Result.success(statistics);
    }

    /**
     * 新增优惠券
     */
    @PostMapping
    @Operation(summary = "新增优惠券")
    public Result<String> add(@RequestBody CouponDTO couponDTO) {
        log.info("新增优惠券: {}", couponDTO);
        couponService.add(couponDTO);
        return Result.success("新增成功");
    }

    /**
     * 更新优惠券
     */
    @PutMapping
    @Operation(summary = "更新优惠券")
    public Result<String> update(@RequestBody CouponDTO couponDTO) {
        log.info("更新优惠券: {}", couponDTO);
        couponService.update(couponDTO);
        return Result.success("更新成功");
    }

    /**
     * 删除优惠券
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除优惠券")
    public Result<String> delete(@PathVariable Long id) {
        log.info("删除优惠券: {}", id);
        couponService.delete(id);
        return Result.success("删除成功");
    }
    
    /**
     * 上架/下架优惠券
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "上架/下架优惠券")
    public Result<String> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        log.info("更新优惠券状态: id={}, status={}", id, status);
        couponService.updateStatus(id, status);
        return Result.success("更新成功");
    }

    /**
     * 用户领取优惠券
     */
    @PostMapping("/receive")
    @Operation(summary = "用户领取优惠券")
    public Result<String> receiveCoupon(@RequestParam Long couponId) {
        // 从上下文获取当前登录用户ID
        Long userId = com.murder.common.context.BaseContext.getCurrentId();
        log.info("用户领取优惠券: userId={}, couponId={}", userId, couponId);
        couponService.receiveCoupon(userId, couponId);
        return Result.success("领取成功");
    }

    /**
     * 查询当前用户的优惠券列表
     */
    @GetMapping("/user")
    @Operation(summary = "查询当前用户的优惠券列表")
    public Result<PageResult<UserCouponVO>> getUserCoupons(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        // 从上下文获取当前登录用户ID
        Long userId = com.murder.common.context.BaseContext.getCurrentId();
        log.info("查询用户的优惠券列表: userId={}, page={}, pageSize={}, status={}", 
                userId, page, pageSize, status);
        PageResult<UserCouponVO> pageResult = couponService.getUserCoupons(userId, page, pageSize, status);
        return Result.success(pageResult);
    }
    
    /**
     * 查询用户可用的优惠券列表
     */
    @GetMapping("/user/{userId}/available")
    @Operation(summary = "查询用户可用的优惠券列表")
    public Result<List<UserCouponVO>> getAvailableCoupons(
            @PathVariable Long userId,
            @RequestParam(required = false) BigDecimal orderAmount) {
        log.info("查询用户可用的优惠券: userId={}, orderAmount={}", userId, orderAmount);
        List<UserCouponVO> coupons = couponService.getAvailableCoupons(userId, orderAmount);
        return Result.success(coupons);
    }

    /**
     * 计算优惠金额
     */
    @GetMapping("/calculate")
    @Operation(summary = "计算优惠金额")
    public Result<BigDecimal> calculateDiscount(
            @RequestParam Long userCouponId,
            @RequestParam BigDecimal orderAmount) {
        log.info("计算优惠金额: userCouponId={}, orderAmount={}", userCouponId, orderAmount);
        BigDecimal discount = couponService.calculateDiscount(userCouponId, orderAmount);
        return Result.success(discount);
    }

    /**
     * 使用优惠券
     */
    @PutMapping("/use")
    @Operation(summary = "使用优惠券")
    public Result<String> useCoupon(
            @RequestParam Long userCouponId,
            @RequestParam Long orderId) {
        log.info("使用优惠券: userCouponId={}, orderId={}", userCouponId, orderId);
        couponService.useCoupon(userCouponId, orderId);
        return Result.success("使用成功");
    }
    
    /**
     * 退还优惠券
     */
    @PutMapping("/refund")
    @Operation(summary = "退还优惠券")
    public Result<String> refundCoupon(@RequestParam Long orderId) {
        log.info("退还优惠券: orderId={}", orderId);
        couponService.refundCoupon(orderId);
        return Result.success("退还成功");
    }
    
    /**
     * 批量过期优惠券
     */
    @PostMapping("/expire")
    @Operation(summary = "批量过期优惠券")
    public Result<String> expireCoupons() {
        log.info("批量过期优惠券");
        couponService.expireCoupons();
        return Result.success("操作成功");
    }
    
    /**
     * 获取预约可用的优惠券列表
     */
    @GetMapping("/available/reservation/{reservationId}")
    @Operation(summary = "获取预约可用的优惠券列表")
    public Result<List<UserCouponVO>> getAvailableCouponsForReservation(
            @PathVariable Long reservationId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) BigDecimal orderAmount) {
        log.info("获取预约可用的优惠券: reservationId={}, userId={}, orderAmount={}", 
                reservationId, userId, orderAmount);
        
        // 从当前登录用户上下文获取userId
        Long currentUserId = userId;
        if (currentUserId == null) {
            try {
                currentUserId = com.murder.common.context.BaseContext.getCurrentId();
            } catch (Exception e) {
                log.warn("无法获取当前登录用户ID，返回空优惠券列表");
                return Result.success(new java.util.ArrayList<>());
            }
        }
        
        // 查询用户的可用优惠券
        List<UserCouponVO> coupons = couponService.getAvailableCoupons(currentUserId, orderAmount);
        return Result.success(coupons);
    }
}
