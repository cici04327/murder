package com.murder.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 剧本实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("script")
public class Script implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 剧本名称
     */
    private String name;
    
    /**
     * 剧本分类ID
     */
    private Long categoryId;
    
    /**
     * 剧本封面图片URL
     */
    private String cover;
    
    /**
     * 剧本详情图片（多张图片用逗号分隔）
     */
    private String images;
    
    /**
     * 剧本简介
     */
    private String description;
    
    /**
     * 剧本类型：1恐怖，2推理，3情感，4欢乐，5机制，6还原
     */
    private Integer type;
    
    /**
     * 难度等级：1简单，2普通，3困难，4硬核
     */
    private Integer difficulty;
    
    /**
     * 游戏人数
     */
    private Integer playerCount;
    
    /**
     * 游戏时长（小时）
     */
    private BigDecimal duration;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 标签（多个标签用逗号分隔）
     */
    private String tags;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
    /**
     * 是否独家：1是，0否
     */
    private Integer isExclusive;
    
    /**
     * 状态：1上架，0下架
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
    
    // 额外添加关键字段的 getter 方法确保序列化
    public Long getId() { return id; }
    public String getName() { return name; }
    public Long getCategoryId() { return categoryId; }
    public String getCover() { return cover; }
    public String getDescription() { return description; }
    public Integer getPlayerCount() { return playerCount; }
    public BigDecimal getDuration() { return duration; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getRating() { return rating; }
    public Integer getStatus() { return status; }
}
