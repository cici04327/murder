package com.murder.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.murder.pojo.dto.UserLoginDTO;
import com.murder.pojo.dto.UserRegisterDTO;
import com.murder.pojo.entity.User;
import com.murder.pojo.vo.UserLoginVO;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    void register(UserRegisterDTO userRegisterDTO);

    /**
     * 用户登录
     */
    UserLoginVO login(UserLoginDTO userLoginDTO);

    /**
     * 管理员登录
     */
    UserLoginVO adminLogin(UserLoginDTO userLoginDTO);

    /**
     * 根据ID获取用户信息
     */
    UserLoginVO getUserById(Long id);

    /**
     * 分页查询用户列表
     */
    Page<User> pageQuery(Integer page, Integer pageSize, String username, String phone, Integer memberLevel);

    /**
     * 修改密码
     */
    void updatePassword(User user);
    
    /**
     * 完善资料奖励积分
     */
    void addPointsForProfile(Long userId);
}
