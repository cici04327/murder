package com.murder.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.murder.pojo.entity.UserPointsRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户积分记录Mapper
 */
@Mapper
public interface UserPointsRecordMapper extends BaseMapper<UserPointsRecord> {
}
