package com.murder.store.controller;

import com.murder.common.result.PageResult;
import com.murder.common.result.Result;
import com.murder.pojo.dto.StoreQueryDTO;
import com.murder.pojo.entity.Store;
import com.murder.pojo.vo.StoreDetailVO;
import com.murder.pojo.vo.StoreStatisticsVO;
import com.murder.pojo.vo.StoreVO;
import com.murder.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 门店控制器
 */
@RestController
@RequestMapping("/store")
@Tag(name = "门店接口")
@Slf4j
public class StoreController {

    @Autowired
    private StoreService storeService;

    /**
     * 分页查询门店列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询门店列表")
    public Result<PageResult<StoreVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name) {
        log.info("分页查询门店列表: page={}, pageSize={}, name={}", page, pageSize, name);
        PageResult<StoreVO> pageResult = storeService.pageQuery(page, pageSize, name);
        return Result.success(pageResult);
    }

    /**
     * 多条件分页查询门店列表
     */
    @PostMapping("/page/advanced")
    @Operation(summary = "多条件分页查询门店列表")
    public Result<PageResult<StoreVO>> pageAdvanced(@RequestBody StoreQueryDTO queryDTO) {
        log.info("多条件分页查询门店列表: {}", queryDTO);
        PageResult<StoreVO> pageResult = storeService.pageQueryAdvanced(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取所有门店列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取所有门店列表")
    public Result<List<StoreVO>> list() {
        log.info("获取所有门店列表");
        List<StoreVO> storeList = storeService.listAll();
        return Result.success(storeList);
    }

    /**
     * 获取门店统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取门店统计信息")
    public Result<StoreStatisticsVO> getStatistics() {
        log.info("获取门店统计信息");
        StoreStatisticsVO statistics = storeService.getStatistics();
        return Result.success(statistics);
    }

    /**
     * 根据ID查询门店详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询门店详情")
    public Result<StoreVO> getById(@PathVariable Long id) {
        log.info("查询门店详情: {}", id);
        StoreVO storeVO = storeService.getById(id);
        return Result.success(storeVO);
    }

    /**
     * 根据ID查询门店详细信息
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "查询门店详细信息")
    public Result<StoreDetailVO> getDetailById(@PathVariable Long id) {
        log.info("查询门店详细信息: {}", id);
        StoreDetailVO detailVO = storeService.getDetailById(id);
        return Result.success(detailVO);
    }

    /**
     * 新增门店
     */
    @PostMapping
    @Operation(summary = "新增门店")
    public Result<String> add(@RequestBody Store store) {
        log.info("新增门店: {}", store);
        storeService.add(store);
        return Result.success("新增成功");
    }

    /**
     * 更新门店
     */
    @PutMapping
    @Operation(summary = "更新门店")
    public Result<String> update(@RequestBody Store store) {
        log.info("更新门店: {}", store);
        storeService.update(store);
        return Result.success("更新成功");
    }

    /**
     * 删除门店
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除门店")
    public Result<String> delete(@PathVariable Long id) {
        log.info("删除门店: {}", id);
        storeService.delete(id);
        return Result.success("删除成功");
    }

    /**
     * 批量删除门店
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除门店")
    public Result<String> batchDelete(@RequestBody List<Long> ids) {
        log.info("批量删除门店: {}", ids);
        storeService.batchDelete(ids);
        return Result.success("批量删除成功");
    }

    /**
     * 更新门店状态
     */
    @PutMapping("/status/{id}")
    @Operation(summary = "更新门店状态")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("更新门店状态: id={}, status={}", id, status);
        storeService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }

    /**
     * 批量更新门店状态
     */
    @PutMapping("/status/batch")
    @Operation(summary = "批量更新门店状态")
    public Result<String> batchUpdateStatus(@RequestBody List<Long> ids, @RequestParam Integer status) {
        log.info("批量更新门店状态: ids={}, status={}", ids, status);
        storeService.batchUpdateStatus(ids, status);
        return Result.success("批量状态更新成功");
    }
    
    /**
     * 获取热门门店
     */
    @GetMapping("/list/hot")
    @Operation(summary = "获取热门门店")
    public Result<List<StoreVO>> getHotStores() {
        log.info("获取热门门店");
        List<StoreVO> hotStores = storeService.getHotStores();
        return Result.success(hotStores);
    }
    
    /**
     * 获取推荐门店
     */
    @GetMapping("/list/recommended")
    @Operation(summary = "获取推荐门店")
    public Result<List<StoreVO>> getRecommendedStores() {
        log.info("获取推荐门店");
        List<StoreVO> recommendedStores = storeService.getRecommendedStores();
        return Result.success(recommendedStores);
    }

    /**
     * 获取附近门店
     */
    @GetMapping("/nearby")
    @Operation(summary = "获取附近门店")
    public Result<List<StoreVO>> getNearbyStores(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取附近门店: latitude={}, longitude={}, limit={}", latitude, longitude, limit);
        // TODO: 实现基于地理位置的附近门店查询
        // 目前返回热门门店作为替代
        List<StoreVO> nearbyStores = storeService.getHotStores();
        return Result.success(nearbyStores);
    }
}
