package com.murder.store.service;

import com.murder.common.result.PageResult;
import com.murder.pojo.dto.StoreQueryDTO;
import com.murder.pojo.entity.Store;
import com.murder.pojo.vo.StoreDetailVO;
import com.murder.pojo.vo.StoreStatisticsVO;
import com.murder.pojo.vo.StoreVO;

import java.util.List;

/**
 * 门店服务接口
 */
public interface StoreService {

    /**
     * 分页查询门店列表
     */
    PageResult<StoreVO> pageQuery(Integer page, Integer pageSize, String name);

    /**
     * 多条件分页查询门店列表
     */
    PageResult<StoreVO> pageQueryAdvanced(StoreQueryDTO queryDTO);

    /**
     * 根据ID查询门店详情
     */
    StoreVO getById(Long id);

    /**
     * 根据ID查询门店详细信息（包含房间、员工、评价统计）
     */
    StoreDetailVO getDetailById(Long id);

    /**
     * 新增门店
     */
    void add(Store store);

    /**
     * 更新门店
     */
    void update(Store store);

    /**
     * 删除门店
     */
    void delete(Long id);

    /**
     * 批量删除门店
     */
    void batchDelete(List<Long> ids);

    /**
     * 更新门店状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 批量更新门店状态
     */
    void batchUpdateStatus(List<Long> ids, Integer status);

    /**
     * 获取门店统计信息
     */
    StoreStatisticsVO getStatistics();

    /**
     * 获取所有门店列表（不分页）
     */
    List<StoreVO> listAll();
    
    /**
     * 获取热门门店
     */
    List<StoreVO> getHotStores();
    
    /**
     * 获取推荐门店
     */
    List<StoreVO> getRecommendedStores();
}
