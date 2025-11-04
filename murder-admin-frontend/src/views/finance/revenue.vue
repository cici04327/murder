<template>
  <div class="finance-revenue">
    <!-- 数据概览卡片 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="revenue-card today">
          <div class="card-icon">💰</div>
          <div class="card-content">
            <div class="card-label">今日营收</div>
            <div class="card-value">¥{{ formatMoney(overview.todayRevenue) }}</div>
            <div class="card-growth" :class="overview.todayGrowth >= 0 ? 'positive' : 'negative'">
              <span>{{ overview.todayGrowth >= 0 ? '↑' : '↓' }}</span>
              {{ Math.abs(overview.todayGrowth) }}%
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="revenue-card week">
          <div class="card-icon">📊</div>
          <div class="card-content">
            <div class="card-label">本周营收</div>
            <div class="card-value">¥{{ formatMoney(overview.weekRevenue) }}</div>
            <div class="card-growth" :class="overview.weekGrowth >= 0 ? 'positive' : 'negative'">
              <span>{{ overview.weekGrowth >= 0 ? '↑' : '↓' }}</span>
              {{ Math.abs(overview.weekGrowth) }}%
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="revenue-card month">
          <div class="card-icon">📈</div>
          <div class="card-content">
            <div class="card-label">本月营收</div>
            <div class="card-value">¥{{ formatMoney(overview.monthRevenue) }}</div>
            <div class="card-growth" :class="overview.monthGrowth >= 0 ? 'positive' : 'negative'">
              <span>{{ overview.monthGrowth >= 0 ? '↑' : '↓' }}</span>
              {{ Math.abs(overview.monthGrowth) }}%
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="revenue-card total">
          <div class="card-icon">💎</div>
          <div class="card-content">
            <div class="card-label">累计营收</div>
            <div class="card-value">¥{{ formatMoney(overview.totalRevenue) }}</div>
            <div class="card-desc">持续增长中</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 营收趋势图表 -->
    <el-card class="chart-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>营收趋势</span>
          <el-radio-group v-model="chartType" size="small">
            <el-radio-button value="day">日</el-radio-button>
            <el-radio-button value="week">周</el-radio-button>
            <el-radio-button value="month">月</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div ref="chartRef" style="width: 100%; height: 400px;"></div>
    </el-card>

    <!-- 门店营收排行 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <span>门店营收排行</span>
          </template>
          <el-table :data="storeRanking" style="width: 100%">
            <el-table-column type="index" label="排名" width="60" />
            <el-table-column prop="storeName" label="门店名称" />
            <el-table-column label="营收" align="right">
              <template #default="{ row }">
                <span class="money-text">¥{{ formatMoney(row.revenue) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="占比" width="100" align="right">
              <template #default="{ row }">
                <el-tag type="success" size="small">{{ row.percentage }}%</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 剧本营收排行 -->
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <span>剧本营收排行</span>
          </template>
          <el-table :data="scriptRanking" style="width: 100%">
            <el-table-column type="index" label="排名" width="60" />
            <el-table-column prop="scriptName" label="剧本名称" />
            <el-table-column label="营收" align="right">
              <template #default="{ row }">
                <span class="money-text">¥{{ formatMoney(row.revenue) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="预约次数" width="90" align="right">
              <template #default="{ row }">
                <el-tag type="primary" size="small">{{ row.count }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 营收明细列表 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>营收明细</span>
          <el-button type="primary" size="small" @click="handleExport">导出报表</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="门店">
          <el-select v-model="queryForm.storeId" placeholder="请选择门店" clearable style="width: 160px">
            <el-option label="全部" :value="null" />
            <el-option v-for="store in storeOptions" :key="store.id" :label="store.name" :value="store.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="storeName" label="门店" width="140" />
        <el-table-column prop="scriptName" label="剧本" width="150" />
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column label="金额" width="120" align="right">
          <template #default="{ row }">
            <span class="money-text">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="支付方式" width="100">
          <template #default="{ row }">
            <el-tag :type="getPaymentType(row.paymentMethod)" size="small">
              {{ row.paymentMethod }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="playTime" label="游玩时间" width="160" />
        <el-table-column prop="createTime" label="支付时间" width="160" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const loading = ref(false)
const chartRef = ref(null)
const chartType = ref('day')
const dateRange = ref([])
let chartInstance = null

// 概览数据
const overview = reactive({
  todayRevenue: 15680,
  todayGrowth: 12.5,
  weekRevenue: 89420,
  weekGrowth: 8.3,
  monthRevenue: 356780,
  monthGrowth: 15.7,
  totalRevenue: 2456890
})

// 门店排行
const storeRanking = ref([
  { storeName: '万达广场店', revenue: 125680, percentage: 35.2 },
  { storeName: '大悦城店', revenue: 98450, percentage: 27.6 },
  { storeName: '银泰城店', revenue: 76320, percentage: 21.4 },
  { storeName: '星光天地店', revenue: 56330, percentage: 15.8 }
])

// 剧本排行
const scriptRanking = ref([
  { scriptName: '午夜铃声', revenue: 45680, count: 156 },
  { scriptName: '雾都迷案', revenue: 38920, count: 134 },
  { scriptName: '岁月回声', revenue: 32150, count: 98 },
  { scriptName: '棋局', revenue: 28640, count: 87 },
  { scriptName: '浮生录', revenue: 24350, count: 76 }
])

// 门店选项
const storeOptions = ref([
  { id: 1, name: '万达广场店' },
  { id: 2, name: '大悦城店' },
  { id: 3, name: '银泰城店' },
  { id: 4, name: '星光天地店' }
])

// 查询表单
const queryForm = reactive({
  storeId: null,
  page: 1,
  pageSize: 10
})

const total = ref(0)

// 表格数据
const tableData = ref([
  {
    orderNo: 'ORD20251103001',
    storeName: '万达广场店',
    scriptName: '午夜铃声',
    userName: '张三',
    amount: 168,
    paymentMethod: '微信支付',
    playTime: '2025-11-03 14:00',
    createTime: '2025-11-03 10:25',
    status: '已完成'
  },
  {
    orderNo: 'ORD20251103002',
    storeName: '大悦城店',
    scriptName: '雾都迷案',
    userName: '李四',
    amount: 198,
    paymentMethod: '支付宝',
    playTime: '2025-11-03 16:00',
    createTime: '2025-11-03 11:30',
    status: '已完成'
  },
  {
    orderNo: 'ORD20251103003',
    storeName: '银泰城店',
    scriptName: '岁月回声',
    userName: '王五',
    amount: 228,
    paymentMethod: '微信支付',
    playTime: '2025-11-03 18:00',
    createTime: '2025-11-03 12:15',
    status: '已完成'
  }
])

const formatMoney = (value) => {
  return String(value).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const getPaymentType = (method) => {
  const types = {
    '微信支付': 'success',
    '支付宝': 'primary',
    '现金': 'warning',
    '银行卡': 'info'
  }
  return types[method] || 'info'
}

const getStatusType = (status) => {
  const types = {
    '已完成': 'success',
    '进行中': 'primary',
    '已取消': 'info',
    '已退款': 'warning'
  }
  return types[status] || 'info'
}

const handleQuery = () => {
  queryForm.page = 1
  fetchList()
}

const handleReset = () => {
  queryForm.storeId = null
  dateRange.value = []
  queryForm.page = 1
  fetchList()
}

const handleSizeChange = (newSize) => {
  queryForm.pageSize = newSize
  queryForm.page = 1
  fetchList()
}

const handlePageChange = (newPage) => {
  queryForm.page = newPage
  fetchList()
}

const handleExport = () => {
  ElMessage.success('导出功能开发中...')
}

const fetchList = () => {
  // TODO: 调用后端API获取数据
  total.value = tableData.value.length
}

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return
  
  chartInstance = echarts.init(chartRef.value)
  updateChart()
}

// 更新图表
const updateChart = () => {
  if (!chartInstance) return

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: getChartXData(),
      axisTick: {
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '营收',
        type: 'bar',
        barWidth: '60%',
        data: getChartYData(),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }
    ]
  }

  chartInstance.setOption(option)
}

const getChartXData = () => {
  if (chartType.value === 'day') {
    return ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  } else if (chartType.value === 'week') {
    return ['第1周', '第2周', '第3周', '第4周']
  } else {
    return ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  }
}

const getChartYData = () => {
  if (chartType.value === 'day') {
    return [8520, 12360, 15480, 18920, 22560, 28940, 32180]
  } else if (chartType.value === 'week') {
    return [68450, 75320, 82190, 89420]
  } else {
    return [125680, 138920, 156780, 172340, 189560, 205420, 228960, 245780, 268340, 289560, 312480, 356780]
  }
}

// 监听图表类型变化
watch(chartType, () => {
  updateChart()
})

onMounted(() => {
  fetchList()
  setTimeout(() => {
    initChart()
  }, 100)

  // 窗口大小改变时重新渲染图表
  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
})
</script>

<style scoped>
.finance-revenue {
  width: 100%;
}

.overview-cards {
  margin-bottom: 0;
}

.revenue-card {
  display: flex;
  align-items: center;
  padding: 10px;
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.revenue-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 20px;
}

.card-icon {
  font-size: 48px;
  margin-right: 20px;
}

.card-content {
  flex: 1;
}

.card-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.card-growth {
  font-size: 12px;
  font-weight: bold;
}

.card-growth.positive {
  color: #67c23a;
}

.card-growth.negative {
  color: #f56c6c;
}

.card-desc {
  font-size: 12px;
  color: #909399;
}

.chart-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.money-text {
  color: #f56c6c;
  font-weight: bold;
}

.query-form {
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .revenue-card :deep(.el-card__body) {
    padding: 15px;
  }

  .card-icon {
    font-size: 36px;
    margin-right: 15px;
  }

  .card-value {
    font-size: 20px;
  }
}
</style>

