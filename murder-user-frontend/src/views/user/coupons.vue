<template>
  <div class="user-coupons">
    <el-card class="page-header">
      <h2>我的优惠券</h2>
      <p class="subtitle">管理您的优惠券，享受更多优惠</p>
    </el-card>

    <!-- 统计信息 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card class="stats-card">
          <div class="stats-content">
            <el-icon class="stats-icon" :size="40" color="#409EFF">
              <Tickets />
            </el-icon>
            <div class="stats-info">
              <div class="stats-value">{{ statistics.total }}</div>
              <div class="stats-label">全部优惠券</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stats-card">
          <div class="stats-content">
            <el-icon class="stats-icon" :size="40" color="#67C23A">
              <CircleCheck />
            </el-icon>
            <div class="stats-info">
              <div class="stats-value">{{ statistics.available }}</div>
              <div class="stats-label">可使用</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stats-card">
          <div class="stats-content">
            <el-icon class="stats-icon" :size="40" color="#F56C6C">
              <Clock />
            </el-icon>
            <div class="stats-info">
              <div class="stats-value">{{ statistics.expiring }}</div>
              <div class="stats-label">即将过期</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 优惠券列表 -->
    <el-card class="coupon-list-card">
      <template #header>
        <div class="card-header">
          <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <el-tab-pane label="未使用" name="unused"></el-tab-pane>
            <el-tab-pane label="已使用" name="used"></el-tab-pane>
            <el-tab-pane label="已过期" name="expired"></el-tab-pane>
          </el-tabs>
          <el-button type="primary" @click="showAvailableCoupons = true">
            <el-icon><Plus /></el-icon>
            领取优惠券
          </el-button>
        </div>
      </template>

      <div v-loading="loading" class="coupon-list">
        <EmptyState
          v-if="coupons.length === 0"
          type="no-coupon"
          @action="showAvailableCoupons = true"
        />

        <el-row :gutter="20" v-else>
          <el-col :span="12" v-for="coupon in coupons" :key="coupon.id">
            <div class="coupon-item" :class="{ 'expired': coupon.status === 3, 'used': coupon.status === 2 }">
              <div class="coupon-left">
                <div class="coupon-amount">
                  <span class="currency">¥</span>
                  <span class="value">{{ coupon.discountValue }}</span>
                </div>
                <div class="coupon-condition">满{{ coupon.minAmount }}元可用</div>
              </div>
              <div class="coupon-right">
                <div class="coupon-info">
                  <h3 class="coupon-name">{{ coupon.couponName || coupon.name }}</h3>
                  <p class="coupon-desc">{{ coupon.description }}</p>
                  <div class="coupon-meta">
                    <el-tag v-if="coupon.type === 1" size="small">满减券</el-tag>
                    <el-tag v-else-if="coupon.type === 2" type="success" size="small">折扣券</el-tag>
                    <el-tag v-else type="warning" size="small">代金券</el-tag>
                    <span class="coupon-date">
                      有效期至：{{ formatDate(coupon.expireTime) }}
                    </span>
                  </div>
                </div>
                <div class="coupon-actions">
                  <el-tag v-if="coupon.status === 2" type="info">已使用</el-tag>
                  <el-tag v-else-if="coupon.status === 3" type="danger">已过期</el-tag>
                  <el-button v-else type="primary" size="small" @click="useCouponNow(coupon)">
                    立即使用
                  </el-button>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 分页 -->
      <el-pagination
        v-if="total > 0"
        class="pagination"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </el-card>

    <!-- 可领取优惠券对话框 -->
    <el-dialog
      v-model="showAvailableCoupons"
      title="领取优惠券"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-loading="availableLoading" class="available-coupons">
        <div v-if="availableCoupons.length === 0" class="empty-state">
          <el-empty description="暂无可领取的优惠券"></el-empty>
        </div>
        <el-row :gutter="20" v-else>
          <el-col :span="24" v-for="coupon in availableCoupons" :key="coupon.id">
            <div class="available-coupon-item">
              <div class="coupon-left">
                <div class="coupon-amount">
                  <span class="currency">¥</span>
                  <span class="value">{{ coupon.discountValue }}</span>
                </div>
                <div class="coupon-condition">满{{ coupon.minAmount }}元可用</div>
              </div>
              <div class="coupon-right">
                <div class="coupon-info">
                  <h3 class="coupon-name">{{ coupon.name }}</h3>
                  <p class="coupon-desc">{{ coupon.description }}</p>
                  <div class="coupon-meta">
                    <span class="coupon-date">
                      有效期：{{ formatDate(coupon.validStartTime) }} 至 {{ formatDate(coupon.validEndTime) }}
                    </span>
                    <span class="coupon-stock">剩余：{{ coupon.remainCount }} / {{ coupon.totalCount }}</span>
                  </div>
                </div>
                <div class="coupon-actions">
                  <el-button 
                    type="primary" 
                    :disabled="coupon.remainCount <= 0"
                    @click="handleReceiveCoupon(coupon.id)"
                  >
                    {{ coupon.remainCount > 0 ? '立即领取' : '已抢光' }}
                  </el-button>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Tickets, CircleCheck, Clock, Plus } from '@element-plus/icons-vue'
