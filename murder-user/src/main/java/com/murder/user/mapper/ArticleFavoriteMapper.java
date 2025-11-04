package com.murder.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.murder.pojo.entity.ArticleFavorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章收藏Mapper
 */
@Mapper
public interface ArticleFavoriteMapper extends BaseMapper<ArticleFavorite> {
}
