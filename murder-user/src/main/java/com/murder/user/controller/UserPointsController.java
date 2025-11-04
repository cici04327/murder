package com.murder.user.controller;

import com.murder.common.context.BaseContext;
import com.murder.common.result.PageResult;
import com.murder.common.result.Result;
import com.murder.pojo.entity.UserPointsRecord;
import com.murder.pojo.vo.UserPointsRecordVO;
import com.murder.user.service.UserPointsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户积分控制器
 */
@RestController
@RequestMapping("/user/points")
@Tag(name = "用户积分接口")
@Slf4j
public class UserPointsController {

    @Autowired
    private UserPointsService userPointsService;

    /**
     * 获取当前用户积分信息（含统计）
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前用户积分信息")
    public Result<Map<String, Object>> getPointsInfo() {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            return Result.error("请先登录");
        }
        log.info("获取用户积分信息: userId={}", userId);
        Map<String, Object> info = userPointsService.getPointsInfo(userId);
        return Result.success(info);
    }

    /**
     * 分页查询当前用户积分记录
     */
    @GetMapping("/records")
    @Operation(summary = "分页查询当前用户积分记录")
    public Result<PageResult<UserPointsRecordVO>> getMyRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type) {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            return Result.error("请先登录");
        }
        log.info("分页查询用户积分记录: page={}, pageSize={}, userId={}, type={}", page, pageSize, userId, type);
        PageResult<UserPointsRecordVO> pageResult = userPointsService.pageQuery(page, pageSize, userId, type);
        return Result.success(pageResult);
    }

    /**
     * 分页查询用户积分记录（管理端使用）
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询用户积分记录")
    public Result<PageResult<UserPointsRecordVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam Long userId) {
        log.info("分页查询用户积分记录: page={}, pageSize={}, userId={}", page, pageSize, userId);
        PageResult<UserPointsRecordVO> pageResult = userPointsService.pageQuery(page, pageSize, userId, null);
        return Result.success(pageResult);
    }

    /**
     * 获取用户总积分
     */
    @GetMapping("/total/{userId}")
    @Operation(summary = "获取用户总积分")
    public Result<Integer> getTotalPoints(@PathVariable Long userId) {
        log.info("获取用户总积分: {}", userId);
        Integer totalPoints = userPointsService.getTotalPoints(userId);
        return Result.success(totalPoints);
    }

    /**
     * 增加积分
     */
    @PostMapping("/add")
    @Operation(summary = "增加积分")
    public Result<String> addPoints(
            @RequestParam Long userId,
            @RequestParam Integer points,
            @RequestParam(required = false) String reason) {
        log.info("增加积分: userId={}, points={}, reason={}", userId, points, reason);
        userPointsService.addPoints(userId, points, reason);
        return Result.success("积分增加成功");
    }

    /**
     * 扣减积分
     */
    @PostMapping("/deduct")
    @Operation(summary = "扣减积分")
    public Result<String> deductPoints(
            @RequestParam Long userId,
            @RequestParam Integer points,
            @RequestParam(required = false) String reason) {
        log.info("扣减积分: userId={}, points={}, reason={}", userId, points, reason);
        userPointsService.deductPoints(userId, points, reason);
        return Result.success("积分扣减成功");
    }

    /**
     * 每日签到
     */
    @PostMapping("/sign-in")
    @Operation(summary = "每日签到")
    public Result<String> signIn() {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            return Result.error("请先登录");
        }
        log.info("用户签到: userId={}", userId);
        
        try {
            userPointsService.signIn(userId);
            return Result.success("签到成功！获得10积分");
        } catch (Exception e) {
            log.error("签到失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }


    /**
     * 积分兑换优惠券
     */
    @PostMapping("/exchange-coupon")
    @Operation(summary = "积分兑换优惠券")
    public Result<String> exchangeCoupon(
            @RequestParam Long couponId,
            @RequestParam Integer points) {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            return Result.error("请先登录");
        }
        log.info("兑换优惠券: userId={}, couponId={}, points={}", userId, couponId, points);
        
        try {
            userPointsService.exchangeCoupon(userId, couponId, points);
            return Result.success("兑换成功");
        } catch (Exception e) {
            log.error("兑换失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取任务完成状态
     */
    @GetMapping("/tasks-status")
    @Operation(summary = "获取任务完成状态")
    public Result<Map<String, Object>> getTasksStatus() {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            return Result.error("请先登录");
        }
        log.info("获取任务完成状态: userId={}", userId);
        
        Map<String, Object> tasksStatus = userPointsService.getTasksStatus(userId);
        return Result.success(tasksStatus);
    }
    
    /**
     * 收藏剧本奖励积分
     */
    @PostMapping("/reward-favorite")
    @Operation(summary = "收藏剧本奖励积分")
    public Result<String> rewardForFavorite(@RequestParam Long userId) {
        log.info("收藏剧本奖励积分: userId={}", userId);
        try {
            userPointsService.rewardForFavorite(userId);
            return Result.success("奖励成功");
        } catch (Exception e) {
            log.error("收藏剧本奖励失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 完成预约奖励积分
     */
    @PostMapping("/reward-reservation")
    @Operation(summary = "完成预约奖励积分")
    public Result<String> rewardForReservation(
            @RequestParam Long userId, 
            @RequestParam Long reservationId) {
        log.info("完成预约奖励积分: userId={}, reservationId={}", userId, reservationId);
        try {
            userPointsService.rewardForReservation(userId, reservationId);
            return Result.success("奖励成功");
        } catch (Exception e) {
            log.error("完成预约奖励失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
