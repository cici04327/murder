package com.murder.script.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.murder.pojo.entity.ScriptFavorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 剧本收藏Mapper
 */
@Mapper
public interface ScriptFavoriteMapper extends BaseMapper<ScriptFavorite> {
}

