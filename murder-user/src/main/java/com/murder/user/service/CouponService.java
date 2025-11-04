package com.murder.user.service;

import com.murder.common.result.PageResult;
import com.murder.pojo.dto.CouponDTO;
import com.murder.pojo.entity.Coupon;
import com.murder.pojo.entity.UserCoupon;
import com.murder.pojo.vo.CouponVO;
import com.murder.pojo.vo.UserCouponVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券服务接口
 */
public interface CouponService {

    /**
     * 分页查询优惠券列表
     */
    PageResult<CouponVO> pageQuery(Integer page, Integer pageSize, String name, Integer type, Integer status);

    /**
     * 根据ID查询优惠券详情
     */
    CouponVO getById(Long id);

    /**
     * 新增优惠券
     */
    void add(CouponDTO couponDTO);

    /**
     * 更新优惠券
     */
    void update(CouponDTO couponDTO);

    /**
     * 删除优惠券
     */
    void delete(Long id);
    
    /**
     * 上架/下架优惠券
     */
    void updateStatus(Long id, Integer status);

    /**
     * 用户领取优惠券
     */
    void receiveCoupon(Long userId, Long couponId);

    /**
     * 查询用户的优惠券列表
     */
    PageResult<UserCouponVO> getUserCoupons(Long userId, Integer page, Integer pageSize, Integer status);
    
    /**
     * 查询用户可用的优惠券列表
     */
    List<UserCouponVO> getAvailableCoupons(Long userId, BigDecimal orderAmount);

    /**
     * 使用优惠券（与订单关联）
     */
    void useCoupon(Long userCouponId, Long orderId);
    
    /**
     * 计算优惠金额
     */
    BigDecimal calculateDiscount(Long userCouponId, BigDecimal orderAmount);
    
    /**
     * 退还优惠券（订单取消时）
     */
    void refundCoupon(Long orderId);
    
    /**
     * 批量过期优惠券（定时任务）
     */
    void expireCoupons();
    
    /**
     * 获取优惠券统计信息
     */
    CouponVO getCouponStatistics(Long couponId);
}
