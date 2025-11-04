package com.murder.reservation.service;

import com.murder.common.result.PageResult;
import com.murder.pojo.dto.ReservationDTO;
import com.murder.pojo.entity.Reservation;

import java.util.List;

/**
 * 预约服务接口
 */
public interface ReservationService {

    /**
     * 创建预约
     */
    Reservation create(ReservationDTO reservationDTO);

    /**
     * 分页查询预约列表
     */
    PageResult<Reservation> pageQuery(Integer page, Integer pageSize, Long userId, Integer status);
    
    /**
     * 分页查询预约列表（包含关联信息）
     */
    PageResult<com.murder.pojo.vo.ReservationVO> pageQueryWithDetails(Integer page, Integer pageSize, Long userId, Integer status);

    /**
     * 根据ID查询预约详情
     */
    Reservation getById(Long id);
    
    /**
     * 根据ID查询预约详情VO（包含关联信息）
     */
    com.murder.pojo.vo.ReservationVO getDetailById(Long id);

    /**
     * 根据预约编号查询预约详情
     */
    Reservation getByReservationNo(String reservationNo);

    /**
     * 确认预约
     */
    void confirm(Long id);

    /**
     * 取消预约
     */
    void cancel(Long id, String reason);

    /**
     * 完成预约
     */
    void complete(Long id);

    /**
     * 支付预约
     */
    void pay(Long id);
    
    /**
     * 查询即将开始的预约（指定小时数内）
     */
    List<Reservation> getUpcomingReservations(Integer hours);
    
    /**
     * 检查房间可用性
     */
    boolean checkRoomAvailability(Long roomId, String reservationTime, Double duration);
}