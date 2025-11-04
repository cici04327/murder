<template>
  <div class="statistics-dashboard">
    <div class="dashboard-header">
      <h1 class="title">数据统计大屏 - 控制台</h1>
      <div class="header-actions">
        <el-button type="primary" :icon="Refresh" circle @click="refreshData" />
        <el-button type="success" :icon="FullScreen" circle @click="toggleFullScreen" />
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="metrics-cards">
      <div class="metric-card revenue">
        <div class="metric-icon">💰</div>
        <div class="metric-content">
          <div class="metric-label">今日营业额</div>
          <div class="metric-value">¥{{ formatNumber(overview.todayRevenue) }}</div>
          <div class="metric-growth" :class="overview.revenueGrowth >= 0 ? 'positive' : 'negative'">
            <span>{{ overview.revenueGrowth >= 0 ? '↑' : '↓' }}</span>
            {{ Math.abs(overview.revenueGrowth) }}%
          </div>
        </div>
      </div>

      <div class="metric-card reservations">
        <div class="metric-icon">📅</div>
        <div class="metric-content">
          <div class="metric-label">今日预约数</div>
          <div class="metric-value">{{ overview.todayReservations }}</div>
          <div class="metric-growth" :class="overview.reservationGrowth >= 0 ? 'positive' : 'negative'">
            <span>{{ overview.reservationGrowth >= 0 ? '↑' : '↓' }}</span>
            {{ Math.abs(overview.reservationGrowth) }}%
          </div>
        </div>
      </div>

      <div class="metric-card users">
        <div class="metric-icon">👥</div>
        <div class="metric-content">
          <div class="metric-label">今日新增用户</div>
          <div class="metric-value">{{ overview.todayNewUsers }}</div>
          <div class="metric-sub">累计 {{ overview.totalUsers }} 人</div>
        </div>
      </div>

      <div class="metric-card stores">
        <div class="metric-icon">🏪</div>
        <div class="metric-content">
          <div class="metric-label">在线门店</div>
          <div class="metric-value">{{ overview.onlineStores }}/{{ overview.totalStores }}</div>
          <div class="metric-sub">营业中</div>
        </div>
      </div>

      <div class="metric-card coupons">
        <div class="metric-icon">🎫</div>
        <div class="metric-content">
          <div class="metric-label">优惠券使用</div>
          <div class="metric-value">{{ overview.todayCouponUsed }}</div>
          <div class="metric-sub">使用率 {{ overview.couponUsageRate }}%</div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <!-- 第一行 -->
      <div class="chart-row">
        <div class="chart-box">
          <div class="chart-header">
            <span class="chart-title">营业额趋势</span>
            <el-radio-group v-model="chartPeriod" size="small" @change="loadCharts">
              <el-radio-button label="7">近7天</el-radio-button>
              <el-radio-button label="30">近30天</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="revenueChart" class="chart-content"></div>
        </div>

        <div class="chart-box">
          <div class="chart-header">
            <span class="chart-title">门店业绩排行</span>
          </div>
          <div ref="storeRankChart" class="chart-content"></div>
        </div>

        <div class="chart-box">
          <div class="chart-header">
            <span class="chart-title">剧本热度 TOP10</span>
          </div>
          <div ref="scriptRankChart" class="chart-content"></div>
        </div>
      </div>

      <!-- 第二行 -->
      <div class="chart-row">
        <div class="chart-box">
          <div class="chart-header">
            <span class="chart-title">预约来源分布</span>
          </div>
          <div ref="sourceChart" class="chart-content"></div>
        </div>

        <div class="chart-box">
          <div class="chart-header">
            <span class="chart-title">用户增长趋势</span>
          </div>
          <div ref="userGrowthChart" class="chart-content"></div>
        </div>

        <div class="chart-box">
          <div class="chart-header">
            <span class="chart-title">会员等级分布</span>
          </div>
          <div ref="memberChart" class="chart-content"></div>
        </div>
      </div>
    </div>

    <!-- 实时数据流 -->
    <div class="realtime-container">
      <div class="realtime-box">
        <div class="realtime-header">
          <span class="realtime-icon">🔴</span>
          <span class="realtime-title">实时预约动态</span>
        </div>
        <div class="realtime-content scrolling">
          <div v-for="(item, index) in realtime.recentReservations" :key="index" class="realtime-item">
            <span class="item-user">{{ item.userNickname }}</span>
            预约了
            <span class="item-script">《{{ item.scriptName }}》</span>
            -
            <span class="item-store">{{ item.storeName }}</span>
          </div>
        </div>
      </div>

      <div class="realtime-box">
        <div class="realtime-header">
          <span class="realtime-icon">⭐</span>
          <span class="realtime-title">今日热门剧本</span>
        </div>
        <div class="realtime-content">
          <div v-for="(item, index) in realtime.hotScripts" :key="index" class="realtime-item">
            <span class="hot-rank">{{ index + 1 }}.</span>
            <span class="hot-name">{{ item.name }}</span>
            <span class="hot-count">{{ item.todayCount }}场</span>
          </div>
        </div>
      </div>

      <div class="realtime-box">
        <div class="realtime-header">
          <span class="realtime-icon">🎁</span>
          <span class="realtime-title">优惠券使用</span>
        </div>
        <div class="realtime-content scrolling">
          <div v-for="(item, index) in realtime.recentCouponUses" :key="index" class="realtime-item">
            <span class="item-user">{{ item.userNickname }}</span>
            使用了
            <span class="item-coupon">{{ item.couponName }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { Refresh, FullScreen } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

// 数据
const overview = reactive({
  todayRevenue: 0,
  revenueGrowth: 0,
  todayReservations: 0,
  reservationGrowth: 0,
  todayNewUsers: 0,
  totalUsers: 0,
  onlineStores: 0,
  totalStores: 0,
  todayCouponUsed: 0,
  couponUsageRate: 0
})

const chartPeriod = ref('7')
const charts = reactive({})
const rankings = reactive({ storeRankings: [], scriptRankings: [] })
const realtime = reactive({ 
  recentReservations: [], 
  hotScripts: [], 
  recentCouponUses: [] 
})

// 图表refs
const revenueChart = ref(null)
const storeRankChart = ref(null)
const scriptRankChart = ref(null)
const sourceChart = ref(null)
const userGrowthChart = ref(null)
const memberChart = ref(null)

let refreshInterval = null

// 加载概览数据
const loadOverview = async () => {
  try {
    const { data } = await request.get('/statistics/overview')
    Object.assign(overview, data)
  } catch (error) {
    console.error('加载概览数据失败', error)
    // 使用默认值，避免页面崩溃
    Object.assign(overview, {
      todayRevenue: 0,
      revenueGrowth: 0,
      todayReservations: 0,
      reservationGrowth: 0,
      todayNewUsers: 0,
      totalUsers: 0,
      onlineStores: 0,
      totalStores: 0,
      todayCouponUsed: 0,
      couponUsageRate: 0
    })
  }
}

// 加载图表数据
const loadCharts = async () => {
  try {
    const { data } = await request.get('/statistics/charts', {
      params: { days: chartPeriod.value }
    })
    Object.assign(charts, data)
    initCharts()
  } catch (error) {
    console.error('加载图表数据失败', error)
    // 使用空数据，避免页面崩溃
    Object.assign(charts, {
      revenueDates: [],
      revenueAmounts: [],
      reservationSource: {},
      userGrowthDates: [],
      userGrowthCounts: [],
      memberLevelDistribution: {}
    })
    initCharts()
  }
}

// 加载排行榜数据
const loadRankings = async () => {
  try {
    const { data } = await request.get('/statistics/rankings', {
      params: { limit: 10 }
    })
    Object.assign(rankings, data)
    updateRankingCharts()
  } catch (error) {
    console.error('加载排行榜数据失败', error)
    // 使用空数据，避免页面崩溃
    Object.assign(rankings, {
      storeRankings: [],
      scriptRankings: []
    })
    updateRankingCharts()
  }
}

// 加载实时数据
const loadRealtime = async () => {
  try {
    const { data } = await request.get('/statistics/realtime', {
      params: { limit: 10 }
    })
    Object.assign(realtime, data)
  } catch (error) {
    console.error('加载实时数据失败', error)
    // 使用空数据，避免页面崩溃
    Object.assign(realtime, {
      recentReservations: [],
      hotScripts: [],
      recentCouponUses: []
    })
  }
}

// 初始化图表
const initCharts = () => {
  // 营业额趋势图
  if (revenueChart.value) {
    const chart = echarts.init(revenueChart.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        data: charts.revenueDates || [],
        axisLine: { lineStyle: { color: '#666' } }
      },
      yAxis: {
        type: 'value',
        axisLine: { lineStyle: { color: '#666' } }
      },
      series: [{
        data: charts.revenueAmounts || [],
        type: 'line',
        smooth: true,
        areaStyle: { 
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(102, 126, 234, 0.6)' },
            { offset: 1, color: 'rgba(102, 126, 234, 0.1)' }
          ])
        },
        lineStyle: { color: '#667eea', width: 3 },
        itemStyle: { color: '#667eea' }
      }]
    })
  }

  // 预约来源分布
  if (sourceChart.value) {
    const chart = echarts.init(sourceChart.value)
    const sourceData = Object.entries(charts.reservationSource || {}).map(([name, value]) => ({
      name, value
    }))
    chart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 10, textStyle: { color: '#666' } },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: { show: true },
        data: sourceData,
        color: ['#667eea', '#764ba2', '#f093fb']
      }]
    })
  }

  // 用户增长趋势
  if (userGrowthChart.value) {
    const chart = echarts.init(userGrowthChart.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        data: charts.userGrowthDates || [],
        axisLine: { lineStyle: { color: '#666' } }
      },
      yAxis: {
        type: 'value',
        axisLine: { lineStyle: { color: '#666' } }
      },
      series: [{
        data: charts.userGrowthCounts || [],
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(118, 75, 162, 0.6)' },
            { offset: 1, color: 'rgba(118, 75, 162, 0.1)' }
          ])
        },
        lineStyle: { color: '#764ba2', width: 3 },
        itemStyle: { color: '#764ba2' }
      }]
    })
  }

  // 会员等级分布
  if (memberChart.value) {
    const chart = echarts.init(memberChart.value)
    const memberData = Object.entries(charts.memberLevelDistribution || {}).map(([name, value]) => ({
      name, value
    }))
    chart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 10, textStyle: { color: '#666' } },
      series: [{
        type: 'pie',
        radius: ['50%', '80%'],
        data: memberData,
        color: ['#cd7f32', '#c0c0c0', '#ffd700', '#b9f2ff']
      }]
    })
  }
}

