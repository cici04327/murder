package com.murder.script.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.murder.pojo.entity.Script;
import org.apache.ibatis.annotations.Mapper;

/**
 * 剧本映射器
 */
@Mapper
public interface ScriptMapper extends BaseMapper<Script> {
}