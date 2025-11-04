<template>
  <div class="finance-refund">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #fef0f0;">
            <span style="font-size: 32px;">🔙</span>
          </div>
          <div class="stat-content">
            <div class="stat-label">今日退款</div>
            <div class="stat-value">{{ stats.todayCount }}</div>
            <div class="stat-amount">¥{{ formatMoney(stats.todayAmount) }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #f0f9ff;">
            <span style="font-size: 32px;">📋</span>
          </div>
          <div class="stat-content">
            <div class="stat-label">待处理</div>
            <div class="stat-value">{{ stats.pendingCount }}</div>
            <div class="stat-desc">需要审核</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #f0fdf4;">
            <span style="font-size: 32px;">✅</span>
          </div>
          <div class="stat-content">
            <div class="stat-label">本月退款</div>
            <div class="stat-value">{{ stats.monthCount }}</div>
            <div class="stat-amount">¥{{ formatMoney(stats.monthAmount) }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #fffbeb;">
            <span style="font-size: 32px;">📊</span>
          </div>
          <div class="stat-content">
            <div class="stat-label">退款率</div>
            <div class="stat-value">{{ stats.refundRate }}%</div>
            <div class="stat-desc">低于行业平均</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 退款列表 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <span>退款管理</span>
      </template>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="订单号">
          <el-input v-model="queryForm.orderNo" placeholder="请输入订单号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="用户">
          <el-input v-model="queryForm.userName" placeholder="请输入用户名" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
            <el-option label="已退款" value="refunded" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="refundNo" label="退款单号" width="160" />
        <el-table-column prop="orderNo" label="订单号" width="160" />
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="scriptName" label="剧本" width="140" />
        <el-table-column label="订单金额" width="110" align="right">
          <template #default="{ row }">
            <span>¥{{ row.orderAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="退款金额" width="110" align="right">
          <template #default="{ row }">
            <span class="refund-amount">¥{{ row.refundAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="退款原因" width="200" show-overflow-tooltip />
        <el-table-column prop="applyTime" label="申请时间" width="160" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'pending'" 
              type="primary" 
              size="small" 
              @click="handleApprove(row)"
            >
              通过
            </el-button>
            <el-button 
              v-if="row.status === 'pending'" 
              type="danger" 
              size="small" 
              @click="handleReject(row)"
            >
              拒绝
            </el-button>
            <el-button 
              v-if="row.status === 'approved'" 
              type="success" 
              size="small" 
              @click="handleRefund(row)"
            >
              确认退款
            </el-button>
            <el-button size="small" @click="handleViewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 退款详情对话框 -->
    <el-dialog v-model="detailVisible" title="退款详情" width="600px">
      <el-descriptions :column="2" border v-if="currentRefund">
        <el-descriptions-item label="退款单号">{{ currentRefund.refundNo }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ currentRefund.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户">{{ currentRefund.userName }}</el-descriptions-item>
        <el-descriptions-item label="剧本">{{ currentRefund.scriptName }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentRefund.orderAmount }}</el-descriptions-item>
        <el-descriptions-item label="退款金额">¥{{ currentRefund.refundAmount }}</el-descriptions-item>
        <el-descriptions-item label="申请时间" :span="2">{{ currentRefund.applyTime }}</el-descriptions-item>
        <el-descriptions-item label="退款原因" :span="2">{{ currentRefund.reason }}</el-descriptions-item>
        <el-descriptions-item label="状态" :span="2">
          <el-tag :type="getStatusType(currentRefund.status)">
            {{ getStatusText(currentRefund.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentRefund.handleTime" label="处理时间" :span="2">
          {{ currentRefund.handleTime }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentRefund.handleNote" label="处理备注" :span="2">
          {{ currentRefund.handleNote }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog v-model="approveVisible" title="审核退款" width="500px">
      <el-form :model="approveForm" label-width="80px">
        <el-form-item label="退款金额">
          <el-input-number v-model="approveForm.amount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="approveForm.note" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmApprove">确定</el-button>
      </template>
    </el-dialog>

    <!-- 拒绝对话框 -->
    <el-dialog v-model="rejectVisible" title="拒绝退款" width="500px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="拒绝原因">
          <el-input v-model="rejectForm.reason" type="textarea" :rows="4" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const dateRange = ref([])
const detailVisible = ref(false)
const approveVisible = ref(false)
const rejectVisible = ref(false)
const currentRefund = ref(null)

// 统计数据
const stats = reactive({
  todayCount: 3,
  todayAmount: 594,
  pendingCount: 5,
  monthCount: 28,
  monthAmount: 5264,
  refundRate: 2.3
})

// 查询表单
const queryForm = reactive({
  orderNo: '',
  userName: '',
  status: '',
  page: 1,
  pageSize: 10
})

const total = ref(0)

// 审核表单
const approveForm = reactive({
  amount: 0,
  note: ''
})

// 拒绝表单
const rejectForm = reactive({
  reason: ''
})

// 表格数据
const tableData = ref([
  {
    refundNo: 'RF20251103001',
    orderNo: 'ORD20251101045',
    userName: '张三',
    scriptName: '午夜铃声',
    orderAmount: 168,
    refundAmount: 168,
    reason: '临时有事，无法参加',
    applyTime: '2025-11-03 09:15',
    status: 'pending'
  },
  {
    refundNo: 'RF20251103002',
    orderNo: 'ORD20251101052',
    userName: '李四',
    scriptName: '雾都迷案',
    orderAmount: 198,
    refundAmount: 198,
    reason: '活动取消',
    applyTime: '2025-11-03 10:30',
    status: 'pending'
  },
  {
    refundNo: 'RF20251102008',
    orderNo: 'ORD20251030038',
    userName: '王五',
    scriptName: '岁月回声',
    orderAmount: 228,
    refundAmount: 228,
    reason: '身体不适',
    applyTime: '2025-11-02 14:20',
    status: 'approved',
    handleTime: '2025-11-02 15:10',
    handleNote: '同意退款'
  },
  {
    refundNo: 'RF20251102007',
    orderNo: 'ORD20251030025',
    userName: '赵六',
    scriptName: '棋局',
    orderAmount: 218,
    refundAmount: 218,
    reason: '游戏体验不佳',
    applyTime: '2025-11-02 11:45',
    status: 'refunded',
    handleTime: '2025-11-02 16:30'
  },
  {
    refundNo: 'RF20251101015',
    orderNo: 'ORD20251029012',
    userName: '孙七',
    scriptName: '浮生录',
    orderAmount: 188,
    refundAmount: 0,
    reason: '不合理退款',
    applyTime: '2025-11-01 16:20',
    status: 'rejected',
    handleTime: '2025-11-01 17:00',
    handleNote: '已超过退款时限'
  }
])

const formatMoney = (value) => {
  return String(value).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const getStatusType = (status) => {
  const types = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger',
    'refunded': 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    'pending': '待审核',
    'approved': '已通过',
    'rejected': '已拒绝',
    'refunded': '已退款'
  }
  return texts[status] || status
}

const handleQuery = () => {
  queryForm.page = 1
  fetchList()
}

const handleReset = () => {
  queryForm.orderNo = ''
  queryForm.userName = ''
  queryForm.status = ''
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

const handleViewDetail = (row) => {
  currentRefund.value = row
  detailVisible.value = true
}

const handleApprove = (row) => {
  currentRefund.value = row
  approveForm.amount = row.refundAmount
  approveForm.note = ''
  approveVisible.value = true
}

const confirmApprove = async () => {
  try {
    // TODO: 调用后端API
    ElMessage.success('审核通过')
    approveVisible.value = false
    currentRefund.value.status = 'approved'
    currentRefund.value.handleTime = new Date().toLocaleString('zh-CN')
    currentRefund.value.handleNote = approveForm.note
    stats.pendingCount--
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleReject = (row) => {
  currentRefund.value = row
  rejectForm.reason = ''
  rejectVisible.value = true
}

const confirmReject = async () => {
  if (!rejectForm.reason.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  
  try {
    // TODO: 调用后端API
    ElMessage.success('已拒绝退款申请')
    rejectVisible.value = false
    currentRefund.value.status = 'rejected'
    currentRefund.value.handleTime = new Date().toLocaleString('zh-CN')
    currentRefund.value.handleNote = rejectForm.reason
    stats.pendingCount--
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleRefund = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要退款 ¥${row.refundAmount} 给用户 ${row.userName} 吗？`,
      '确认退款',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // TODO: 调用后端API
    ElMessage.success('退款成功')
    row.status = 'refunded'
    stats.todayCount++
    stats.todayAmount += row.refundAmount
    stats.monthCount++
    stats.monthAmount += row.refundAmount
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('退款失败')
    }
  }
}

const fetchList = () => {
  // TODO: 调用后端API获取数据
  total.value = tableData.value.length
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.finance-refund {
  width: 100%;
}

.stats-cards {
  margin-bottom: 0;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-amount {
  font-size: 14px;
  color: #f56c6c;
  font-weight: bold;
}

.stat-desc {
  font-size: 12px;
  color: #67c23a;
}

.refund-amount {
  color: #f56c6c;
  font-weight: bold;
}

.query-form {
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .stat-card :deep(.el-card__body) {
    padding: 15px;
  }

  .stat-icon {
    width: 50px;
    height: 50px;
    margin-right: 15px;
  }

  .stat-value {
    font-size: 24px;
  }
}
</style>

