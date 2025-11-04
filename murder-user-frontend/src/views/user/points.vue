<template>
  <div class="user-points">
    <el-card class="page-header">
      <h2>我的积分</h2>
      <p class="subtitle">积分可用于兑换优惠券和抵扣现金</p>
    </el-card>

    <!-- 积分概览 -->
    <el-card class="points-overview">
      <div class="overview-content">
        <div class="points-display">
          <div class="points-icon">
            <el-icon :size="60" color="#F59E0B">
              <TrophyBase />
            </el-icon>
          </div>
          <div class="points-info">
            <div class="current-points">{{ currentPoints }}</div>
            <div class="points-label">当前积分</div>
            <div class="points-level">
              <el-tag :type="levelInfo.type" size="small">{{ levelInfo.name }}</el-tag>
              <span class="level-progress">距离下一级还需 {{ levelInfo.nextLevelPoints }} 积分</span>
            </div>
          </div>
        </div>
        <div class="points-stats">
          <div class="stat-item">
            <div class="stat-value">{{ totalEarned }}</div>
            <div class="stat-label">累计获得</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ totalUsed }}</div>
            <div class="stat-label">累计使用</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ expiringSoon }}</div>
            <div class="stat-label">即将过期</div>
          </div>
        </div>
        <div class="quick-actions">
          <el-button type="primary" :icon="Calendar" @click="handleSignIn" :disabled="hasSignedToday">
            {{ hasSignedToday ? '今日已签到' : '每日签到 +10' }}
          </el-button>
          <el-button type="success" :icon="Ticket" @click="showExchangeDialog = true">
            兑换优惠券
          </el-button>
        </div>
      </div>
    </el-card>


    <!-- 积分任务 -->
    <el-card class="points-tasks">
      <template #header>
        <div class="card-header">
          <span>积分任务</span>
          <el-tag type="info" size="small">完成任务赚积分</el-tag>
        </div>
      </template>
      <el-row :gutter="15">
        <el-col :span="8" v-for="task in tasks" :key="task.id">
          <div class="task-card" :class="{ completed: task.completed }">
            <div class="task-icon">
              <el-icon :size="30" :color="task.completed ? '#67C23A' : '#409EFF'">
                <component :is="task.icon" />
              </el-icon>
            </div>
            <div class="task-info">
              <div class="task-title">{{ task.title }}</div>
              <div class="task-desc">{{ task.description }}</div>
              <div class="task-reward">
                <el-tag type="warning" size="small">+{{ task.points }} 积分</el-tag>
              </div>
            </div>
            <div class="task-action">
              <el-button 
                v-if="!task.completed" 
                type="primary" 
                size="small"
                @click="handleTaskAction(task)"
              >
                {{ task.actionText }}
              </el-button>
              <el-tag v-else type="success" size="small">已完成</el-tag>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 兑换优惠券对话框 -->
    <el-dialog 
      v-model="showExchangeDialog" 
      title="兑换优惠券" 
      width="800px"
      :close-on-click-modal="false"
    >
      <div class="exchange-content">
        <div class="current-points-tip">
          当前可用积分：<span class="highlight">{{ currentPoints }}</span>
        </div>
        <el-row :gutter="20">
          <el-col :span="12" v-for="coupon in coupons" :key="coupon.id">
            <div class="coupon-card">
              <div class="coupon-amount">
                <span class="symbol">¥</span>
                <span class="value">{{ coupon.amount }}</span>
              </div>
              <div class="coupon-info">
                <div class="coupon-title">{{ coupon.title }}</div>
                <div class="coupon-desc">{{ coupon.description }}</div>
                <div class="coupon-expire">有效期：{{ coupon.validDays }}天</div>
              </div>
              <div class="coupon-footer">
                <el-tag type="warning" size="small">{{ coupon.points }} 积分</el-tag>
                <el-button 
                  type="primary" 
                  size="small"
                  :disabled="currentPoints < coupon.points"
                  @click="handleExchange(coupon)"
                >
                  立即兑换
                </el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-dialog>

    <!-- 积分规则 -->
    <el-card class="points-rules">
      <template #header>
        <h3>积分规则</h3>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="rule-item">
            <el-icon :size="30" color="#67C23A"><CircleCheck /></el-icon>
            <div class="rule-title">完成预约</div>
            <div class="rule-desc">每次完成预约可获得 100 积分</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="rule-item">
            <el-icon :size="30" color="#E6A23C"><Star /></el-icon>
            <div class="rule-title">发表评价</div>
            <div class="rule-desc">每次评价可获得 50 积分</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="rule-item">
            <el-icon :size="30" color="#409EFF"><User /></el-icon>
            <div class="rule-title">邀请好友</div>
            <div class="rule-desc">好友注册可获得 200 积分</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="rule-item">
            <el-icon :size="30" color="#F56C6C"><Calendar /></el-icon>
            <div class="rule-title">每日签到</div>
            <div class="rule-desc">每日签到可获得 10 积分</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 积分明细 -->
    <el-card class="points-records">
      <template #header>
        <div class="records-header">
          <h3>积分明细</h3>
          <el-radio-group v-model="recordType" @change="loadRecords">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="earned">获得</el-radio-button>
            <el-radio-button label="used">使用</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <div v-loading="loading" class="records-list">
        <div v-if="records.length === 0" class="empty-state">
          <el-empty description="暂无积分记录"></el-empty>
        </div>

        <div v-else class="record-items">
          <div v-for="record in records" :key="record.id" class="record-item">
            <div class="record-icon">
              <el-icon 
                :size="24" 
                :color="record.type === 1 ? '#67C23A' : '#F56C6C'"
              >
                <component :is="getRecordIcon(record.type)" />
              </el-icon>
            </div>
            <div class="record-info">
              <div class="record-title">{{ record.description }}</div>
              <div class="record-time">{{ formatDateTime(record.createTime) }}</div>
            </div>
            <div class="record-points" :class="{ 'earned': record.type === 1, 'used': record.type === 2 }">
              {{ record.type === 1 ? '+' : '-' }}{{ record.points }}
            </div>
          </div>
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
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  TrophyBase, 
  CircleCheck, 
  Star, 
  User, 
  Calendar,
  ShoppingCart,
  Ticket,
  ChatDotSquare
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { 
  getUserPoints, 
  getPointsRecords, 
  signIn, 
  exchangeCoupon,
  getTasksStatus
} from '@/api/user'

