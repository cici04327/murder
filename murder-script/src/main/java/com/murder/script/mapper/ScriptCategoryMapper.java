package com.murder.script.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.murder.pojo.entity.ScriptCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 剧本分类映射器
 */
@Mapper
public interface ScriptCategoryMapper extends BaseMapper<ScriptCategory> {
}