import { getMyCoupons, getAvailableCoupons, receiveCoupon } from '@/api/coupon'
import { useRouter } from 'vue-router'
import EmptyState from '@/components/EmptyState.vue'

const router = useRouter()

// 状态
const loading = ref(false)
const activeTab = ref('unused')
const coupons = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 可领取优惠券
const showAvailableCoupons = ref(false)
const availableLoading = ref(false)
const availableCoupons = ref([])

// 统计信息
const statistics = reactive({
  total: 0,
  available: 0,
  expiring: 0
})

// 分页处理函数
const handlePageChange = (newPage) => {
  console.log('优惠券列表页码变化:', newPage)
  currentPage.value = newPage
  loadCoupons()
}

const handleSizeChange = (newSize) => {
  console.log('优惠券列表每页大小变化:', newSize)
  pageSize.value = newSize
  currentPage.value = 1
  loadCoupons()
}

// 加载优惠券列表
const loadCoupons = async () => {
  loading.value = true
  try {
    console.log('加载优惠券列表，参数:', { page: currentPage.value, pageSize: pageSize.value, status: activeTab.value })
    const params = {
      status: activeTab.value === 'unused' ? 1 : activeTab.value === 'used' ? 2 : 3,
      page: currentPage.value,
      pageSize: pageSize.value
    }
    const res = await getMyCoupons(params)
    if (res.code === 1 || res.code === 200) {
      coupons.value = res.data.records || []
      total.value = res.data.total || 0
      
      // 更新统计信息
      updateStatistics()
    }
  } catch (error) {
    console.error('加载优惠券失败:', error)
    ElMessage.error('加载优惠券失败')
  } finally {
    loading.value = false
  }
}

// 更新统计信息
const updateStatistics = async () => {
  try {
    // 获取所有状态的优惠券数量
    const [unusedRes, usedRes, expiredRes] = await Promise.all([
      getMyCoupons({ status: 1, page: 1, pageSize: 1 }),
      getMyCoupons({ status: 2, page: 1, pageSize: 1 }),
      getMyCoupons({ status: 3, page: 1, pageSize: 1 })
    ])
    
    statistics.available = unusedRes.data?.total || 0
    statistics.total = statistics.available + (usedRes.data?.total || 0) + (expiredRes.data?.total || 0)
    
    // 计算即将过期的优惠券（7天内）- 需要获取所有未使用的优惠券
    if (statistics.available > 0) {
      const allUnusedRes = await getMyCoupons({ status: 1, page: 1, pageSize: statistics.available })
      const now = new Date()
      const sevenDaysLater = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000)
      
      const allUnusedCoupons = allUnusedRes.data?.records || []
      statistics.expiring = allUnusedCoupons.filter(c => {
        if (!c.expireTime) return false
        const expireTime = new Date(c.expireTime)
        return expireTime <= sevenDaysLater && expireTime > now
      }).length
    } else {
      statistics.expiring = 0
    }
  } catch (error) {
    console.error('更新统计信息失败:', error)
  }
}

