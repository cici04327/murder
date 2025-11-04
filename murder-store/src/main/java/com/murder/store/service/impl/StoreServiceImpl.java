package com.murder.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.murder.common.constant.RedisConstant;
import com.murder.common.result.PageResult;
import com.murder.pojo.dto.StoreQueryDTO;
import com.murder.pojo.entity.Store;
import com.murder.pojo.entity.StoreRoom;
import com.murder.pojo.vo.StoreDetailVO;
import com.murder.pojo.vo.StoreStatisticsVO;
import com.murder.pojo.vo.StoreVO;
import com.murder.store.mapper.StoreEmployeeMapper;
import com.murder.store.mapper.StoreMapper;
import com.murder.store.mapper.StoreReviewMapper;
import com.murder.store.mapper.StoreRoomMapper;
import com.murder.store.service.StoreService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 门店服务实现类
 */
@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreMapper storeMapper;
    
    @Autowired
    private StoreRoomMapper storeRoomMapper;
    
    @Autowired
    private StoreEmployeeMapper storeEmployeeMapper;
    
    @Autowired
    private StoreReviewMapper storeReviewMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 分页查询门店列表
     */
    @Override
    public PageResult<StoreVO> pageQuery(Integer page, Integer pageSize, String name) {
        Page<Store> pageInfo = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Store::getName, name);
        }
        wrapper.orderByDesc(Store::getCreateTime);
        
        // 手动查询总数
        Long total = storeMapper.selectCount(wrapper);
        
        storeMapper.selectPage(pageInfo, wrapper);
        
        List<StoreVO> storeVOList = pageInfo.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        // 使用手动查询的 total 值
        return new PageResult<>(total, storeVOList);
    }

    /**
     * 根据ID查询门店详情
     */
    @Override
    @Cacheable(value = "store", key = "#id", unless = "#result == null")
    public StoreVO getById(Long id) {
        // 先从Redis缓存中获取
        String cacheKey = RedisConstant.STORE_CACHE_PREFIX + id;
        StoreVO cachedStore = (StoreVO) redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedStore != null) {
            return cachedStore;
        }
        
        // 缓存未命中，从数据库查询
        Store store = storeMapper.selectById(id);
        StoreVO storeVO = convertToVO(store);
        
        // 存入缓存
        if (storeVO != null) {
            redisTemplate.opsForValue().set(cacheKey, storeVO, RedisConstant.CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
        return storeVO;
    }

    /**
     * 新增门店
     */
    @Override
    public void add(Store store) {
        storeMapper.insert(store);
    }

    /**
     * 更新门店
     */
    @Override
    @CacheEvict(value = "store", key = "#store.id")
    public void update(Store store) {
        storeMapper.updateById(store);
        // 删除缓存
        String cacheKey = RedisConstant.STORE_CACHE_PREFIX + store.getId();
        redisTemplate.delete(cacheKey);
    }

    /**
     * 删除门店
     */
    @Override
    @CacheEvict(value = "store", key = "#id")
    public void delete(Long id) {
        storeMapper.deleteById(id);
        // 删除缓存
        String cacheKey = RedisConstant.STORE_CACHE_PREFIX + id;
        redisTemplate.delete(cacheKey);
    }

    @Override
    public PageResult<StoreVO> pageQueryAdvanced(StoreQueryDTO queryDTO) {
        Page<Store> pageInfo = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        
        // 门店名称模糊查询
        if (StringUtils.hasText(queryDTO.getName())) {
            wrapper.like(Store::getName, queryDTO.getName());
        }
        
        // 地址模糊查询
        if (StringUtils.hasText(queryDTO.getAddress())) {
            wrapper.like(Store::getAddress, queryDTO.getAddress());
        }
        
        // 状态查询 - 默认只查询营业中的门店
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Store::getStatus, queryDTO.getStatus());
        } else {
            wrapper.eq(Store::getStatus, 1);
        }
        
        // 评分范围查询
        if (queryDTO.getMinRating() != null) {
            wrapper.ge(Store::getRating, queryDTO.getMinRating());
        }
        if (queryDTO.getMaxRating() != null) {
            wrapper.le(Store::getRating, queryDTO.getMaxRating());
        }
        
        // 手动查询总数
        Long total = storeMapper.selectCount(wrapper);
        
        // 根据排序方式排序
        String sortBy = queryDTO.getSortBy();
        if ("rating".equals(sortBy)) {
            // 评分最高：按评分降序，评分相同按创建时间降序
            wrapper.orderByDesc(Store::getRating)
                   .orderByDesc(Store::getCreateTime);
        } else if ("hot".equals(sortBy)) {
            // 最热门：按创建时间降序（可以后续改为按预约数量等）
            wrapper.orderByDesc(Store::getCreateTime)
                   .orderByDesc(Store::getRating);
        } else if ("distance".equals(sortBy)) {
            // 距离最近：如果提供了经纬度，按距离排序；否则按默认排序
            if (queryDTO.getLongitude() != null && queryDTO.getLatitude() != null) {
                // 先查询所有数据
                wrapper.orderByDesc(Store::getRating);
            } else {
                // 没有经纬度信息，按评分排序
                wrapper.orderByDesc(Store::getRating)
                       .orderByDesc(Store::getCreateTime);
            }
        } else {
            // 默认排序：按评分降序
            wrapper.orderByDesc(Store::getRating)
                   .orderByDesc(Store::getCreateTime);
        }
        
        storeMapper.selectPage(pageInfo, wrapper);
        
        List<StoreVO> storeVOList = pageInfo.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        // 如果是距离排序且提供了经纬度，需要计算距离并重新排序
        if ("distance".equals(sortBy) && queryDTO.getLongitude() != null && queryDTO.getLatitude() != null) {
            storeVOList = storeVOList.stream()
                    .peek(storeVO -> {
                        // 计算距离并存储在VO中
                        if (storeVO.getLongitude() != null && storeVO.getLatitude() != null) {
                            double distance = calculateDistance(
                                    queryDTO.getLatitude().doubleValue(),
                                    queryDTO.getLongitude().doubleValue(),
                                    storeVO.getLatitude().doubleValue(),
                                    storeVO.getLongitude().doubleValue()
                            );
                            storeVO.setDistance(distance);
                        }
                    })
                    .sorted((s1, s2) -> {
                        // 按距离排序
                        if (s1.getDistance() == null) return 1;
                        if (s2.getDistance() == null) return -1;
                        return Double.compare(s1.getDistance(), s2.getDistance());
                    })
                    .collect(Collectors.toList());
        }
        
        // 使用手动查询的 total 值
        return new PageResult<>(total, storeVOList);
    }
    
    /**
     * 计算两点之间的距离（单位：公里）
     * 使用Haversine公式计算地球表面两点间的距离
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // 地球半径（公里）
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }

    @Override
    public StoreDetailVO getDetailById(Long id) {
        Store store = storeMapper.selectById(id);
        if (store == null) {
            return null;
        }
        
        // 查询房间列表
        LambdaQueryWrapper<StoreRoom> roomWrapper = new LambdaQueryWrapper<>();
        roomWrapper.eq(StoreRoom::getStoreId, id);
        List<StoreRoom> rooms = storeRoomMapper.selectList(roomWrapper);
        
        // 转换房间信息
        List<StoreDetailVO.RoomInfo> roomInfos = rooms.stream()
                .map(room -> StoreDetailVO.RoomInfo.builder()
                        .id(room.getId())
                        .name(room.getName())
                        .type(room.getType())
                        .capacity(room.getCapacity())
                        .description(room.getDescription())
                        .status(room.getStatus())
                        .build())
                .collect(Collectors.toList());
        
        // 统计员工数量
        Long employeeCount = storeEmployeeMapper.countByStoreId(id);
        
        // 统计评价数量
        Long reviewCount = storeReviewMapper.countByStoreId(id);
        
        // 构建详情VO
        StoreDetailVO detailVO = StoreDetailVO.builder()
                .id(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .phone(store.getPhone())
                .images(store.getImages())
                .description(store.getDescription())
                .openTime(store.getOpenTime() != null ? store.getOpenTime().toString() : null)
                .closeTime(store.getCloseTime() != null ? store.getCloseTime().toString() : null)
                .longitude(store.getLongitude())
                .latitude(store.getLatitude())
                .rating(store.getRating())
                .status(store.getStatus())
                .rooms(roomInfos)
                .employeeCount(employeeCount.intValue())
                .reviewCount(reviewCount.intValue())
                .build();
        
        return detailVO;
    }

    @Override
    public void batchDelete(List<Long> ids) {
        storeMapper.deleteBatchIds(ids);
        // 批量删除缓存
        ids.forEach(id -> {
            String cacheKey = RedisConstant.STORE_CACHE_PREFIX + id;
            redisTemplate.delete(cacheKey);
        });
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Store store = new Store();
        store.setId(id);
        store.setStatus(status);
        storeMapper.updateById(store);
        
        // 删除缓存
        String cacheKey = RedisConstant.STORE_CACHE_PREFIX + id;
        redisTemplate.delete(cacheKey);
    }

    @Override
    public void batchUpdateStatus(List<Long> ids, Integer status) {
        ids.forEach(id -> updateStatus(id, status));
    }

    @Override
    public StoreStatisticsVO getStatistics() {
        // 统计门店总数
        Long totalStores = storeMapper.selectCount(null);
        
        // 统计营业中门店数
        LambdaQueryWrapper<Store> openWrapper = new LambdaQueryWrapper<>();
        openWrapper.eq(Store::getStatus, 1);
        Long openStores = storeMapper.selectCount(openWrapper);
        
        // 统计停业门店数
        Long closedStores = totalStores - openStores;
        
        // 统计总房间数
        Long totalRooms = storeRoomMapper.selectCount(null);
        
        // 统计可用房间数
        LambdaQueryWrapper<StoreRoom> availableWrapper = new LambdaQueryWrapper<>();
        availableWrapper.eq(StoreRoom::getStatus, 1);
        Long availableRooms = storeRoomMapper.selectCount(availableWrapper);
        
        // 计算平均评分
        List<Store> allStores = storeMapper.selectList(null);
        BigDecimal averageRating = allStores.stream()
                .map(Store::getRating)
                .filter(rating -> rating != null && rating.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(allStores.size()), 2, BigDecimal.ROUND_HALF_UP);
        
        // 统计员工数
        Long totalEmployees = storeEmployeeMapper.selectCount(null);
        
        // 统计在职员工数
        LambdaQueryWrapper<com.murder.pojo.entity.StoreEmployee> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.eq(com.murder.pojo.entity.StoreEmployee::getStatus, 1);
        Long activeEmployees = storeEmployeeMapper.selectCount(activeWrapper);
        
        return StoreStatisticsVO.builder()
                .totalStores(totalStores)
                .openStores(openStores)
                .closedStores(closedStores)
                .totalRooms(totalRooms)
                .availableRooms(availableRooms)
                .averageRating(averageRating)
                .totalEmployees(totalEmployees)
                .activeEmployees(activeEmployees)
                .build();
    }

    @Override
    public List<StoreVO> listAll() {
        List<Store> stores = storeMapper.selectList(null);
        return stores.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取热门门店
     */
    @Override
    public List<StoreVO> getHotStores() {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getStatus, 1);
        wrapper.orderByDesc(Store::getCreateTime);
        wrapper.last("LIMIT 10");
        List<Store> stores = storeMapper.selectList(wrapper);
        return stores.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取推荐门店
     */
    @Override
    public List<StoreVO> getRecommendedStores() {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getStatus, 1);
        wrapper.orderByDesc(Store::getCreateTime);
        wrapper.last("LIMIT 10");
        List<Store> stores = storeMapper.selectList(wrapper);
        return stores.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 实体转VO
     */
    private StoreVO convertToVO(Store store) {
        if (store == null) {
            return null;
        }
        StoreVO storeVO = new StoreVO();
        BeanUtils.copyProperties(store, storeVO);
        // 手动转换LocalTime为String
        if (store.getOpenTime() != null) {
            storeVO.setOpenTime(store.getOpenTime().toString());
        }
        if (store.getCloseTime() != null) {
            storeVO.setCloseTime(store.getCloseTime().toString());
        }
        return storeVO;
    }
}
