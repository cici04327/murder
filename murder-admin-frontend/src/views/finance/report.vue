<template>
  <div class="finance-report">
    <!-- 报表类型选择 -->
    <el-card>
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="报表类型">
          <el-select v-model="queryForm.reportType" style="width: 160px" @change="handleReportTypeChange">
            <el-option label="日报" value="daily" />
            <el-option label="周报" value="weekly" />
            <el-option label="月报" value="monthly" />
            <el-option label="年报" value="yearly" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="queryForm.dateRange"
            :type="getDatePickerType()"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 280px"
          />
        </el-form-item>
        <el-form-item label="门店">
          <el-select v-model="queryForm.storeId" placeholder="全部门店" clearable style="width: 160px">
            <el-option label="全部" :value="null" />
            <el-option v-for="store in storeOptions" :key="store.id" :label="store.name" :value="store.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleGenerate">生成报表</el-button>
          <el-button type="success" @click="handleExport">导出Excel</el-button>
          <el-button @click="handlePrint">打印</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 报表摘要 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>{{ reportTitle }}</span>
          <span class="report-time">生成时间：{{ generateTime }}</span>
        </div>
      </template>

      <el-row :gutter="20" class="summary-section">
        <el-col :xs="24" :sm="12" :md="8">
          <div class="summary-item">
            <div class="summary-label">总营收</div>
            <div class="summary-value revenue">¥{{ formatMoney(summary.totalRevenue) }}</div>
            <div class="summary-compare">
              <span :class="summary.revenueGrowth >= 0 ? 'positive' : 'negative'">
                {{ summary.revenueGrowth >= 0 ? '↑' : '↓' }} {{ Math.abs(summary.revenueGrowth) }}%
              </span>
              <span class="compare-text">较上期</span>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <div class="summary-item">
            <div class="summary-label">订单总数</div>
            <div class="summary-value orders">{{ summary.totalOrders }}</div>
            <div class="summary-compare">
              <span :class="summary.ordersGrowth >= 0 ? 'positive' : 'negative'">
                {{ summary.ordersGrowth >= 0 ? '↑' : '↓' }} {{ Math.abs(summary.ordersGrowth) }}%
              </span>
              <span class="compare-text">较上期</span>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <div class="summary-item">
            <div class="summary-label">客单价</div>
            <div class="summary-value price">¥{{ summary.avgPrice }}</div>
            <div class="summary-compare">
              <span :class="summary.priceGrowth >= 0 ? 'positive' : 'negative'">
                {{ summary.priceGrowth >= 0 ? '↑' : '↓' }} {{ Math.abs(summary.priceGrowth) }}%
              </span>
              <span class="compare-text">较上期</span>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-divider />

      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <div class="metric-item">
            <div class="metric-label">退款金额</div>
            <div class="metric-value">¥{{ formatMoney(summary.refundAmount) }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="metric-item">
            <div class="metric-label">实收金额</div>
            <div class="metric-value">¥{{ formatMoney(summary.actualRevenue) }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="metric-item">
            <div class="metric-label">新增用户</div>
            <div class="metric-value">{{ summary.newUsers }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="metric-item">
            <div class="metric-label">活跃用户</div>
            <div class="metric-value">{{ summary.activeUsers }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 明细表格 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <span>营收明细</span>
      </template>

      <el-table :data="detailData" style="width: 100%" show-summary :summary-method="getSummaries">
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="orderCount" label="订单数" width="100" align="right" />
        <el-table-column label="营收金额" width="140" align="right">
          <template #default="{ row }">
            <span class="money-text">¥{{ formatMoney(row.revenue) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="退款金额" width="140" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c;">¥{{ formatMoney(row.refund) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="实收金额" width="140" align="right">
          <template #default="{ row }">
            <span style="color: #67c23a; font-weight: bold;">¥{{ formatMoney(row.actual) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="客单价" width="120" align="right">
          <template #default="{ row }">
            <span>¥{{ row.avgPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column label="新增用户" width="100" align="right">
          <template #default="{ row }">
            {{ row.newUsers }}
          </template>
        </el-table-column>
        <el-table-column label="活跃用户" width="100" align="right">
          <template #default="{ row }">
            {{ row.activeUsers }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" />
      </el-table>
    </el-card>

    <!-- 图表分析 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <span>营收趋势</span>
          </template>
          <div ref="revenueTrendRef" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <span>收入构成</span>
          </template>
          <div ref="revenueCompositionRef" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const revenueTrendRef = ref(null)
const revenueCompositionRef = ref(null)
let revenueTrendChart = null
let compositionChart = null

const storeOptions = ref([
  { id: 1, name: '万达广场店' },
  { id: 2, name: '大悦城店' },
  { id: 3, name: '银泰城店' },
  { id: 4, name: '星光天地店' }
])

const queryForm = reactive({
  reportType: 'daily',
  dateRange: [],
  storeId: null
})

const generateTime = ref(new Date().toLocaleString('zh-CN'))

const reportTitle = computed(() => {
  const types = {
    'daily': '日报表',
    'weekly': '周报表',
    'monthly': '月报表',
    'yearly': '年报表'
  }
  return types[queryForm.reportType] || '财务报表'
})

const summary = reactive({
  totalRevenue: 356780,
  revenueGrowth: 15.7,
  totalOrders: 1842,
  ordersGrowth: 12.3,
  avgPrice: 193.7,
  priceGrowth: 3.1,
  refundAmount: 5264,
  actualRevenue: 351516,
  newUsers: 256,
  activeUsers: 1024
})

const detailData = ref([
  {
    date: '2025-11-01',
    orderCount: 68,
    revenue: 12580,
    refund: 168,
    actual: 12412,
    avgPrice: 185,
    newUsers: 12,
    activeUsers: 56,
    remark: '周五，预约较多'
  },
  {
    date: '2025-11-02',
    orderCount: 92,
    revenue: 18920,
    refund: 396,
    actual: 18524,
    avgPrice: 206,
    newUsers: 18,
    activeUsers: 78,
    remark: '周六，客流高峰'
  },
  {
    date: '2025-11-03',
    orderCount: 85,
    revenue: 16480,
    refund: 228,
    actual: 16252,
    avgPrice: 194,
    newUsers: 15,
    activeUsers: 71,
    remark: '周日，正常营业'
  }
])

const formatMoney = (value) => {
  return String(value).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const getDatePickerType = () => {
  const types = {
    'daily': 'daterange',
    'weekly': 'week',
    'monthly': 'monthrange',
    'yearly': 'year'
  }
  return types[queryForm.reportType] || 'daterange'
}

const handleReportTypeChange = () => {
  queryForm.dateRange = []
}

const handleGenerate = () => {
  generateTime.value = new Date().toLocaleString('zh-CN')
  ElMessage.success('报表生成成功')
  // TODO: 调用后端API生成报表
}

const handleExport = () => {
  ElMessage.success('导出Excel功能开发中...')
  // TODO: 调用导出API
}

const handlePrint = () => {
  window.print()
}

const getSummaries = (param) => {
  const { columns, data } = param
  const sums = []
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    const values = data.map(item => Number(item[column.property]))
    if (column.property === 'orderCount' || column.property === 'revenue' || 
        column.property === 'refund' || column.property === 'actual' ||
        column.property === 'newUsers' || column.property === 'activeUsers') {
      sums[index] = values.reduce((prev, curr) => {
        const value = Number(curr)
        if (!isNaN(value)) {
          return prev + curr
        } else {
          return prev
        }
      }, 0)
      if (column.property === 'revenue' || column.property === 'refund' || column.property === 'actual') {
        sums[index] = '¥' + formatMoney(sums[index])
      }
    } else if (column.property === 'avgPrice') {
      const totalRevenue = data.reduce((sum, item) => sum + Number(item.revenue), 0)
      const totalOrders = data.reduce((sum, item) => sum + Number(item.orderCount), 0)
      sums[index] = '¥' + (totalRevenue / totalOrders).toFixed(2)
    } else {
      sums[index] = '-'
    }
  })
  return sums
}

const initCharts = () => {
  // 营收趋势图
  if (revenueTrendRef.value) {
    revenueTrendChart = echarts.init(revenueTrendRef.value)
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['营收', '实收']
      },
      xAxis: {
        type: 'category',
        data: detailData.value.map(d => d.date)
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
          type: 'line',
          data: detailData.value.map(d => d.revenue),
          smooth: true,
          itemStyle: { color: '#409EFF' }
        },
        {
          name: '实收',
          type: 'line',
          data: detailData.value.map(d => d.actual),
          smooth: true,
          itemStyle: { color: '#67C23A' }
        }
      ]
    }
    revenueTrendChart.setOption(option)
  }

  // 收入构成图
  if (revenueCompositionRef.value) {
    compositionChart = echarts.init(revenueCompositionRef.value)
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: ¥{c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '收入构成',
          type: 'pie',
          radius: '60%',
          data: [
            { value: 125680, name: '万达广场店' },
            { value: 98450, name: '大悦城店' },
            { value: 76320, name: '银泰城店' },
            { value: 56330, name: '星光天地店' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    compositionChart.setOption(option)
  }
}

onMounted(() => {
  setTimeout(() => {
    initCharts()
  }, 100)

  window.addEventListener('resize', () => {
    revenueTrendChart?.resize()
    compositionChart?.resize()
  })
})
</script>

<style scoped>
.finance-report {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.report-time {
  font-size: 12px;
  color: #909399;
}

.summary-section {
  margin-bottom: 20px;
}

.summary-item {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  border-radius: 8px;
}

.summary-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.summary-value {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
}

.summary-value.revenue {
  color: #409EFF;
}

.summary-value.orders {
  color: #67C23A;
}

.summary-value.price {
  color: #E6A23C;
}

.summary-compare {
  font-size: 12px;
}

.summary-compare .positive {
  color: #67c23a;
  font-weight: bold;
}

.summary-compare .negative {
  color: #f56c6c;
  font-weight: bold;
}

.summary-compare .compare-text {
  color: #909399;
  margin-left: 5px;
}

.metric-item {
  text-align: center;
  padding: 15px;
}

.metric-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.metric-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.money-text {
  color: #409EFF;
  font-weight: bold;
}

@media print {
  .el-card__header,
  .query-form {
    display: none;
  }
}
</style>