// 加载可领取的优惠券
const loadAvailableCoupons = async () => {
  availableLoading.value = true
  try {
    const res = await getAvailableCoupons()
    if (res.code === 1) {
      availableCoupons.value = res.data || []
    }
  } catch (error) {
    console.error('加载可领取优惠券失败:', error)
    ElMessage.error('加载可领取优惠券失败')
  } finally {
    availableLoading.value = false
  }
}

// 领取优惠券
const handleReceiveCoupon = async (couponId) => {
  try {
    const res = await receiveCoupon(couponId)
    if (res.code === 1) {
      ElMessage.success('领取成功')
      loadAvailableCoupons()
      loadCoupons()
    }
  } catch (error) {
    console.error('领取优惠券失败:', error)
  }
}

// 立即使用优惠券
const useCouponNow = (coupon) => {
  ElMessageBox.confirm(
    '使用优惠券需要创建预约，是否前往预约页面？',
    '提示',
    {
      confirmButtonText: '前往预约',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(() => {
    console.log('跳转到预约页面，优惠券ID:', coupon.id)
    // 使用命名路由跳转
    router.push({ 
      name: 'CreateReservation',
      query: { couponId: coupon.id }
    }).then(() => {
      console.log('跳转成功')
    }).catch(err => {
      console.error('跳转失败:', err)
      // 如果命名路由失败，尝试使用路径
      router.push('/reservation/create')
    })
  }).catch(() => {
    console.log('用户取消跳转')
  })
}

// 切换标签
const handleTabChange = () => {
  currentPage.value = 1
  loadCoupons()
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 初始化
onMounted(() => {
  loadCoupons()
})

// 监听可领取优惠券对话框打开
const handleDialogOpen = () => {
  if (showAvailableCoupons.value) {
    loadAvailableCoupons()
  }
}

// Watch对话框状态
import { watch } from 'vue'
watch(showAvailableCoupons, (newVal) => {
  if (newVal) {
    loadAvailableCoupons()
  }
})
</script>

<style scoped>
.user-coupons {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #303133;
}

.subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stats-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stats-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stats-icon {
  flex-shrink: 0;
}

.stats-info {
  flex: 1;
}

.stats-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stats-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

/* 优惠券列表 */
.coupon-list-card :deep(.el-card__header) {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
}

.card-header :deep(.el-tabs) {
  flex: 1;
}

.card-header :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.coupon-list {
  min-height: 400px;
}

.coupon-item {
  display: flex;
  border: 2px solid #DCDFE6;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
  transition: all 0.3s;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.coupon-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.coupon-item.expired,
.coupon-item.used {
  background: linear-gradient(135deg, #bbb 0%, #999 100%);
  opacity: 0.7;
}

.coupon-left {
  width: 150px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px;
  color: white;
  position: relative;
}

.coupon-left::after {
  content: '';
  position: absolute;
  right: -1px;
  top: 50%;
  transform: translateY(-50%);
  width: 2px;
  height: 80%;
  background-image: linear-gradient(to bottom, white 50%, transparent 50%);
  background-size: 2px 10px;
}

.coupon-amount {
  margin-bottom: 5px;
}

.coupon-amount .currency {
  font-size: 20px;
}

.coupon-amount .value {
  font-size: 36px;
  font-weight: bold;
}

.coupon-condition {
  font-size: 12px;
  opacity: 0.9;
}

.coupon-right {
  flex: 1;
  display: flex;
  padding: 20px;
  background: white;
  justify-content: space-between;
  align-items: center;
}

.coupon-info {
  flex: 1;
}

.coupon-name {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
}

.coupon-desc {
  margin: 0 0 10px 0;
  font-size: 13px;
  color: #606266;
}

.coupon-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: #909399;
}

.coupon-actions {
  margin-left: 20px;
}

/* 可领取优惠券 */
.available-coupons {
  max-height: 600px;
  overflow-y: auto;
}

.available-coupon-item {
  display: flex;
  border: 2px solid #DCDFE6;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 15px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.available-coupon-item .coupon-right {
  background: white;
}

.coupon-stock {
  margin-left: 15px;
  color: #E6A23C;
}

/* 空状态 */
.empty-state {
  padding: 60px 0;
  text-align: center;
}

/* 分页 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