const router = useRouter()

// 状态
const loading = ref(false)
const currentPoints = ref(0)
const totalEarned = ref(0)
const totalUsed = ref(0)
const expiringSoon = ref(0)

const recordType = ref('all')
const records = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 新增功能状态
const hasSignedToday = ref(false)
const showExchangeDialog = ref(false)

// 可兑换优惠券
const coupons = ref([
  {
    id: 1,
    title: '满100减10',
    description: '订单满100元可用',
    amount: 10,
    points: 100,
    validDays: 30
  },
  {
    id: 2,
    title: '满200减25',
    description: '订单满200元可用',
    amount: 25,
    points: 250,
    validDays: 30
  },
  {
    id: 3,
    title: '满300减40',
    description: '订单满300元可用',
    amount: 40,
    points: 400,
    validDays: 30
  },
  {
    id: 4,
    title: '满500减80',
    description: '订单满500元可用',
    amount: 80,
    points: 800,
    validDays: 60
  }
])

// 积分等级
const levelInfo = computed(() => {
  const points = currentPoints.value
  if (points < 100) {
    return { name: '青铜会员', type: '', nextLevelPoints: 100 - points }
  } else if (points < 500) {
    return { name: '白银会员', type: 'info', nextLevelPoints: 500 - points }
  } else if (points < 1000) {
    return { name: '黄金会员', type: 'warning', nextLevelPoints: 1000 - points }
  } else if (points < 5000) {
    return { name: '铂金会员', type: 'success', nextLevelPoints: 5000 - points }
  } else {
    return { name: '钻石会员', type: 'danger', nextLevelPoints: 0 }
  }
})

