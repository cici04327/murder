package com.murder.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.murder.common.result.PageResult;
import com.murder.common.result.Result;
import com.murder.pojo.dto.UserLoginDTO;
import com.murder.pojo.dto.UserRegisterDTO;
import com.murder.pojo.entity.User;
import com.murder.pojo.vo.UserLoginVO;
import com.murder.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户接口")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<String> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("用户注册: {}", userRegisterDTO);
        userService.register(userRegisterDTO);
        return Result.success("注册成功");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录: {}", userLoginDTO);
        UserLoginVO userLoginVO = userService.login(userLoginDTO);
        return Result.success(userLoginVO);
    }

    /**
     * 管理员登录
     */
    @PostMapping("/admin/login")
    @Operation(summary = "管理员登录")
    public Result<UserLoginVO> adminLogin(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("管理员登录: {}", userLoginDTO);
        UserLoginVO userLoginVO = userService.adminLogin(userLoginDTO);
        return Result.success(userLoginVO);
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询用户列表")
    public Result<PageResult> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer memberLevel
    ) {
        log.info("分页查询用户: page={}, pageSize={}, username={}, phone={}, memberLevel={}", 
                page, pageSize, username, phone, memberLevel);
        
        Page<User> pageInfo = userService.pageQuery(page, pageSize, username, phone, memberLevel);
        
        PageResult pageResult = new PageResult();
        pageResult.setTotal(pageInfo.getTotal());
        pageResult.setRecords(pageInfo.getRecords());
        
        return Result.success(pageResult);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户信息")
    public Result<User> getUserById(@PathVariable Long id) {
        log.info("获取用户信息: {}", id);
        User user = userService.getById(id);
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @PostMapping
    @Operation(summary = "新增用户")
    public Result<String> add(@RequestBody User user) {
        log.info("新增用户: {}", user);
        userService.save(user);
        return Result.success("新增成功");
    }

    /**
     * 更新用户
     */
    @PutMapping
    @Operation(summary = "更新用户")
    public Result<String> update(@RequestBody User user) {
        log.info("更新用户: {}", user);
        
        // 获取更新前的用户信息，检查是否首次完善资料
        User oldUser = userService.getById(user.getId());
        boolean shouldReward = false;
        
        if (oldUser != null) {
            // 判断是否是首次完善资料（之前昵称或手机号为空，现在填写了）
            boolean wasIncomplete = (oldUser.getNickname() == null || oldUser.getNickname().isEmpty()) 
                                  || (oldUser.getPhone() == null || oldUser.getPhone().isEmpty());
            boolean isNowComplete = (user.getNickname() != null && !user.getNickname().isEmpty()) 
                                  && (user.getPhone() != null && !user.getPhone().isEmpty());
            shouldReward = wasIncomplete && isNowComplete;
        }
        
        // 更新用户信息
        userService.updateById(user);
        
        // 如果是首次完善资料，奖励积分
        if (shouldReward) {
            try {
                userService.addPointsForProfile(user.getId());
                log.info("用户{}完善资料，获得30积分", user.getId());
                return Result.success("更新成功，获得30积分奖励");
            } catch (Exception e) {
                log.error("完善资料积分发放失败", e);
                return Result.success("更新成功");
            }
        }
        
        return Result.success("更新成功");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<String> delete(@PathVariable Long id) {
        log.info("删除用户: {}", id);
        userService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 修改用户状态
     */
    @PutMapping("/status/{id}")
    @Operation(summary = "修改用户状态")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody User user) {
        log.info("修改用户状态: id={}, status={}", id, user.getStatus());
        user.setId(id);
        userService.updateById(user);
        return Result.success("状态修改成功");
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public Result<String> updatePassword(@RequestBody User user) {
        log.info("修改密码: userId={}", user.getId());
        userService.updatePassword(user);
        return Result.success("密码修改成功");
    }

    /**
     * 上传头像
     */
    @PutMapping("/avatar")
    @Operation(summary = "上传头像")
    public Result<String> uploadAvatar(@RequestBody User user) {
        log.info("上传头像: userId={}", user.getId());
        userService.updateById(user);
        return Result.success("头像上传成功");
    }
}
