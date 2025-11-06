<template>
  <div class="user-coupons">
    <el-card class="page-header">
      <div class="header-content">
        <div>
          <h2>
            <el-icon class="header-icon"><Ticket /></el-icon>
            我的优惠券
          </h2>
          <p class="subtitle">管理您的优惠券，享受更多优惠</p>
        </div>
        <el-button type="primary" size="large" @click="showAvailableCoupons = true">
          <el-icon><Plus /></el-icon>
          领取优惠券
        </el-button>
      </div>
    </el-card>

    <!-- 统计信息 -->
    <el-row :gutter="15" class="stats-row">
      <el-col :xs="24" :sm="8" :md="8">
        <el-card shadow="hover" class="stats-card stats-primary">
          <div class="stats-content">
            <div class="stats-icon-wrapper">
              <el-icon class="stats-icon" :size="40"><Tickets /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ formatNumber(statistics.total) }}</div>
              <div class="stats-label">全部优惠券</div>
              <div class="stats-desc">累计拥有</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="8">
        <el-card shadow="hover" class="stats-card stats-success">
          <div class="stats-content">
            <div class="stats-icon-wrapper">
              <el-icon class="stats-icon" :size="40"><CircleCheck /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ formatNumber(statistics.available) }}</div>
              <div class="stats-label">可使用</div>
              <div class="stats-desc">{{ statistics.available > 0 ? '立即使用' : '暂无可用' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="8">
        <el-card shadow="hover" class="stats-card stats-danger">
          <div class="stats-content">
            <div class="stats-icon-wrapper">
              <el-icon class="stats-icon" :size="40"><Clock /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ formatNumber(statistics.expiring) }}</div>
              <div class="stats-label">即将过期</div>
              <div class="stats-desc">{{ statistics.expiring > 0 ? '7天内到期' : '无过期风险' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 优惠券列表 -->
    <el-card class="coupon-list-card">
      <template #header>
        <div class="card-header">
          <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="coupon-tabs">
            <el-tab-pane name="unused">
              <template #label>
                <span class="tab-label">
                  <el-icon><CircleCheck /></el-icon>
                  未使用
                  <el-badge v-if="statistics.available > 0" :value="statistics.available" class="tab-badge" />
                </span>
              </template>
            </el-tab-pane>
            <el-tab-pane name="used">
              <template #label>
                <span class="tab-label">
                  <el-icon><Select /></el-icon>
                  已使用
                </span>
              </template>
            </el-tab-pane>
            <el-tab-pane name="expired">
              <template #label>
                <span class="tab-label">
                  <el-icon><CircleClose /></el-icon>
                  已过期
                </span>
              </template>
            </el-tab-pane>
          </el-tabs>
        </div>
      </template>

      <div v-loading="loading" class="coupon-list">
        <EmptyState
          v-if="coupons.length === 0"
          type="no-coupon"
          @action="showAvailableCoupons = true"
        />

        <el-row :gutter="15" v-else>
          <el-col :xs="24" :sm="24" :md="12" :lg="12" v-for="coupon in coupons" :key="coupon.id">
            <div class="coupon-item" :class="getCouponClass(coupon)">
              <!-- 优惠券左侧 - 金额显示 -->
              <div class="coupon-left">
                <div class="coupon-amount">
                  <span class="currency">¥</span>
                  <span class="value">{{ coupon.discountValue }}</span>
                </div>
                <div class="coupon-condition">满{{ coupon.minAmount }}元</div>
                <div class="coupon-type-badge">
                  {{ getCouponTypeName(coupon.type) }}
                </div>
              </div>

              <!-- 优惠券右侧 - 详细信息 -->
              <div class="coupon-right">
                <div class="coupon-info">
                  <h3 class="coupon-name">
                    {{ coupon.couponName || coupon.name }}
                    <el-tag v-if="isExpiringSoon(coupon)" type="warning" size="small" effect="dark">即将过期</el-tag>
                  </h3>
                  <p class="coupon-desc">{{ coupon.description || '可用于所有剧本预约' }}</p>
                  <div class="coupon-meta">
                    <div class="meta-item">
                      <el-icon><Calendar /></el-icon>
                      <span>有效期至 {{ formatDate(coupon.expireTime) }}</span>
                    </div>
                    <div class="meta-item" v-if="coupon.status === 2 && coupon.usedTime">
                      <el-icon><Clock /></el-icon>
                      <span>使用于 {{ formatDate(coupon.usedTime) }}</span>
                    </div>
                  </div>
                </div>
                <div class="coupon-actions">
                  <el-tag v-if="coupon.status === 2" type="info" size="large">已使用</el-tag>
                  <el-tag v-else-if="coupon.status === 3" type="danger" size="large">已过期</el-tag>
                  <el-button v-else type="primary" size="default" @click="useCouponNow(coupon)">
                    立即使用
                  </el-button>
                </div>
              </div>

              <!-- 装饰性元素 -->
              <div class="coupon-decoration">
                <div class="circle circle-top"></div>
                <div class="circle circle-bottom"></div>
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
      title="🎁 领取优惠券"
      width="900px"
      :close-on-click-modal="false"
      class="coupon-dialog"
    >
      <div v-loading="availableLoading" class="available-coupons">
        <div v-if="availableCoupons.length === 0" class="empty-state">
          <el-empty description="暂无可领取的优惠券">
            <template #image>
              <el-icon :size="100" color="#909399"><Ticket /></el-icon>
            </template>
          </el-empty>
        </div>
        <div v-else class="available-list">
          <div v-for="coupon in availableCoupons" :key="coupon.id" class="available-coupon-item">
            <!-- 优惠券左侧 - 金额显示 -->
            <div class="coupon-left">
              <div class="coupon-amount">
                <span class="currency">¥</span>
                <span class="value">{{ coupon.discountValue }}</span>
              </div>
              <div class="coupon-condition">满{{ coupon.minAmount }}元</div>
              <div class="coupon-type-badge">
                {{ getCouponTypeName(coupon.type) }}
              </div>
            </div>

            <!-- 优惠券右侧 - 详细信息 -->
            <div class="coupon-right">
              <div class="coupon-info">
                <h3 class="coupon-name">
                  {{ coupon.name }}
                  <el-tag v-if="coupon.remainCount <= 10 && coupon.remainCount > 0" type="danger" size="small" effect="dark">
                    仅剩{{ coupon.remainCount }}张
                  </el-tag>
                </h3>
                <p class="coupon-desc">{{ coupon.description || '可用于所有剧本预约' }}</p>
                <div class="coupon-meta">
                  <div class="meta-item">
                    <el-icon><Calendar /></el-icon>
                    <span>{{ formatDate(coupon.validStartTime) }} 至 {{ formatDate(coupon.validEndTime) }}</span>
                  </div>
                  <div class="meta-item">
                    <el-icon><Tickets /></el-icon>
                    <span>剩余 {{ coupon.remainCount }} / {{ coupon.totalCount }}</span>
                  </div>
                  <div class="meta-item" v-if="coupon.exchangePoints && coupon.exchangePoints > 0">
                    <el-icon><StarFilled /></el-icon>
                    <span>需要 {{ coupon.exchangePoints }} 积分</span>
                  </div>
                </div>
              </div>
              <div class="coupon-actions">
                <el-button 
                  :type="coupon.exchangePoints > 0 ? 'warning' : 'primary'"
                  size="large"
                  :disabled="coupon.remainCount <= 0"
                  :loading="receivingCouponId === coupon.id"
                  @click="handleReceiveCoupon(coupon)"
                >
                  <el-icon v-if="coupon.remainCount > 0"><Plus /></el-icon>
                  {{ coupon.remainCount > 0 ? (coupon.exchangePoints > 0 ? `${coupon.exchangePoints}积分兑换` : '免费领取') : '已抢光' }}
                </el-button>
              </div>
            </div>

            <!-- 装饰性元素 -->
            <div class="coupon-decoration">
              <div class="circle circle-top"></div>
              <div class="circle circle-bottom"></div>
            </div>

            <!-- 进度条 -->
            <div class="stock-progress">
              <el-progress 
                :percentage="getStockPercentage(coupon)" 
                :color="getProgressColor(coupon)"
                :show-text="false"
                :stroke-width="3"
              />
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Tickets, 
  CircleCheck, 
  Clock, 
  Plus, 
  Ticket,
  Select,
  CircleClose,
  Calendar,
  StarFilled
} from '@element-plus/icons-vue'
import { getMyCoupons, getAvailableCoupons, receiveCoupon } from '@/api/coupon'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import EmptyState from '@/components/EmptyState.vue'

const router = useRouter()
const userStore = useUserStore()

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
const receivingCouponId = ref(null)

// 统计信息
const statistics = reactive({
  total: 0,
  available: 0,
  expiring: 0
})

// 格式化数字
const formatNumber = (num) => {
  if (num === undefined || num === null) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

// 获取优惠券类型名称
const getCouponTypeName = (type) => {
  const typeMap = {
    1: '满减券',
    2: '折扣券',
    3: '代金券'
  }
  return typeMap[type] || '优惠券'
}

// 获取优惠券样式类
const getCouponClass = (coupon) => {
  const classes = []
  if (coupon.status === 3) classes.push('expired')
  if (coupon.status === 2) classes.push('used')
  if (isExpiringSoon(coupon)) classes.push('expiring-soon')
  return classes.join(' ')
}

// 判断是否即将过期（7天内）
const isExpiringSoon = (coupon) => {
  if (!coupon.expireTime || coupon.status !== 1) return false
  const now = new Date()
  const expireTime = new Date(coupon.expireTime)
  const sevenDaysLater = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000)
  return expireTime <= sevenDaysLater && expireTime > now
}

// 获取库存百分比
const getStockPercentage = (coupon) => {
  if (!coupon.totalCount || coupon.totalCount === 0) return 0
  return Math.round((coupon.remainCount / coupon.totalCount) * 100)
}

// 获取进度条颜色
const getProgressColor = (coupon) => {
  const percentage = getStockPercentage(coupon)
  if (percentage > 50) return '#67c23a'
  if (percentage > 20) return '#e6a23c'
  return '#f56c6c'
}

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
    const statusValue = activeTab.value === 'unused' ? 1 : activeTab.value === 'used' ? 2 : 3
    console.log('===== 加载优惠券列表 =====')
    console.log('当前标签:', activeTab.value)
    console.log('状态值:', statusValue)
    console.log('页码:', currentPage.value)
    console.log('每页数量:', pageSize.value)
    
    const params = {
      status: statusValue,
      page: currentPage.value,
      pageSize: pageSize.value
    }
    
    const res = await getMyCoupons(params)
    console.log('API响应:', res)
    console.log('响应码:', res.code)
    console.log('数据:', res.data)
    
    if (res.code === 1 || res.code === 200) {
      const records = res.data.records || []
      const totalCount = res.data.total || 0
      
      console.log('优惠券记录数:', records.length)
      console.log('总数:', totalCount)
      console.log('优惠券列表:', records)
      
      coupons.value = records
      total.value = totalCount
      
      // 更新统计信息
      updateStatistics()
    } else {
      console.error('API返回错误码:', res.code, '错误信息:', res.msg)
      ElMessage.error(res.msg || '加载优惠券失败')
    }
  } catch (error) {
    console.error('加载优惠券失败，错误详情:', error)
    ElMessage.error('加载优惠券失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 更新统计信息
const updateStatistics = async () => {
  try {
    console.log('===== 更新统计信息 =====')
    
    // 获取所有状态的优惠券数量（使用较大的pageSize以获取所有记录）
    const [unusedRes, usedRes, expiredRes] = await Promise.all([
      getMyCoupons({ status: 1, page: 1, pageSize: 1000 }),
      getMyCoupons({ status: 2, page: 1, pageSize: 1000 }),
      getMyCoupons({ status: 3, page: 1, pageSize: 1000 })
    ])
    
    console.log('未使用响应:', unusedRes)
    console.log('已使用响应:', usedRes)
    console.log('已过期响应:', expiredRes)
    
    // 修复：后端total字段有问题，使用records.length获取实际数量
    const unusedCount = unusedRes.data?.records?.length || 0
    const usedCount = usedRes.data?.records?.length || 0
    const expiredCount = expiredRes.data?.records?.length || 0
    
    console.log('未使用数量:', unusedCount)
    console.log('已使用数量:', usedCount)
    console.log('已过期数量:', expiredCount)
    
    statistics.available = unusedCount
    statistics.total = unusedCount + usedCount + expiredCount
    
    console.log('统计信息 - 可用:', statistics.available, '总数:', statistics.total)
    
    // 计算即将过期的优惠券（7天内）- 需要获取所有未使用的优惠券
    if (statistics.available > 0) {
      console.log('获取所有未使用优惠券，数量:', statistics.available)
      const allUnusedRes = await getMyCoupons({ status: 1, page: 1, pageSize: statistics.available })
      console.log('所有未使用优惠券响应:', allUnusedRes)
      
      const now = new Date()
      const sevenDaysLater = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000)
      
      const allUnusedCoupons = allUnusedRes.data?.records || []
      console.log('未使用优惠券列表:', allUnusedCoupons)
      
      statistics.expiring = allUnusedCoupons.filter(c => {
        if (!c.expireTime) return false
        const expireTime = new Date(c.expireTime)
        return expireTime <= sevenDaysLater && expireTime > now
      }).length
      
      console.log('即将过期数量:', statistics.expiring)
    } else {
      statistics.expiring = 0
      console.log('没有未使用的优惠券')
    }
  } catch (error) {
    console.error('更新统计信息失败，错误详情:', error)
  }
}

// 加载可领取的优惠券
const loadAvailableCoupons = async () => {
  availableLoading.value = true
  try {
    const res = await getAvailableCoupons()
    console.log('可领取优惠券API响应:', res)
    console.log('响应码:', res.code)
    console.log('数据:', res.data)
    
    if (res.code === 1 || res.code === 200) {
      // 如果返回的是分页结构，取records，否则直接用data
      if (res.data && res.data.records) {
        availableCoupons.value = res.data.records || []
        console.log('可领取优惠券数量:', res.data.records.length)
      } else {
        availableCoupons.value = res.data || []
        console.log('可领取优惠券数量:', res.data?.length || 0)
      }
    } else {
      console.error('API返回错误码:', res.code)
      ElMessage.error(res.msg || '加载失败')
    }
  } catch (error) {
    console.error('加载可领取优惠券失败:', error)
    ElMessage.error('加载可领取优惠券失败')
  } finally {
    availableLoading.value = false
  }
}

// 领取优惠券
const handleReceiveCoupon = async (coupon) => {
  // 如果需要积分兑换，先确认
  if (coupon.exchangePoints && coupon.exchangePoints > 0) {
    const userPoints = userStore.userInfo?.points || 0
    
    if (userPoints < coupon.exchangePoints) {
      ElMessage.warning(`积分不足！当前积分：${userPoints}，需要：${coupon.exchangePoints}`)
      return
    }
    
    try {
      await ElMessageBox.confirm(
        `兑换此优惠券需要消耗 ${coupon.exchangePoints} 积分，当前积分：${userPoints}，确认兑换吗？`,
        '积分兑换确认',
        {
          confirmButtonText: '确认兑换',
          cancelButtonText: '取消',
          type: 'warning',
          customClass: 'exchange-confirm-box'
        }
      )
    } catch {
      return // 用户取消
    }
  }
  
  receivingCouponId.value = coupon.id
  try {
    const res = await receiveCoupon(coupon.id)
    if (res.code === 1 || res.code === 200) {
      ElMessage.success({
        message: coupon.exchangePoints > 0 
          ? `🎉 兑换成功！消耗 ${coupon.exchangePoints} 积分` 
          : '🎉 领取成功！已添加到您的优惠券',
        duration: 3000
      })
      // 刷新用户信息以更新积分
      if (coupon.exchangePoints > 0) {
        userStore.fetchUserInfo()
      }
      loadAvailableCoupons()
      loadCoupons()
      updateStatistics()
    } else {
      ElMessage.error(res.msg || '领取失败，请重试')
    }
  } catch (error) {
    console.error('领取优惠券失败:', error)
    const errorMsg = error.response?.data?.msg || error.message || '领取失败，请重试'
    ElMessage.error(errorMsg)
  } finally {
    receivingCouponId.value = null
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

// 监听对话框状态
watch(showAvailableCoupons, (newVal) => {
  if (newVal) {
    loadAvailableCoupons()
  }
})
</script>

<style scoped>
.user-coupons {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

/* 页面头部 */
.page-header {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.page-header :deep(.el-card__body) {
  padding: 30px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
}

.page-header h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
  color: white;
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-icon {
  font-size: 32px;
}

.subtitle {
  margin: 0;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.stats-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.stats-content {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px 10px;
}

.stats-icon-wrapper {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  flex-shrink: 0;
}

.stats-primary .stats-icon-wrapper {
  background: rgba(64, 158, 255, 0.1);
}

.stats-success .stats-icon-wrapper {
  background: rgba(103, 194, 58, 0.1);
}

.stats-danger .stats-icon-wrapper {
  background: rgba(245, 108, 108, 0.1);
}

.stats-icon {
  opacity: 0.9;
}

.stats-primary .stats-icon {
  color: #409eff;
}

.stats-success .stats-icon {
  color: #67c23a;
}

.stats-danger .stats-icon {
  color: #f56c6c;
}

.stats-info {
  flex: 1;
  min-width: 0;
}

.stats-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
  margin-bottom: 4px;
}

.stats-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 2px;
  font-weight: 500;
}

.stats-desc {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 2px;
}

/* 优惠券列表 */
.coupon-list-card :deep(.el-card__header) {
  padding: 20px;
  background: #f8f9fa;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.coupon-tabs {
  flex: 1;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 5px;
  position: relative;
}

.tab-badge {
  margin-left: 5px;
}

.coupon-list {
  min-height: 400px;
  padding: 10px 0;
}

/* 优惠券卡片 */
.coupon-item {
  display: flex;
  border-radius: 12px;
  overflow: visible;
  margin-bottom: 15px;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
}

.coupon-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.coupon-item.expired,
.coupon-item.used {
  background: linear-gradient(135deg, #bbb 0%, #999 100%);
  opacity: 0.6;
}

.coupon-item.expired:hover,
.coupon-item.used:hover {
  transform: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.coupon-item.expiring-soon {
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 2px 8px rgba(230, 162, 60, 0.3);
  }
  50% {
    box-shadow: 0 4px 16px rgba(230, 162, 60, 0.6);
  }
}

/* 优惠券左侧 - 金额区域 */
.coupon-left {
  width: 140px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 25px 15px;
  color: white;
  position: relative;
  background: rgba(255, 255, 255, 0.1);
}

.coupon-amount {
  margin-bottom: 8px;
  text-align: center;
}

.coupon-amount .currency {
  font-size: 18px;
  font-weight: 500;
  opacity: 0.9;
}

.coupon-amount .value {
  font-size: 40px;
  font-weight: bold;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.coupon-condition {
  font-size: 13px;
  opacity: 0.95;
  margin-bottom: 8px;
  font-weight: 500;
}

.coupon-type-badge {
  font-size: 11px;
  padding: 3px 10px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  font-weight: 500;
  backdrop-filter: blur(5px);
}

/* 优惠券右侧 - 信息区域 */
.coupon-right {
  flex: 1;
  display: flex;
  padding: 20px;
  background: white;
  justify-content: space-between;
  align-items: center;
  position: relative;
}

.coupon-info {
  flex: 1;
  min-width: 0;
}

.coupon-name {
  margin: 0 0 8px 0;
  font-size: 17px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.coupon-desc {
  margin: 0 0 12px 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}

.coupon-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 12px;
  color: #909399;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.meta-item .el-icon {
  font-size: 14px;
}

.coupon-actions {
  margin-left: 20px;
  flex-shrink: 0;
}

/* 装饰性元素 */
.coupon-decoration {
  position: absolute;
  left: 140px;
  top: 0;
  bottom: 0;
  width: 0;
  pointer-events: none;
}

.circle {
  position: absolute;
  width: 20px;
  height: 20px;
  background: #f8f9fa;
  border-radius: 50%;
  left: -10px;
}

.circle-top {
  top: -10px;
}

.circle-bottom {
  bottom: -10px;
}

/* 可领取优惠券对话框 */
.coupon-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 25px 30px;
}

.coupon-dialog :deep(.el-dialog__title) {
  color: white;
  font-size: 20px;
  font-weight: 600;
}

.coupon-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: white;
  font-size: 20px;
}

.coupon-dialog :deep(.el-dialog__body) {
  padding: 20px;
}

.available-coupons {
  max-height: 600px;
  overflow-y: auto;
}

.available-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.available-coupon-item {
  display: flex;
  border-radius: 12px;
  overflow: visible;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
  transition: all 0.3s ease;
}

.available-coupon-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(240, 147, 251, 0.4);
}

.available-coupon-item .coupon-right {
  background: white;
}

/* 库存进度条 */
.stock-progress {
  position: absolute;
  bottom: 0;
  left: 140px;
  right: 0;
  background: white;
}

.stock-progress :deep(.el-progress) {
  margin: 0;
}

.stock-progress :deep(.el-progress-bar__outer) {
  border-radius: 0;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .user-coupons {
    padding: 10px;
  }

  .page-header :deep(.el-card__body) {
    padding: 20px;
  }

  .header-content {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .page-header h2 {
    font-size: 22px;
  }

  .stats-content {
    flex-direction: column;
    text-align: center;
    gap: 10px;
  }

  .stats-icon-wrapper {
    width: 50px;
    height: 50px;
  }

  .stats-icon {
    font-size: 32px !important;
  }

  .stats-value {
    font-size: 24px;
  }

  .coupon-item {
    flex-direction: column;
  }

  .coupon-left {
    width: 100%;
    padding: 20px;
    flex-direction: row;
    justify-content: space-around;
  }

  .coupon-right {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .coupon-actions {
    margin-left: 0;
    width: 100%;
  }

  .coupon-actions .el-button {
    width: 100%;
  }

  .coupon-decoration {
    display: none;
  }

  .available-coupon-item {
    flex-direction: column;
  }

  .available-coupon-item .coupon-left {
    width: 100%;
  }

  .stock-progress {
    left: 0;
  }
}

@media (max-width: 992px) {
  .card-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
}
</style>