// 更新排行榜图表
const updateRankingCharts = () => {
  // 门店业绩排行
  if (storeRankChart.value && rankings.storeRankings) {
    const chart = echarts.init(storeRankChart.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'value',
        axisLine: { lineStyle: { color: '#666' } }
      },
      yAxis: {
        type: 'category',
        data: rankings.storeRankings.map(item => item.name).reverse(),
        axisLine: { lineStyle: { color: '#666' } }
      },
      series: [{
        type: 'bar',
        data: rankings.storeRankings.map(item => item.revenue).reverse(),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ])
        },
        barWidth: '60%'
      }]
    })
  }

  // 剧本热度排行
  if (scriptRankChart.value && rankings.scriptRankings) {
    const chart = echarts.init(scriptRankChart.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'value',
        axisLine: { lineStyle: { color: '#666' } }
      },
      yAxis: {
        type: 'category',
        data: rankings.scriptRankings.map(item => item.name).reverse(),
        axisLine: { lineStyle: { color: '#666' } }
      },
      series: [{
        type: 'bar',
        data: rankings.scriptRankings.map(item => item.bookingCount).reverse(),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#f093fb' },
            { offset: 1, color: '#f5576c' }
          ])
        },
        barWidth: '60%'
      }]
    })
  }
}

// 刷新所有数据
const refreshData = () => {
  loadOverview()
  loadCharts()
  loadRankings()
  loadRealtime()
}

