package com.murder.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 性别：1男，2女
     */
    private Integer gender;
    
    /**
     * 会员等级：1普通，2银卡，3金卡，4钻石
     */
    private Integer memberLevel;
    
    /**
     * VIP等级：0非会员，1普通，2银卡，3金卡，4钻石
     */
    private Integer vipLevel;
    
    /**
     * VIP到期时间
     */
    private LocalDateTime vipExpireTime;
    
    /**
     * 邮箱
     */
    @TableField(exist = false)
    private String email;
    
    /**
     * 生日
     */
    @TableField(exist = false)
    private java.time.LocalDate birthday;
    
    /**
     * 所在城市
     */
    @TableField(exist = false)
    private String city;
    
    /**
     * 积分
     */
    private Integer points;
    
    /**
     * 总游戏次数
     */
    @TableField(exist = false)
    private Integer totalGames;
    
    /**
     * 总消费金额
     */
    @TableField(exist = false)
    private java.math.BigDecimal totalSpend;
    
    /**
     * 最后登录时间
     */
    @TableField(exist = false)
    private LocalDateTime lastLoginTime;
    
    /**
     * 状态：1启用，0禁用
     */
    private Integer status;
    
    /**
     * 用户角色：USER-普通用户，ADMIN-管理员
     */
    private String role;
    
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