// 积分任务
const tasks = ref([
  {
    id: 1,
    title: '完成预约',
    description: '预约一次剧本杀',
    points: 100,
    icon: ShoppingCart,
    completed: false,
    actionText: '去预约'
  },
  {
    id: 2,
    title: '发表评价',
    description: '为剧本或门店评价',
    points: 50,
    icon: ChatDotSquare,
    completed: false,
    actionText: '去评价'
  },
  {
    id: 3,
    title: '完善资料',
    description: '完善个人资料',
    points: 30,
    icon: User,
    completed: false,
    actionText: '去完善'
  },
  {
    id: 4,
    title: '每日签到',
    description: '连续签到7天',
    points: 70,
    icon: Calendar,
    completed: false,
    actionText: '查看'
  },
  {
    id: 5,
    title: '邀请好友',
    description: '邀请好友注册',
    points: 200,
    icon: User,
    completed: false,
    actionText: '去邀请'
  },
  {
    id: 6,
    title: '收藏剧本',
    description: '收藏5个剧本',
    points: 20,
    icon: Star,
    completed: false,
    actionText: '去收藏'
  }
])

// 加载积分信息
const loadPoints = async () => {
  try {
    console.log('开始加载积分信息...')
    const res = await getUserPoints()
    console.log('积分信息响应:', res)
    if (res.code === 1 || res.code === 200) {
      const newPoints = res.data.currentPoints || 0
      console.log('更新积分:', currentPoints.value, '->', newPoints)
      currentPoints.value = newPoints
      totalEarned.value = res.data.totalEarned || 0
      totalUsed.value = res.data.totalUsed || 0
      expiringSoon.value = res.data.expiringSoon || 0
    } else {
      console.error('获取积分失败:', res)
    }
  } catch (error) {
    console.error('加载积分信息失败:', error)
    ElMessage.error('加载积分信息失败')
  }
}

// 分页处理函数
const handlePageChange = (newPage) => {
  console.log('积分记录页码变化:', newPage)
  currentPage.value = newPage
  loadRecords()
}

const handleSizeChange = (newSize) => {
  console.log('积分记录每页大小变化:', newSize)
  pageSize.value = newSize
  currentPage.value = 1
  loadRecords()
}