// 全屏切换
const toggleFullScreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

// 格式化数字
const formatNumber = (num) => {
  return (num || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

onMounted(() => {
  refreshData()
  // 每30秒自动刷新
  refreshInterval = setInterval(refreshData, 30000)
})

onUnmounted(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
  }
})
</script>

<style scoped>
.statistics-dashboard {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  color: #fff;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.title {
  font-size: 32px;
  font-weight: bold;
  margin: 0;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
}

.header-actions {
  display: flex;
  gap: 10px;
}

/* 指标卡片 */
.metrics-cards {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.metric-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}

.metric-card:hover {
  transform: translateY(-5px);
}

.metric-icon {
  font-size: 48px;
}

.metric-content {
  flex: 1;
}

.metric-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.metric-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.metric-growth {
  font-size: 14px;
  font-weight: bold;
}

.metric-growth.positive {
  color: #67c23a;
}

.metric-growth.negative {
  color: #f56c6c;
}

.metric-sub {
  font-size: 12px;
  color: #999;
}

/* 图表容器 */
.charts-container {
  margin-bottom: 20px;
}

.chart-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.chart-box {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.chart-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.chart-content {
  height: 300px;
}

/* 实时数据流 */
.realtime-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.realtime-box {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.realtime-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.realtime-icon {
  font-size: 20px;
}

.realtime-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.realtime-content {
  max-height: 200px;
  overflow-y: auto;
}

.realtime-content.scrolling {
  animation: scroll-up 20s linear infinite;
}

.realtime-item {
  padding: 10px;
  margin-bottom: 8px;
  background: #f8f9fa;
  border-radius: 8px;
  font-size: 14px;
  color: #333;
}

.item-user, .item-script, .item-store, .item-coupon {
  font-weight: bold;
  color: #667eea;
}

.hot-rank {
  font-weight: bold;
  color: #f5576c;
  margin-right: 8px;
}

.hot-name {
  color: #333;
  margin-right: 10px;
}

.hot-count {
  color: #667eea;
  font-weight: bold;
  float: right;
}

@keyframes scroll-up {
  0% {
    transform: translateY(0);
  }
  100% {
    transform: translateY(-50%);
  }
}

/* 响应式 */
@media (max-width: 1600px) {
  .metrics-cards {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 1200px) {
  .chart-row {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .realtime-container {
    grid-template-columns: 1fr;
  }
}
</style>
