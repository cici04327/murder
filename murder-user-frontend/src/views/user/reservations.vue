<template>
  <div class="reservations-container">
    <el-card>
      <template #header>
        <div class="header">
          <h2>我的预约</h2>
          <el-button type="primary" @click="router.push('/reservation/create')">
            <el-icon><Plus /></el-icon>
            新建预约
          </el-button>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待支付" name="0" />
        <el-tab-pane label="待确认" name="1" />
        <el-tab-pane label="已确认" name="2" />
        <el-tab-pane label="已完成" name="3" />
        <el-tab-pane label="已取消" name="4" />
      </el-tabs>
      
      <div class="reservations-list" v-loading="loading">
        <div class="reservation-item" v-for="item in reservations" :key="item.id">
          <div class="item-header">
            <span class="order-no">订单号：{{ item.id }}</span>
            <el-tag :type="getStatusType(item.status)">
              {{ getStatusText(item.status) }}
            </el-tag>
          </div>
          
          <el-row :gutter="20" class="item-content">
            <el-col :span="16">
              <div class="info-row">
                <span class="label">剧本：</span>
                <span class="value">{{ item.scriptName }}</span>
              </div>
              <div class="info-row">
                <span class="label">门店：</span>
                <span class="value">{{ item.storeName }}</span>
              </div>
              <div class="info-row">
                <span class="label">房间：</span>
                <span class="value">{{ item.roomName }}</span>
              </div>
              <div class="info-row">
                <span class="label">预约时间：</span>
                <span class="value">{{ item.reservationTime }}</span>
              </div>
              <div class="info-row">
                <span class="label">参与人数：</span>
                <span class="value">{{ item.playerCount }}人</span>
              </div>
            </el-col>
            
            <el-col :span="8" class="price-actions">
              <div class="price">
                <span class="label">总价：</span>
                <span class="value">¥{{ item.totalPrice }}</span>
              </div>
              <div class="actions">
                <el-button
                  v-if="item.status === 0"
                  type="primary"
                  size="small"
                  @click="handlePay(item)"
                >
                  立即支付
                </el-button>
                <el-button
                  v-if="item.status === 3"
                  type="primary"
                  size="small"
                  @click="handleReview(item)"
                >
                  评价
                </el-button>
                <el-button
                  v-if="item.status < 2"
                  size="small"
                  @click="handleCancel(item)"
                >
                  取消预约
                </el-button>
                <el-button size="small" @click="handleViewDetail(item)">
                  查看详情
                </el-button>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <EmptyState
          v-if="!loading && reservations.length === 0"
          type="no-reservation"
          @action="$router.push('/reservation/create')"
        />
      </div>
      
      <el-pagination
        v-if="total > 0"
        class="pagination"
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 30]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </el-card>
    
    <!-- 取消预约对话框 -->
    <el-dialog v-model="showCancelDialog" title="取消预约" width="400px">
      <el-form>
        <el-form-item label="取消原因">
          <el-input
            v-model="cancelReason"
            type="textarea"
            :rows="4"
            placeholder="请输入取消原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCancelDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmCancel">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import EmptyState from '@/components/EmptyState.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyReservations, cancelReservation } from '@/api/reservation'

const router = useRouter()

const loading = ref(false)
const activeTab = ref('all')
const reservations = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const showCancelDialog = ref(false)
const cancelReason = ref('')
const currentCancelItem = ref(null)

const getStatusType = (status) => {
  const types = {
    0: 'warning',
    1: 'info',
    2: 'success',
    3: 'success',
    4: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '待支付',
    1: '待确认',
    2: '已确认',
    3: '已完成',
    4: '已取消'
  }
  return texts[status] || '未知'
}

const handlePageChange = (newPage) => {
  console.log('预约列表页码变化:', newPage)
  page.value = newPage
  loadReservations()
}

const handleSizeChange = (newSize) => {
  console.log('预约列表每页大小变化:', newSize)
  pageSize.value = newSize
  page.value = 1
  loadReservations()
}

const loadReservations = async () => {
  loading.value = true
  try {
    console.log('加载预约列表，参数:', { page: page.value, pageSize: pageSize.value })
    const params = {
      page: page.value,
      pageSize: pageSize.value
    }
    if (activeTab.value !== 'all') {
      params.status = parseInt(activeTab.value)
    }
    
    const res = await getMyReservations(params)
    if (res.data) {
      if (Array.isArray(res.data)) {
        reservations.value = res.data
        total.value = res.data.length
      } else {
        reservations.value = res.data.records || res.data.list || []
        total.value = res.data.total || 0
      }
    }
  } catch (error) {
    console.error('加载预约列表失败:', error)
    // 模拟数据
    reservations.value = [
      {
        id: 1001,
        scriptName: '迷雾庄园',
        storeName: '探案密室',
        roomName: '推理房A',
        reservationTime: '2024-01-15 14:00',
        playerCount: 6,
        totalPrice: 528,
        status: 0
      },
      {
        id: 1002,
        scriptName: '时光旅人',
        storeName: '时空剧本馆',
        roomName: '沉浸房B',
        reservationTime: '2024-01-16 15:00',
        playerCount: 5,
        totalPrice: 340,
        status: 2
      }
    ]
    total.value = 2
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  page.value = 1
  loadReservations()
}

const handlePay = (item) => {
  router.push(`/payment/${item.id}`)
}

const handleCancel = (item) => {
  currentCancelItem.value = item
  showCancelDialog.value = true
}

const confirmCancel = async () => {
  if (!cancelReason.value.trim()) {
    ElMessage.warning('请输入取消原因')
    return
  }
  
  try {
    const res = await cancelReservation(currentCancelItem.value.id, cancelReason.value)
    if (res.code === 1 || res.code === 200) {
      ElMessage.success('取消成功')
      showCancelDialog.value = false
      cancelReason.value = ''
      loadReservations()
    }
  } catch (error) {
    console.error('取消预约失败:', error)
  }
}

const handleReview = (item) => {
  router.push({
    path: `/script/${item.scriptId}`,
    query: { review: true }
  })
}

const handleViewDetail = (item) => {
  router.push(`/reservation/detail/${item.id}`)
}

onMounted(() => {
  loadReservations()
})
</script>

<style scoped>
.reservations-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  margin: 0;
}

.reservations-list {
  min-height: 400px;
}

.reservation-item {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  transition: all 0.3s;
}

.reservation-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.order-no {
  font-weight: bold;
  color: #666;
}

.item-content {
  margin-top: 15px;
}

.info-row {
  margin-bottom: 10px;
  display: flex;
}

.info-row .label {
  color: #999;
  min-width: 80px;
}

.info-row .value {
  color: #333;
}

.price-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
}

.price {
  font-size: 18px;
}

.price .label {
  color: #666;
}

.price .value {
  color: #f56c6c;
  font-weight: bold;
  font-size: 24px;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

.actions .el-button {
  width: 100%;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
