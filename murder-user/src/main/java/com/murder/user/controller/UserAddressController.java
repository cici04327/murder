package com.murder.user.controller;

import com.murder.common.result.Result;
import com.murder.pojo.dto.UserAddressDTO;
import com.murder.pojo.entity.UserAddress;
import com.murder.user.service.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户地址控制器
 */
@RestController
@RequestMapping("/user/address")
@Tag(name = "用户地址接口")
@Slf4j
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    /**
     * 查询用户地址列表
     */
    @GetMapping("/list/{userId}")
    @Operation(summary = "查询用户地址列表")
    public Result<List<UserAddress>> list(@PathVariable Long userId) {
        log.info("查询用户地址列表: {}", userId);
        List<UserAddress> addressList = userAddressService.listByUserId(userId);
        return Result.success(addressList);
    }

    /**
     * 根据ID查询地址详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询地址详情")
    public Result<UserAddress> getById(@PathVariable Long id) {
        log.info("查询地址详情: {}", id);
        UserAddress address = userAddressService.getById(id);
        return Result.success(address);
    }

    /**
     * 新增地址
     */
    @PostMapping
    @Operation(summary = "新增地址")
    public Result<String> add(@RequestBody UserAddressDTO addressDTO) {
        log.info("新增地址: {}", addressDTO);
        userAddressService.add(addressDTO);
        return Result.success("新增成功");
    }

    /**
     * 更新地址
     */
    @PutMapping
    @Operation(summary = "更新地址")
    public Result<String> update(@RequestBody UserAddressDTO addressDTO) {
        log.info("更新地址: {}", addressDTO);
        userAddressService.update(addressDTO);
        return Result.success("更新成功");
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除地址")
    public Result<String> delete(@PathVariable Long id) {
        log.info("删除地址: {}", id);
        userAddressService.delete(id);
        return Result.success("删除成功");
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/{id}/default")
    @Operation(summary = "设置默认地址")
    public Result<String> setDefault(@PathVariable Long id, @RequestParam Long userId) {
        log.info("设置默认地址: id={}, userId={}", id, userId);
        userAddressService.setDefault(id, userId);
        return Result.success("设置成功");
    }

    /**
     * 获取默认地址
     */
    @GetMapping("/default/{userId}")
    @Operation(summary = "获取默认地址")
    public Result<UserAddress> getDefault(@PathVariable Long userId) {
        log.info("获取默认地址: {}", userId);
        UserAddress address = userAddressService.getDefaultAddress(userId);
        return Result.success(address);
    }
}
