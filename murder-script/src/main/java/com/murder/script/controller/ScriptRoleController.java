package com.murder.script.controller;

import com.murder.common.result.Result;
import com.murder.pojo.entity.ScriptRole;
import com.murder.script.service.ScriptRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 剧本角色控制器
 */
@RestController
@RequestMapping("/script/role")
@Tag(name = "剧本角色接口")
@Slf4j
public class ScriptRoleController {

    @Autowired
    private ScriptRoleService scriptRoleService;

    /**
     * 根据剧本ID获取角色列表
     */
    @GetMapping("/list")
    @Operation(summary = "根据剧本ID获取角色列表")
    public Result<List<ScriptRole>> list(@RequestParam Long scriptId) {
        log.info("获取剧本角色列表: scriptId={}", scriptId);
        List<ScriptRole> roles = scriptRoleService.listByScriptId(scriptId);
        return Result.success(roles);
    }

    /**
     * 根据ID查询角色详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询角色详情")
    public Result<ScriptRole> getById(@PathVariable Long id) {
        log.info("查询角色详情: {}", id);
        ScriptRole role = scriptRoleService.getById(id);
        return Result.success(role);
    }

    /**
     * 新增角色
     */
    @PostMapping
    @Operation(summary = "新增角色")
    public Result<String> add(@RequestBody ScriptRole scriptRole) {
        log.info("新增角色: {}", scriptRole);
        scriptRoleService.add(scriptRole);
        return Result.success("新增成功");
    }

    /**
     * 更新角色
     */
    @PutMapping
    @Operation(summary = "更新角色")
    public Result<String> update(@RequestBody ScriptRole scriptRole) {
        log.info("更新角色: {}", scriptRole);
        scriptRoleService.update(scriptRole);
        return Result.success("更新成功");
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    public Result<String> delete(@PathVariable Long id) {
        log.info("删除角色: {}", id);
        scriptRoleService.delete(id);
        return Result.success("删除成功");
    }

    /**
     * 批量新增角色
     */
    @PostMapping("/batch")
    @Operation(summary = "批量新增角色")
    public Result<String> addBatch(@RequestBody List<ScriptRole> roles) {
        log.info("批量新增角色，数量: {}", roles.size());
        scriptRoleService.addBatch(roles);
        return Result.success("批量新增成功");
    }
}
