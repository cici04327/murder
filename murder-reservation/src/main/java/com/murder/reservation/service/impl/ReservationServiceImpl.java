package com.murder.reservation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.murder.common.result.PageResult;
import com.murder.pojo.dto.ReservationDTO;
import com.murder.pojo.entity.Reservation;
import com.murder.reservation.mapper.ReservationMapper;
import com.murder.reservation.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 预约服务实现类
 */
@Service
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationMapper reservationMapper;
    
    @Autowired(required = false)
    private RestTemplate restTemplate;

    private static final AtomicLong SEQUENCE = new AtomicLong(1);
    
    private static final String USER_SERVICE_URL = "http://localhost:8082";
    private static final String STORE_SERVICE_URL = "http://localhost:8083";
    private static final String SCRIPT_SERVICE_URL = "http://localhost:8084";

    /**
     * 创建预约
     */
    @Override
    public Reservation create(ReservationDTO reservationDTO) {
        // 检查房间可用性
        if (reservationDTO.getRoomId() != null && reservationDTO.getReservationTime() != null) {
            String reservationTimeStr = reservationDTO.getReservationTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            boolean isAvailable = checkRoomAvailability(
                    reservationDTO.getRoomId(), 
                    reservationTimeStr, 
                    3.0 // 默认时长3小时
            );
            if (!isAvailable) {
                throw new RuntimeException("该时段房间已被预约，请选择其他时间");
            }
        }
        
        // 生成预约编号
        String reservationNo = generateReservationNo();
        
        // 构建预约对象
        Reservation reservation = new Reservation();
        BeanUtils.copyProperties(reservationDTO, reservation);
        reservation.setOrderNo(reservationNo);
        reservation.setStatus(1); // 待确认
        reservation.setPayStatus(0); // 未支付
        
        // 处理优惠券
        if (reservationDTO.getUserCouponId() != null) {
            try {
                // 计算优惠金额
                BigDecimal discount = calculateCouponDiscount(
                    reservationDTO.getUserCouponId(), 
                    reservation.getTotalPrice()
                );
                
                reservation.setCouponId(reservationDTO.getUserCouponId());
                reservation.setDiscountAmount(discount);
                reservation.setActualAmount(reservation.getTotalPrice().subtract(discount));
                
                log.info("订单使用优惠券，优惠金额: {}", discount);
            } catch (Exception e) {
                log.error("计算优惠券折扣失败", e);
                // 优惠券处理失败不影响订单创建
                reservation.setActualAmount(reservation.getTotalPrice());
            }
        } else {
            reservation.setActualAmount(reservation.getTotalPrice());
        }
        
        // 保存预约
        reservationMapper.insert(reservation);
        
        // 使用优惠券
        if (reservationDTO.getUserCouponId() != null) {
            try {
                useCoupon(reservationDTO.getUserCouponId(), reservation.getId());
            } catch (Exception e) {
                log.error("使用优惠券失败", e);
            }
        }
        
        // 发送预约成功通知
        try {
            sendReservationSuccessNotification(reservation);
        } catch (Exception e) {
            log.error("发送预约成功通知失败", e);
        }
        
        return reservation;
    }

    /**
     * 分页查询预约列表
     */
    @Override
    public PageResult<Reservation> pageQuery(Integer page, Integer pageSize, Long userId, Integer status) {
        Page<Reservation> pageInfo = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(Reservation::getUserId, userId);
        }
        if (status != null) {
            wrapper.eq(Reservation::getStatus, status);
        }
        wrapper.orderByDesc(Reservation::getCreateTime);
        
        // 手动查询总数
        Long total = reservationMapper.selectCount(wrapper);
        
        reservationMapper.selectPage(pageInfo, wrapper);
        
        // 使用手动查询的 total 值
        return new PageResult<>(total, pageInfo.getRecords());
    }
    
    /**
     * 分页查询预约列表（包含关联信息）
     */
    @Override
    public PageResult<com.murder.pojo.vo.ReservationVO> pageQueryWithDetails(Integer page, Integer pageSize, Long userId, Integer status) {
        // 先获取基础分页数据
        PageResult<Reservation> pageResult = pageQuery(page, pageSize, userId, status);
        
        // 转换为VO列表
        java.util.List<com.murder.pojo.vo.ReservationVO> voList = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList());
        
        return new PageResult<>(pageResult.getTotal(), voList);
    }
    
    /**
     * 将Reservation转换为ReservationVO（不查询关联信息，仅基础转换）
     */
    private com.murder.pojo.vo.ReservationVO convertToVO(Reservation reservation) {
        com.murder.pojo.vo.ReservationVO vo = new com.murder.pojo.vo.ReservationVO();
        org.springframework.beans.BeanUtils.copyProperties(reservation, vo);
        
        // 查询关联信息
        try {
            // 查询剧本名称
            if (reservation.getScriptId() != null && restTemplate != null) {
                try {
                    String scriptUrl = SCRIPT_SERVICE_URL + "/script/" + reservation.getScriptId();
                    Map<String, Object> scriptResponse = restTemplate.getForObject(scriptUrl, Map.class);
                    if (scriptResponse != null && scriptResponse.get("data") != null) {
                        Map<String, Object> script = (Map<String, Object>) scriptResponse.get("data");
                        vo.setScriptName((String) script.get("name"));
                    }
                } catch (Exception e) {
                    log.debug("查询剧本信息失败: {}", e.getMessage());
                }
            }
            
            // 查询门店名称
            if (reservation.getStoreId() != null && restTemplate != null) {
                try {
                    String storeUrl = STORE_SERVICE_URL + "/store/" + reservation.getStoreId();
                    Map<String, Object> storeResponse = restTemplate.getForObject(storeUrl, Map.class);
                    if (storeResponse != null && storeResponse.get("data") != null) {
                        Map<String, Object> store = (Map<String, Object>) storeResponse.get("data");
                        vo.setStoreName((String) store.get("name"));
                    }
                } catch (Exception e) {
                    log.debug("查询门店信息失败: {}", e.getMessage());
                }
            }
            
            // 查询房间名称和容量
            if (reservation.getRoomId() != null && restTemplate != null) {
                try {
                    String roomUrl = STORE_SERVICE_URL + "/store/room/detail/" + reservation.getRoomId();
                    Map<String, Object> roomResponse = restTemplate.getForObject(roomUrl, Map.class);
                    if (roomResponse != null && roomResponse.get("data") != null) {
                        Map<String, Object> room = (Map<String, Object>) roomResponse.get("data");
                        vo.setRoomName((String) room.get("name"));
                        if (room.get("capacity") != null) {
                            vo.setRoomCapacity((Integer) room.get("capacity"));
                        }
                    }
                } catch (Exception e) {
                    log.debug("查询房间信息失败: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("查询关联信息时出错: {}", e.getMessage());
        }
        
        return vo;
    }

    /**
     * 根据ID查询预约详情
     */
    @Override
    public Reservation getById(Long id) {
        return reservationMapper.selectById(id);
    }
    
    /**
     * 根据ID查询预约详情VO（包含关联信息）
     */
    @Override
    public com.murder.pojo.vo.ReservationVO getDetailById(Long id) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            return null;
        }
        
        com.murder.pojo.vo.ReservationVO vo = new com.murder.pojo.vo.ReservationVO();
        org.springframework.beans.BeanUtils.copyProperties(reservation, vo);
        
        // 查询门店信息
        if (reservation.getStoreId() != null && restTemplate != null) {
            try {
                String url = STORE_SERVICE_URL + "/store/" + reservation.getStoreId();
                Map<String, Object> storeResponse = restTemplate.getForObject(url, Map.class);
                if (storeResponse != null && storeResponse.get("data") != null) {
                    Map<String, Object> store = (Map<String, Object>) storeResponse.get("data");
                    vo.setStoreName((String) store.get("name"));
                    vo.setStoreAddress((String) store.get("address"));
                    vo.setStorePhone((String) store.get("phone"));
                }
            } catch (Exception e) {
                log.error("查询门店信息失败: {}", e.getMessage());
            }
        }
        
        // 查询剧本信息
        if (reservation.getScriptId() != null && restTemplate != null) {
            try {
                String url = SCRIPT_SERVICE_URL + "/script/" + reservation.getScriptId();
                Map<String, Object> scriptResponse = restTemplate.getForObject(url, Map.class);
                if (scriptResponse != null && scriptResponse.get("data") != null) {
                    Map<String, Object> script = (Map<String, Object>) scriptResponse.get("data");
                    vo.setScriptName((String) script.get("name"));
                    vo.setScriptCover((String) script.get("cover"));
                }
            } catch (Exception e) {
                log.error("查询剧本信息失败: {}", e.getMessage());
            }
        }
        
        // 查询房间信息
        if (reservation.getRoomId() != null && restTemplate != null) {
            try {
                String url = STORE_SERVICE_URL + "/store/room/detail/" + reservation.getRoomId();
                Map<String, Object> roomResponse = restTemplate.getForObject(url, Map.class);
                if (roomResponse != null && roomResponse.get("data") != null) {
                    Map<String, Object> room = (Map<String, Object>) roomResponse.get("data");
                    vo.setRoomName((String) room.get("name"));
                    if (room.get("capacity") != null) {
                        vo.setRoomCapacity((Integer) room.get("capacity"));
                    }
                }
            } catch (Exception e) {
                log.error("查询房间信息失败: {}", e.getMessage());
            }
        }
        
        return vo;
    }

    /**
     * 根据预约编号查询预约详情
     */
    @Override
    public Reservation getByReservationNo(String reservationNo) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getOrderNo, reservationNo);
        return reservationMapper.selectOne(wrapper);
    }

    /**
     * 确认预约
     */
    @Override
    public void confirm(Long id) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setStatus(2); // 已确认
        reservationMapper.updateById(reservation);
    }

    /**
     * 取消预约
     */
    @Override
    public void cancel(Long id, String reason) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new RuntimeException("预约不存在");
        }
        
        // 退还优惠券
        if (reservation.getCouponId() != null) {
            try {
                refundCoupon(id);
                log.info("订单取消，退还优惠券");
            } catch (Exception e) {
                log.error("退还优惠券失败", e);
            }
        }
        
        reservation.setStatus(4); // 已取消
        reservation.setRemark(reason);
        reservationMapper.updateById(reservation);
    }

    /**
     * 完成预约
     */
    @Override
    public void complete(Long id) {
        // 查询预约信息
        Reservation existingReservation = reservationMapper.selectById(id);
        if (existingReservation == null) {
            throw new RuntimeException("预约不存在");
        }
        
        // 更新预约状态
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setStatus(3); // 已完成
        reservationMapper.updateById(reservation);
        
        // 增加积分奖励（完成预约获得100积分）
        try {
            addPointsForReservation(existingReservation.getUserId(), id);
        } catch (Exception e) {
            log.error("完成预约增加积分失败", e);
            // 积分失败不影响预约完成
        }
    }

    /**
     * 支付预约
     */
    @Override
    public void pay(Long id) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setPayStatus(1); // 已支付
        reservation.setPayTime(LocalDateTime.now());
        reservationMapper.updateById(reservation);
    }
    
    /**
     * 查询即将开始的预约（指定小时数内）
     */
    @Override
    public List<Reservation> getUpcomingReservations(Integer hours) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusHours(hours);
        return reservationMapper.selectUpcomingReservations(now, endTime);
    }
    
    /**
     * 检查房间可用性
     */
    @Override
    public boolean checkRoomAvailability(Long roomId, String reservationTime, Double duration) {
        try {
            LocalDateTime startTime = LocalDateTime.parse(reservationTime, 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            
            // 计算结束时间（向上取整小时数）
            long durationHours = (long) Math.ceil(duration);
            LocalDateTime endTime = startTime.plusHours(durationHours);
            
            // 查询是否有冲突的预约
            int conflictCount = reservationMapper.countConflictingReservations(roomId, startTime, endTime);
            
            return conflictCount == 0;
        } catch (Exception e) {
            log.error("检查房间可用性失败", e);
            return false;
        }
    }

    /**
     * 生成预约编号
     */
    private String generateReservationNo() {
        // 使用时间戳+随机数确保唯一性
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int)(Math.random() * 10000);
        String randomPart = String.format("%04d", random);
        return "R" + datePart + randomPart;
    }
    
    /**
     * 计算优惠券折扣
     */
    private BigDecimal calculateCouponDiscount(Long userCouponId, BigDecimal orderAmount) {
        if (restTemplate == null) {
            log.warn("RestTemplate未配置，无法调用优惠券服务");
            return BigDecimal.ZERO;
        }
        
        try {
            String url = USER_SERVICE_URL + "/coupon/calculate?userCouponId=" + userCouponId 
                    + "&orderAmount=" + orderAmount;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.get("data") != null) {
                return new BigDecimal(response.get("data").toString());
            }
        } catch (Exception e) {
            log.error("调用优惠券服务计算折扣失败", e);
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * 增加预约积分奖励
     */
    private void addPointsForReservation(Long userId, Long reservationId) {
        if (restTemplate == null) {
            log.warn("RestTemplate未配置，无法调用积分服务");
            return;
        }
        
        try {
            String url = USER_SERVICE_URL + "/user/points/reward-reservation?userId=" + userId 
                    + "&reservationId=" + reservationId;
            restTemplate.postForObject(url, null, String.class);
            log.info("用户{}完成预约{}，获得100积分", userId, reservationId);
        } catch (Exception e) {
            log.error("调用积分服务失败", e);
            throw e;
        }
    }
    
    /**
     * 使用优惠券
     */
    private void useCoupon(Long userCouponId, Long orderId) {
        if (restTemplate == null) {
            log.warn("RestTemplate未配置，无法调用优惠券服务");
            return;
        }
        
        try {
            String url = USER_SERVICE_URL + "/coupon/use?userCouponId=" + userCouponId 
                    + "&orderId=" + orderId;
            restTemplate.put(url, null);
            log.info("优惠券使用成功: userCouponId={}, orderId={}", userCouponId, orderId);
        } catch (Exception e) {
            log.error("使用优惠券失败", e);
            throw new RuntimeException("使用优惠券失败: " + e.getMessage());
        }
    }
    
    /**
     * 退还优惠券
     */
    private void refundCoupon(Long orderId) {
        if (restTemplate == null) {
            log.warn("RestTemplate未配置，无法调用优惠券服务");
            return;
        }
        
        try {
            String url = USER_SERVICE_URL + "/coupon/refund?orderId=" + orderId;
            restTemplate.put(url, null);
            log.info("优惠券退还成功: orderId={}", orderId);
        } catch (Exception e) {
            log.error("退还优惠券失败", e);
            throw new RuntimeException("退还优惠券失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送预约成功通知
     */
    private void sendReservationSuccessNotification(Reservation reservation) {
        if (restTemplate == null) {
            log.warn("RestTemplate未配置，无法发送通知");
            return;
        }
        
        try {
            String title = "预约成功通知";
            String content = String.format("您已成功预约，预约编号：%s，预约时间：%s，请准时到场！", 
                    reservation.getOrderNo(), 
                    reservation.getReservationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("title", title);
            notificationData.put("content", content);
            notificationData.put("type", 1); // 预约成功
            notificationData.put("bizType", "reservation");
            notificationData.put("bizId", reservation.getId());
            notificationData.put("userIds", new Long[]{reservation.getUserId()});
            
            String url = USER_SERVICE_URL + "/notification/send";
            restTemplate.postForObject(url, notificationData, Map.class);
            
            log.info("发送预约成功通知: userId={}, reservationId={}", reservation.getUserId(), reservation.getId());
        } catch (Exception e) {
            log.error("发送预约成功通知失败", e);
        }
    }
}