// 加载积分记录
const loadRecords = async () => {
  loading.value = true
  try {
    console.log('加载积分记录，参数:', { page: currentPage.value, pageSize: pageSize.value, type: recordType.value })
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    
    // 根据筛选类型添加参数
    if (recordType.value === 'earned') {
      params.type = 1  // 获得
    } else if (recordType.value === 'used') {
      params.type = 2  // 使用
    }
    
    const res = await getPointsRecords(params)
    if (res.code === 1 || res.code === 200) {
      records.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载积分记录失败:', error)
    ElMessage.error('加载积分记录失败')
  } finally {
    loading.value = false
  }
}

// 获取记录图标
const getRecordIcon = (type) => {
  // type: 1=增加, 2=减少
  if (type === 1) {
    return CircleCheck  // 绿色勾号 - 获得积分
  } else if (type === 2) {
    return ShoppingCart  // 购物车 - 使用积分
  }
  return Star  // 默认星星
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 签到功能
const handleSignIn = async () => {
  if (hasSignedToday.value) {
    ElMessage.warning('今日已签到')
    return
  }
  
  try {
    console.log('开始签到...')
    const res = await signIn()
    console.log('签到响应:', res)
    
    if (res.code === 1 || res.code === 200) {
      ElMessage.success('签到成功！获得10积分')
      hasSignedToday.value = true
      
      // 更新任务4的状态
      const signInTask = tasks.value.find(t => t.id === 4)
      if (signInTask) {
        signInTask.completed = true
      }
      
      // 延迟刷新，确保后端数据已更新
      console.log('延迟500ms后刷新数据...')
      setTimeout(async () => {
        await loadPoints()
        await loadRecords()
        console.log('数据刷新完成')
      }, 500)
      
    } else {
      ElMessage.error(res.msg || '签到失败')
      // 如果后端返回"今日已签到"，更新前端状态
      if (res.msg && res.msg.includes('已签到')) {
        hasSignedToday.value = true
        const signInTask = tasks.value.find(t => t.id === 4)
        if (signInTask) {
          signInTask.completed = true
        }
        // 即使已签到，也刷新一次数据
        await loadPoints()
        await loadRecords()
      }
    }
  } catch (error) {
    console.error('签到失败:', error)
    const errorMsg = error.response?.data?.msg || error.message || '签到失败'
    ElMessage.error(errorMsg)
    
    // 如果后端返回"今日已签到"，更新前端状态
    if (errorMsg.includes('已签到')) {
      hasSignedToday.value = true
      const signInTask = tasks.value.find(t => t.id === 4)
      if (signInTask) {
        signInTask.completed = true
      }
      // 即使已签到，也刷新一次数据
      await loadPoints()
      await loadRecords()
    }
  }
}


// 处理任务动作
const handleTaskAction = async (task) => {
  switch (task.id) {
    case 1: // 完成预约
      ElMessage.info('前往预约剧本')
      router.push('/script')
      break
      
    case 2: // 发表评价
      ElMessage.info('请先完成预约，然后进行评价')
      router.push('/user/reservations')
      break
      
    case 3: // 完善资料
      // 检查是否已完善资料
      await handleCompleteProfile()
      break
      
    case 4: // 每日签到
      handleSignIn()
      break
      
    case 5: // 邀请好友
      handleInviteFriend()
      break
      
    case 6: // 收藏剧本
      // 检查收藏数量
      await handleCheckFavorite()
      break
  }
}

// 处理完善资料任务
const handleCompleteProfile = async () => {
  try {
    const res = await completeProfileTask()
    
    if (res.code === 1 || res.code === 200) {
      if (res.data?.alreadyCompleted) {
        ElMessage.info('您已完成过该任务')
        const task = tasks.value.find(t => t.id === 3)
        if (task) task.completed = true
      } else if (res.data?.completed) {
        ElMessage.success('完善资料任务已完成！获得30积分')
        const task = tasks.value.find(t => t.id === 3)
        if (task) task.completed = true
        
        // 刷新数据
        setTimeout(async () => {
          await loadPoints()
          await loadRecords()
        }, 500)
      } else {
        // 资料未完善，引导用户前往完善
        ElMessage.info('请前往个人中心完善资料')
        router.push('/user/profile')
      }
    }
  } catch (error) {
    console.error('完善资料任务失败:', error)
    ElMessage.info('前往个人资料页面完善信息')
    router.push('/user/profile')
  }
}

// 检查收藏任务
const handleCheckFavorite = async () => {
  try {
    const res = await checkFavoriteTask()
    
    if (res.code === 1 || res.code === 200) {
      if (res.data?.alreadyCompleted) {
        ElMessage.info('您已完成过该任务')
        const task = tasks.value.find(t => t.id === 6)
        if (task) task.completed = true
      } else if (res.data?.completed) {
        ElMessage.success(`收藏任务已完成！获得20积分`)
        const task = tasks.value.find(t => t.id === 6)
        if (task) task.completed = true
        
        // 刷新数据
        setTimeout(async () => {
          await loadPoints()
          await loadRecords()
        }, 500)
      } else {
        // 未达到收藏条件
        const current = res.data?.currentFavorites || 0
        const required = res.data?.requiredFavorites || 5
        ElMessage.info(`您已收藏${current}个剧本，还需收藏${required - current}个`)
        router.push('/script')
      }
    }
  } catch (error) {
    console.error('检查收藏任务失败:', error)
    ElMessage.info('前往剧本页面收藏喜欢的剧本')
    router.push('/script')
  }
}

// 邀请好友功能
const handleInviteFriend = async () => {
  try {
    // 获取或生成邀请码
    const inviteRes = await getInviteInfo()
    let inviteCode = ''
    
    if (inviteRes.code === 1 || inviteRes.code === 200) {
      inviteCode = inviteRes.data?.inviteCode
      
      // 如果没有邀请码，生成一个
      if (!inviteCode) {
        const generateRes = await generateInviteCode()
        if (generateRes.code === 1 || generateRes.code === 200) {
          inviteCode = generateRes.data?.inviteCode
        }
      }
    }
    
    if (inviteCode) {
      const inviteUrl = `${window.location.origin}/register?inviteCode=${inviteCode}`
      
      // 复制邀请链接
      try {
        await navigator.clipboard.writeText(inviteUrl)
        ElMessage.success({
          message: `邀请链接已复制！每成功邀请一位好友可获得200积分`,
          duration: 5000
        })
      } catch (e) {
        // 如果复制失败，显示链接
        ElMessage.success({
          message: `您的邀请码：${inviteCode}<br>邀请链接：${inviteUrl}`,
          duration: 10000,
          dangerouslyUseHTMLString: true
        })
      }
    } else {
      ElMessage.error('获取邀请码失败，请稍后重试')
    }
  } catch (error) {
    console.error('生成邀请链接失败:', error)
    ElMessage.error('生成邀请链接失败，请稍后重试')
  }
}

// 兑换优惠券
const handleExchange = async (coupon) => {
  if (currentPoints.value < coupon.points) {
    ElMessage.warning('积分不足，无法兑换')
    return
  }
  
  try {
    const res = await exchangeCoupon(coupon.id, coupon.points)
    if (res.code === 1) {
      ElMessage.success(`成功兑换${coupon.title}！消耗${coupon.points}积分`)
      showExchangeDialog.value = false
      loadPoints()
      loadRecords()
    } else {
      ElMessage.error(res.msg || '兑换失败')
    }
  } catch (error) {
    console.error('兑换失败:', error)
    ElMessage.error(error.response?.data?.msg || '兑换失败，请重试')
  }
}

// 检查任务完成状态
const checkAllTasksStatus = async () => {
  try {
    const res = await getTasksStatus()
    console.log('任务状态响应:', res)
    
    if (res.code === 1 || res.code === 200) {
      const taskStatus = res.data || {}
      
      // 更新各任务状态
      tasks.value.forEach(task => {
        switch (task.id) {
          case 1: // 完成预约
            task.completed = taskStatus.hasReservation || false
            break
          case 2: // 发表评价
            task.completed = taskStatus.hasReview || false
            break
          case 3: // 完善资料
            task.completed = taskStatus.hasCompleteProfile || false
            break
          case 4: // 每日签到
            task.completed = taskStatus.hasSignedToday || false
            hasSignedToday.value = taskStatus.hasSignedToday || false
            break
          case 5: // 邀请好友
            task.completed = taskStatus.hasInviteFriend || false
            break
          case 6: // 收藏剧本
            task.completed = taskStatus.hasFavoriteScript || false
            break
        }
      })
      
      console.log('任务状态更新完成:', tasks.value)
    }
  } catch (error) {
    console.error('检查任务状态失败:', error)
    // 如果检查失败，尝试从记录中检查签到状态
    await checkSignInStatusFromRecords()
  }
}

// 从记录中检查签到状态（备用方法）
const checkSignInStatusFromRecords = async () => {
  try {
    const res = await getPointsRecords({ page: 1, pageSize: 10, type: 1 })
    if (res.code === 1 || res.code === 200) {
      const records = res.data.records || []
      const today = new Date().toDateString()
      
      const hasSignedIn = records.some(record => {
        const recordDate = new Date(record.createTime).toDateString()
        return recordDate === today && record.description && record.description.includes('签到')
      })
      
      hasSignedToday.value = hasSignedIn
      const signInTask = tasks.value.find(t => t.id === 4)
      if (signInTask) {
        signInTask.completed = hasSignedIn
      }
    }
  } catch (error) {
    console.error('检查签到状态失败:', error)
  }
}

// 初始化
onMounted(async () => {
  await loadPoints()
  await loadRecords()
  await checkAllTasksStatus()
})
</script>

<style scoped>
.user-points {
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

/* 积分概览 */
.points-overview {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.points-overview :deep(.el-card__body) {
  padding: 30px;
}

.overview-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.overview-content > div:first-child {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.points-display {
  display: flex;
  align-items: center;
  gap: 20px;
}

.points-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #FEF3C7 0%, #FCD34D 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.points-info {
  text-align: left;
}

.current-points {
  font-size: 48px;
  font-weight: bold;
  color: #FCD34D;
  line-height: 1;
}

.points-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  margin-top: 8px;
}

.points-level {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.level-progress {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
}

.points-stats {
  display: flex;
  gap: 60px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: rgba(255, 255, 255, 0.95);
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 8px;
}

.quick-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.quick-actions .el-button {
  flex: 1;
  max-width: 200px;
}

/* 积分任务 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 积分任务 */
.points-tasks {
  margin-bottom: 20px;
}

.task-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s;
  cursor: pointer;
  background: white;
  margin-bottom: 15px;
}

.task-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.task-card.completed {
  background: #f0f9ff;
  border-color: #67C23A;
}

.task-icon {
  text-align: center;
  margin-bottom: 15px;
}

.task-info {
  text-align: center;
  margin-bottom: 15px;
}

.task-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.task-desc {
  font-size: 13px;
  color: #909399;
  margin-bottom: 10px;
}

.task-reward {
  margin-top: 8px;
}

.task-action {
  text-align: center;
}

.task-action .el-button {
  width: 100%;
}

/* 兑换对话框 */
.exchange-content {
  padding: 10px 0;
}

.current-points-tip {
  text-align: center;
  font-size: 16px;
  margin-bottom: 25px;
  padding: 15px;
  background: linear-gradient(135deg, #FEF3C7 0%, #FCD34D 100%);
  border-radius: 8px;
}

.current-points-tip .highlight {
  font-size: 28px;
  font-weight: bold;
  color: #F59E0B;
  margin-left: 10px;
}

.coupon-card {
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  transition: all 0.3s;
  background: linear-gradient(135deg, #ffffff 0%, #f9fafb 100%);
}

.coupon-card:hover {
  border-color: #409EFF;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.coupon-amount {
  text-align: center;
  margin-bottom: 15px;
  color: #F56C6C;
}

.coupon-amount .symbol {
  font-size: 24px;
  font-weight: bold;
}

.coupon-amount .value {
  font-size: 48px;
  font-weight: bold;
}

.coupon-info {
  text-align: center;
  margin-bottom: 15px;
}

.coupon-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.coupon-desc {
  font-size: 13px;
  color: #606266;
  margin-bottom: 5px;
}

.coupon-expire {
  font-size: 12px;
  color: #909399;
}

.coupon-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #e4e7ed;
}

/* 积分规则 */
.points-rules {
  margin-bottom: 20px;
}

.points-rules h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.rule-item {
  text-align: center;
  padding: 20px;
  border: 1px solid #EBEEF5;
  border-radius: 8px;
  transition: all 0.3s;
}

.rule-item:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
  transform: translateY(-3px);
}

.rule-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin: 10px 0;
}

.rule-desc {
  font-size: 13px;
  color: #909399;
  line-height: 1.5;
}

/* 积分明细 */
.points-records h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.records-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.records-list {
  min-height: 400px;
}

.record-items {
  margin-top: 20px;
}

.record-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #EBEEF5;
  transition: background-color 0.3s;
}

.record-item:hover {
  background-color: #F5F7FA;
}

.record-item:last-child {
  border-bottom: none;
}

.record-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #F5F7FA;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.record-info {
  flex: 1;
}

.record-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 5px;
}

.record-time {
  font-size: 12px;
  color: #909399;
}

.record-points {
  font-size: 20px;
  font-weight: bold;
  margin-left: 15px;
}

.record-points.earned {
  color: #67C23A;
}

.record-points.used {
  color: #F56C6C;
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
