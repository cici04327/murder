package com.murder.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 门店实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("store")
public class Store implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 门店名称
     */
    private String name;
    
    /**
     * 门店地址
     */
    private String address;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 门店图片
     */
    @TableField(exist = false)
    private String images;
    
    /**
     * 门店简介
     */
    private String description;
    
    /**
     * 营业开始时间
     */
    private LocalTime openTime;
    
    /**
     * 营业结束时间
     */
    private LocalTime closeTime;
    
    /**
     * 经度
     */
    private BigDecimal longitude;
    
    /**
     * 纬度
     */
    private BigDecimal latitude;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
    /**
     * 状态：1营业中，0已打烊
     */
    private Integer status;
    
    /**
     * 逻辑删除：1删除，0未删除
     */
    @TableLogic
    private Integer isDeleted;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
