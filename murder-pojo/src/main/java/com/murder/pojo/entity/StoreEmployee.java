package com.murder.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 门店员工实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("store_employee")
public class StoreEmployee implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 门店ID
     */
    private Long storeId;
    
    /**
     * 员工姓名
     */
    private String name;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 职位：1店长，2副店长，3主持人，4服务员
     */
    private Integer position;
    
    /**
     * 入职日期
     */
    private LocalDate joinDate;
    
    /**
     * 月薪
     */
    private BigDecimal salary;
    
    /**
     * 状态：1在职，0离职
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
