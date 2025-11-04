package com.murder.store.service;

import com.murder.pojo.vo.StatisticsChartsVO;
import com.murder.pojo.vo.StatisticsOverviewVO;
import com.murder.pojo.vo.StatisticsRankingVO;
import com.murder.pojo.vo.StatisticsRealtimeVO;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取统计概览数据
     */
    StatisticsOverviewVO getOverview();

    /**
     * 获取图表数据
     * @param days 天数（7或30）
     */
    StatisticsChartsVO getCharts(Integer days);

    /**
     * 获取排行榜数据
     * @param limit 限制数量
     */
    StatisticsRankingVO getRankings(Integer limit);

    /**
     * 获取实时数据
     * @param limit 限制数量
     */
    StatisticsRealtimeVO getRealtime(Integer limit);
}
