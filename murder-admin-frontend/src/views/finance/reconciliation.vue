<template>
  <div class="finance-reconciliation">
    <!-- 对账概览 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="overview-card">
          <div class="card-icon" style="background: #e6f7ff;">
            <span style="font-size: 32px;">💳</span>
          </div>
          <div class="card-content">
            <div class="card-label">待对账订单</div>
            <div class="card-value">{{ stats.pendingCount }}</div>
            <div class="card-desc">需要核对</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="overview-card">
          <div class="card-icon" style="background: #f6ffed;">
            <span style="font-size: 32px;">✅</span>
          </div>
          <div class="card-content">
            <div class="card-label">今日已对账</div>
            <div class="card-value">{{ stats.todayCount }}</div>
            <div class="card-amount">¥{{ formatMoney(stats.todayAmount) }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="overview-card">
          <div class="card-icon" style="background: #fff7e6;">
            <span style="font-size: 32px;">⚠️</span>
          </div>
          <div class="card-content">
            <div class="card-label">异常订单</div>
            <div class="card-value" style="color: #fa8c16;">{{ stats.errorCount }}</div>
            <div class="card-desc">需要处理</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="overview-card">
          <div class="card-icon" style="background: #fff1f0;">
            <span style="font-size: 32px;">📊</span>
          </div>
          <div class="card-content">
            <div class="card-label">本月对账金额</div>
            <div class="card-value">{{ stats.monthCount }}</div>
            <div class="card-amount">¥{{ formatMoney(stats.monthAmount) }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 对账操作 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>对账管理</span>
          <div>
            <el-button type="primary" @click="handleBatchReconcile">批量对账</el-button>
            <el-button type="success" @click="handleImportBankStatement">导入银行流水</el-button>
          </div>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="订单号">
          <el-input v-model="queryForm.orderNo" placeholder="请输入订单号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="queryForm.paymentMethod" placeholder="请选择" clearable style="width: 140px">
            <el-option label="微信支付" value="wechat" />
            <el-option label="支付宝" value="alipay" />
            <el-option label="银行卡" value="bank" />
            <el-option label="现金" value="cash" />
          </el-select>
        </el-form-item>
        <el-form-item label="对账状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="待对账" value="pending" />
            <el-option label="已对账" value="reconciled" />
            <el-option label="异常" value="error" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
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

      <el-table 
        :data="tableData" 
        style="width: 100%" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="orderNo" label="订单号" width="160" />
        <el-table-column prop="transactionNo" label="交易流水号" width="200" />
        <el-table-column label="支付方式" width="110">
          <template #default="{ row }">
            <el-tag :type="getPaymentType(row.paymentMethod)" size="small">
              {{ getPaymentText(row.paymentMethod) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="订单金额" width="120" align="right">
          <template #default="{ row }">
            <span>¥{{ row.orderAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="实付金额" width="120" align="right">
          <template #default="{ row }">
            <span class="actual-amount">¥{{ row.actualAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="差额" width="100" align="right">
          <template #default="{ row }">
            <span :class="getDifferenceClass(row.difference)">
              {{ row.difference > 0 ? '+' : '' }}¥{{ row.difference }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="payTime" label="支付时间" width="160" />
        <el-table-column prop="reconcileTime" label="对账时间" width="160" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'pending'" 
              type="primary" 
              size="small" 
              @click="handleReconcile(row)"
            >
              对账
            </el-button>
            <el-button 
              v-if="row.status === 'error'" 
              type="warning" 
              size="small" 
              @click="handleFixError(row)"
            >
              处理异常
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

    <!-- 对账详情对话框 -->
    <el-dialog v-model="detailVisible" title="对账详情" width="700px">
      <el-descriptions :column="2" border v-if="currentRecord">
        <el-descriptions-item label="订单号">{{ currentRecord.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="交易流水号">{{ currentRecord.transactionNo }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ getPaymentText(currentRecord.paymentMethod) }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentRecord.orderAmount }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">¥{{ currentRecord.actualAmount }}</el-descriptions-item>
        <el-descriptions-item label="差额">¥{{ currentRecord.difference }}</el-descriptions-item>
        <el-descriptions-item label="支付时间" :span="2">{{ currentRecord.payTime }}</el-descriptions-item>
        <el-descriptions-item label="对账时间" :span="2">{{ currentRecord.reconcileTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态" :span="2">
          <el-tag :type="getStatusType(currentRecord.status)">
            {{ getStatusText(currentRecord.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentRecord.remark" label="备注" :span="2">
          {{ currentRecord.remark }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 处理异常对话框 -->
    <el-dialog v-model="errorVisible" title="处理异常订单" width="500px">
      <el-form :model="errorForm" label-width="100px">
        <el-form-item label="订单号">
          <el-input v-model="errorForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="异常原因">
          <el-select v-model="errorForm.errorType" placeholder="请选择异常原因" style="width: 100%">
            <el-option label="金额不符" value="amount_mismatch" />
            <el-option label="重复支付" value="duplicate_payment" />
            <el-option label="支付失败" value="payment_failed" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理方式">
          <el-select v-model="errorForm.handleType" placeholder="请选择处理方式" style="width: 100%">
            <el-option label="强制对账" value="force_reconcile" />
            <el-option label="退款" value="refund" />
            <el-option label="人工核对" value="manual_check" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="errorForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="errorVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmFixError">确定</el-button>
      </template>
    </el-dialog>

    <!-- 导入银行流水对话框 -->
    <el-dialog v-model="importVisible" title="导入银行流水" width="500px">
      <el-upload
        class="upload-demo"
        drag
        action="#"
        :auto-upload="false"
        accept=".xlsx,.xls"
        :on-change="handleFileChange"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传 xlsx/xls 文件，且不超过 5MB
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmImport">开始导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'

const loading = ref(false)
const dateRange = ref([])
const detailVisible = ref(false)
const errorVisible = ref(false)
const importVisible = ref(false)
const currentRecord = ref(null)
const selectedRecords = ref([])

// 统计数据
const stats = reactive({
  pendingCount: 12,
  todayCount: 48,
  todayAmount: 9264,
  errorCount: 3,
  monthCount: 1842,
  monthAmount: 356780
})

// 查询表单
const queryForm = reactive({
  orderNo: '',
  paymentMethod: '',
  status: '',
  page: 1,
  pageSize: 10
})

const total = ref(0)

// 异常处理表单
const errorForm = reactive({
  orderNo: '',
  errorType: '',
  handleType: '',
  remark: ''
})

// 表格数据
const tableData = ref([
  {
    orderNo: 'ORD20251103001',
    transactionNo: 'WX20251103145820398',
    paymentMethod: 'wechat',
    orderAmount: 168,
    actualAmount: 168,
    difference: 0,
    payTime: '2025-11-03 10:25',
    reconcileTime: '2025-11-03 16:30',
    status: 'reconciled'
  },
  {
    orderNo: 'ORD20251103002',
    transactionNo: 'ALI20251103152630145',
    paymentMethod: 'alipay',
    orderAmount: 198,
    actualAmount: 198,
    difference: 0,
    payTime: '2025-11-03 11:30',
    reconcileTime: null,
    status: 'pending'
  },
  {
    orderNo: 'ORD20251103003',
    transactionNo: 'WX20251103160845721',
    paymentMethod: 'wechat',
    orderAmount: 228,
    actualAmount: 225,
    difference: -3,
    payTime: '2025-11-03 12:15',
    reconcileTime: null,
    status: 'error',
    remark: '实付金额与订单金额不符'
  },
  {
    orderNo: 'ORD20251103004',
    transactionNo: null,
    paymentMethod: 'cash',
    orderAmount: 188,
    actualAmount: 188,
    difference: 0,
    payTime: '2025-11-03 13:40',
    reconcileTime: null,
    status: 'pending'
  }
])

const formatMoney = (value) => {
  return String(value).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const getPaymentType = (method) => {
  const types = {
    'wechat': 'success',
    'alipay': 'primary',
    'cash': 'warning',
    'bank': 'info'
  }
  return types[method] || 'info'
}

const getPaymentText = (method) => {
  const texts = {
    'wechat': '微信支付',
    'alipay': '支付宝',
    'cash': '现金',
    'bank': '银行卡'
  }
  return texts[method] || method
}

const getStatusType = (status) => {
  const types = {
    'pending': 'warning',
    'reconciled': 'success',
    'error': 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    'pending': '待对账',
    'reconciled': '已对账',
    'error': '异常'
  }
  return texts[status] || status
}

const getDifferenceClass = (difference) => {
  if (difference > 0) return 'difference-positive'
  if (difference < 0) return 'difference-negative'
  return 'difference-zero'
}

const handleQuery = () => {
  queryForm.page = 1
  fetchList()
}

const handleReset = () => {
  queryForm.orderNo = ''
  queryForm.paymentMethod = ''
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

const handleSelectionChange = (selection) => {
  selectedRecords.value = selection
}

const handleViewDetail = (row) => {
  currentRecord.value = row
  detailVisible.value = true
}

const handleReconcile = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要对账订单 ${row.orderNo} 吗？`,
      '确认对账',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    // TODO: 调用后端API
    ElMessage.success('对账成功')
    row.status = 'reconciled'
    row.reconcileTime = new Date().toLocaleString('zh-CN')
    stats.pendingCount--
    stats.todayCount++
    stats.todayAmount += row.actualAmount
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('对账失败')
    }
  }
}

const handleBatchReconcile = async () => {
  if (selectedRecords.value.length === 0) {
    ElMessage.warning('请选择需要对账的订单')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要对账选中的 ${selectedRecords.value.length} 个订单吗？`,
      '批量对账',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    // TODO: 调用后端API
    ElMessage.success(`成功对账 ${selectedRecords.value.length} 个订单`)
    selectedRecords.value.forEach(record => {
      if (record.status === 'pending') {
        record.status = 'reconciled'
        record.reconcileTime = new Date().toLocaleString('zh-CN')
        stats.pendingCount--
        stats.todayCount++
        stats.todayAmount += record.actualAmount
      }
    })
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量对账失败')
    }
  }
}

const handleFixError = (row) => {
  currentRecord.value = row
  errorForm.orderNo = row.orderNo
  errorForm.errorType = ''
  errorForm.handleType = ''
  errorForm.remark = ''
  errorVisible.value = true
}

const confirmFixError = async () => {
  if (!errorForm.errorType || !errorForm.handleType) {
    ElMessage.warning('请选择异常原因和处理方式')
    return
  }

  try {
    // TODO: 调用后端API
    ElMessage.success('异常处理成功')
    errorVisible.value = false
    currentRecord.value.status = 'reconciled'
    currentRecord.value.reconcileTime = new Date().toLocaleString('zh-CN')
    currentRecord.value.remark = errorForm.remark
    stats.errorCount--
  } catch (error) {
    ElMessage.error('处理失败')
  }
}

const handleImportBankStatement = () => {
  importVisible.value = true
}

const handleFileChange = (file) => {
  console.log('文件选择:', file)
}

const confirmImport = () => {
  // TODO: 实现文件上传和导入逻辑
  ElMessage.success('导入功能开发中...')
  importVisible.value = false
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
.finance-reconciliation {
  width: 100%;
}

.overview-cards {
  margin-bottom: 0;
}

.overview-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  padding: 20px;
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
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
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.card-amount {
  font-size: 14px;
  color: #67c23a;
  font-weight: bold;
}

.card-desc {
  font-size: 12px;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.actual-amount {
  color: #67c23a;
  font-weight: bold;
}

.difference-zero {
  color: #909399;
}

.difference-positive {
  color: #67c23a;
  font-weight: bold;
}

.difference-negative {
  color: #f56c6c;
  font-weight: bold;
}

.query-form {
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .overview-card :deep(.el-card__body) {
    padding: 15px;
  }

  .card-icon {
    width: 50px;
    height: 50px;
    margin-right: 15px;
  }

  .card-value {
    font-size: 24px;
  }
}
</style>

