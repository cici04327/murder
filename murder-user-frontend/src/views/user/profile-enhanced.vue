<template>
  <div class="profile-enhanced">
    <!-- 顶部用户信息卡片 -->
    <el-card class="user-info-card" shadow="always">
      <div class="user-header">
        <div class="user-avatar-section">
          <el-avatar :size="100" :src="userInfo.avatar" class="user-avatar">
            <el-icon size="50"><User /></el-icon>
          </el-avatar>
          <el-upload
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            action="/api/upload/avatar"
            accept="image/*"
          >
            <el-button size="small" class="upload-btn">
              <el-icon><Camera /></el-icon>
              更换头像
            </el-button>
          </el-upload>
        </div>
        
        <div class="user-info">
          <div class="user-name-row">
            <h2>{{ userInfo.username || '未登录' }}</h2>
            <el-tag v-if="userInfo.vipLevel" type="warning" effect="dark" size="large">
              <el-icon><Medal /></el-icon>
              VIP{{ userInfo.vipLevel }}
            </el-tag>
          </div>
          
          <div class="user-meta">
            <span class="meta-item">
              <el-icon><Phone /></el-icon>
              {{ userInfo.phone || '未绑定' }}
            </span>
            <span class="meta-item">
              <el-icon><Message /></el-icon>
              {{ userInfo.email || '未绑定' }}
            </span>
            <span class="meta-item">
              <el-icon><Calendar /></el-icon>
              注册于 {{ formatDate(userInfo.createTime) }}
            </span>
          </div>
          
          <div class="user-actions">
            <el-button type="primary" @click="editProfileDialog = true">
              <el-icon><Edit /></el-icon>
              编辑资料
            </el-button>
            <el-button @click="showPasswordDialog = true">
              <el-icon><Lock /></el-icon>
              修改密码
            </el-button>
            <el-button type="warning" v-if="!userInfo.vipLevel" @click="showVipDialog = true">
              <el-icon><Medal /></el-icon>
              开通VIP
            </el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 数据统计卡片 -->
    <el-row :gutter="20" class="stats-section">
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-primary">
          <div class="stat-content">
            <el-icon class="stat-icon" size="40"><Tickets /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.reservationCount }}</div>
              <div class="stat-label">我的预约</div>
            </div>
          </div>
          <el-button text class="stat-link" @click="$router.push('/user/reservations')">
            查看详情 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </el-card>
      </el-col>
      
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-success">
          <div class="stat-content">
            <el-icon class="stat-icon" size="40"><Star /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.favoriteCount }}</div>
              <div class="stat-label">收藏剧本</div>
            </div>
          </div>
          <el-button text class="stat-link" @click="$router.push('/user/favorites')">
            查看详情 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </el-card>
      </el-col>
      
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-warning">
          <div class="stat-content">
            <el-icon class="stat-icon" size="40"><Coin /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.points }}</div>
              <div class="stat-label">我的积分</div>
            </div>
          </div>
          <el-button text class="stat-link" @click="$router.push('/user/points')">
            查看详情 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </el-card>
      </el-col>
      
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-danger">
          <div class="stat-content">
            <el-icon class="stat-icon" size="40"><Ticket /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.couponCount }}</div>
              <div class="stat-label">优惠券</div>
            </div>
          </div>
          <el-button text class="stat-link" @click="$router.push('/user/coupons')">
            查看详情 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </el-card>
      </el-col>
    </el-row>

    <!-- 待处理事项 -->
    <el-card class="pending-section" shadow="never" v-if="hasPendingItems">
      <template #header>
        <div class="card-header">
          <span><el-icon><Bell /></el-icon> 待处理事项</span>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :xs="24" :sm="8" v-if="pendingItems.unpaidCount > 0">
          <div class="pending-item pending-warning" @click="handlePendingClick('unpaid')">
            <el-badge :value="pendingItems.unpaidCount" class="item-badge">
              <el-icon class="pending-icon" size="30"><Wallet /></el-icon>
            </el-badge>
            <div class="pending-info">
              <div class="pending-title">待付款</div>
              <div class="pending-desc">{{ pendingItems.unpaidCount }} 个订单待付款</div>
            </div>
            <el-icon class="pending-arrow"><ArrowRight /></el-icon>
          </div>
        </el-col>
        
        <el-col :xs="24" :sm="8" v-if="pendingItems.unusedCount > 0">
          <div class="pending-item pending-success" @click="handlePendingClick('unused')">
            <el-badge :value="pendingItems.unusedCount" class="item-badge">
              <el-icon class="pending-icon" size="30"><Calendar /></el-icon>
            </el-badge>
            <div class="pending-info">
              <div class="pending-title">待使用</div>
              <div class="pending-desc">{{ pendingItems.unusedCount }} 个预约待使用</div>
            </div>
            <el-icon class="pending-arrow"><ArrowRight /></el-icon>
          </div>
        </el-col>
        
        <el-col :xs="24" :sm="8" v-if="pendingItems.unreadCount > 0">
          <div class="pending-item pending-primary" @click="handlePendingClick('unread')">
            <el-badge :value="pendingItems.unreadCount" class="item-badge">
              <el-icon class="pending-icon" size="30"><Message /></el-icon>
            </el-badge>
            <div class="pending-info">
              <div class="pending-title">未读消息</div>
              <div class="pending-desc">{{ pendingItems.unreadCount }} 条消息未读</div>
            </div>
            <el-icon class="pending-arrow"><ArrowRight /></el-icon>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 快捷操作 -->
    <el-card class="quick-actions-section" shadow="never">
      <template #header>
        <div class="card-header">
          <span><el-icon><Operation /></el-icon> 快捷操作</span>
        </div>
      </template>
      
      <el-row :gutter="15">
        <el-col :xs="12" :sm="8" :md="6" v-for="action in quickActions" :key="action.name">
          <div class="quick-action-item" @click="handleQuickAction(action.path)">
            <el-icon class="action-icon" :style="{color: action.color}" size="35">
              <component :is="action.icon" />
            </el-icon>
            <div class="action-name">{{ action.name }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 成就展示 -->
    <el-card class="achievements-section" shadow="never">
      <template #header>
        <div class="card-header">
          <span><el-icon><Trophy /></el-icon> 我的成就</span>
          <el-button text size="small" @click="showAllAchievements = true">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      
      <el-row :gutter="15">
        <el-col :xs="12" :sm="8" :md="6" v-for="achievement in achievements" :key="achievement.id">
          <div class="achievement-item" :class="{'achievement-unlocked': achievement.unlocked}">
            <el-icon class="achievement-icon" size="40">
              <component :is="achievement.icon" />
            </el-icon>
            <div class="achievement-name">{{ achievement.name }}</div>
            <div class="achievement-desc">{{ achievement.desc }}</div>
            <el-progress 
              v-if="!achievement.unlocked" 
              :percentage="achievement.progress" 
              :show-text="false"
              style="width: 100%"
            />
            <el-tag v-else type="success" size="small">已解锁</el-tag>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 编辑资料对话框 -->
    <el-dialog v-model="editProfileDialog" title="编辑个人资料" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="editForm.username" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editProfileDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateProfile">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="500px">
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdatePassword">确定</el-button>
      </template>
    </el-dialog>

    <!-- VIP开通对话框 -->
    <el-dialog v-model="showVipDialog" title="开通VIP会员" width="800px">
      <div class="vip-plans">
        <div 
          class="vip-plan" 
          v-for="plan in vipPlans" 
          :key="plan.id"
          :class="{'vip-plan-selected': selectedPlan === plan.id}"
          @click="selectedPlan = plan.id"
        >
          <el-tag v-if="plan.tag" type="danger" class="plan-tag">{{ plan.tag }}</el-tag>
          <h3>{{ plan.name }}</h3>
          <div class="plan-price">
            <span class="price">¥{{ plan.price }}</span>
            <span class="unit">/{{ plan.duration }}</span>
          </div>
          <ul class="plan-features">
            <li v-for="feature in plan.features" :key="feature">
              <el-icon color="#67C23A"><Check /></el-icon>
              {{ feature }}
            </li>
          </ul>
        </div>
      </div>
      <template #footer>
        <el-button @click="showVipDialog = false">取消</el-button>
        <el-button type="warning" @click="handlePurchaseVip">
          <el-icon><Medal /></el-icon>
          立即开通
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserInfo } from '@/api/user'
import { getMyReservations } from '@/api/reservation'
import { getMyCoupons } from '@/api/coupon'
import { getFavoriteScripts } from '@/api/script'
import {
  User, Camera, Phone, Message, Calendar, Edit, Lock,
  Tickets, Star, Coin, Ticket, Bell, Wallet, ArrowRight,
  Operation, Trophy, Check, Medal,
  Grid, List, ChatLineRound, SetUp, DataAnalysis, Memo
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const userInfo = ref({
  username: '',
  phone: '',
  email: '',
  avatar: '',
  vipLevel: 0,
  createTime: ''
})

const stats = ref({
  reservationCount: 0,
  favoriteCount: 0,
  points: 0,
  couponCount: 0
})

const pendingItems = ref({
  unpaidCount: 0,
  unusedCount: 0,
  unreadCount: 0
})

const hasPendingItems = computed(() => {
  return pendingItems.value.unpaidCount > 0 || 
         pendingItems.value.unusedCount > 0 || 
         pendingItems.value.unreadCount > 0
})

const quickActions = ref([
  { name: '浏览剧本', path: '/script', icon: 'Grid', color: '#409EFF' },
  { name: '我的预约', path: '/user/reservations', icon: 'Tickets', color: '#67C23A' },
  { name: '我的收藏', path: '/user/favorites', icon: 'Star', color: '#E6A23C' },
  { name: '我的优惠券', path: '/user/coupons', icon: 'Ticket', color: '#F56C6C' },
  { name: '我的积分', path: '/user/points', icon: 'Coin', color: '#909399' },
  { name: '消息通知', path: '/user/notifications', icon: 'Message', color: '#409EFF' },
  { name: '账号设置', path: '/user/settings', icon: 'SetUp', color: '#606266' },
  { name: '数据统计', path: '/user/statistics', icon: 'DataAnalysis', color: '#67C23A' }
])

const achievements = ref([
  { id: 1, name: '初次体验', desc: '完成首次预约', icon: 'Tickets', unlocked: false, progress: 0 },
  { id: 2, name: '剧本达人', desc: '体验10个剧本', icon: 'Star', unlocked: false, progress: 0 },
  { id: 3, name: '社交达人', desc: '邀请5位好友', icon: 'User', unlocked: false, progress: 0 },
  { id: 4, name: '评论家', desc: '发表20条评价', icon: 'Memo', unlocked: false, progress: 0 }
])

const vipPlans = ref([
  {
    id: 1,
    name: '月度会员',
    duration: '月',
    price: 29,
    features: ['专属客服', '预约优先', '积分双倍', '每月赠送优惠券']
  },
  {
    id: 2,
    name: '季度会员',
    duration: '季',
    price: 79,
    tag: '推荐',
    features: ['专属客服', '预约优先', '积分三倍', '每月赠送优惠券', '生日特权']
  },
  {
    id: 3,
    name: '年度会员',
    duration: '年',
    price: 268,
    tag: '最划算',
    features: ['专属客服', '预约优先', '积分五倍', '每月赠送优惠券', '生日特权', '专属徽章']
  }
])

const editProfileDialog = ref(false)
const showPasswordDialog = ref(false)
const showVipDialog = ref(false)
const showAllAchievements = ref(false)
const selectedPlan = ref(2)

const editForm = reactive({
  username: '',
  phone: '',
  email: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 加载用户信息
const loadUserInfo = async () => {
  try {
    // 从 userStore 获取基本信息
    const user = userStore.userInfo
    if (user) {
      userInfo.value = {
        username: user.username || user.name || '用户',
        phone: user.phone || '',
        email: user.email || '',
        avatar: user.avatar || '',
        vipLevel: user.vipLevel || 0,
        createTime: user.createTime || ''
      }
      
      // 更新编辑表单
      editForm.username = userInfo.value.username
      editForm.phone = userInfo.value.phone
      editForm.email = userInfo.value.email
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    // 加载预约数量
    const reservationRes = await getMyReservations({ page: 1, pageSize: 1 })
    if (reservationRes.code === 1 || reservationRes.code === 200) {
      stats.value.reservationCount = reservationRes.data.total || 0
      
      // 计算待付款和待使用数量
      const allReservations = await getMyReservations({ page: 1, pageSize: 100 })
      if (allReservations.code === 1 || allReservations.code === 200) {
        const records = allReservations.data.records || []
        pendingItems.value.unpaidCount = records.filter(r => r.status === 0 || r.status === '待付款').length
        pendingItems.value.unusedCount = records.filter(r => r.status === 1 || r.status === '待使用').length
      }
    }
    
    // 加载收藏数量
    const favoriteRes = await getFavoriteScripts({ page: 1, pageSize: 1 })
    if (favoriteRes.code === 1 || favoriteRes.code === 200) {
      stats.value.favoriteCount = favoriteRes.data.total || (favoriteRes.data.records || []).length
    }
    
    // 加载优惠券数量
    const couponRes = await getMyCoupons({ page: 1, pageSize: 1 })
    if (couponRes.code === 1 || couponRes.code === 200) {
      stats.value.couponCount = couponRes.data.total || 0
    }
    
    // 加载积分（从 userStore）
    if (userStore.userInfo && userStore.userInfo.points !== undefined) {
      stats.value.points = userStore.userInfo.points
    }
    
    // 计算成就进度
    calculateAchievements()
    
    console.log('统计数据加载完成:', stats.value)
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 计算成就进度
const calculateAchievements = () => {
  // 初次体验：完成首次预约
  if (stats.value.reservationCount > 0) {
    achievements.value[0].unlocked = true
    achievements.value[0].progress = 100
  }
  
  // 剧本达人：体验10个剧本
  const scriptProgress = Math.min((stats.value.reservationCount / 10) * 100, 100)
  achievements.value[1].progress = Math.round(scriptProgress)
  if (stats.value.reservationCount >= 10) {
    achievements.value[1].unlocked = true
  }
  
  // 社交达人：邀请5位好友（暂时基于预约数估算）
  const socialProgress = Math.min((stats.value.reservationCount / 15) * 100, 100)
  achievements.value[2].progress = Math.round(socialProgress)
  if (stats.value.reservationCount >= 15) {
    achievements.value[2].unlocked = true
  }
  
  // 评论家：发表20条评价（暂时基于收藏数估算）
  const reviewProgress = Math.min((stats.value.favoriteCount / 5) * 100, 100)
  achievements.value[3].progress = Math.round(reviewProgress)
  if (stats.value.favoriteCount >= 5) {
    achievements.value[3].unlocked = true
  }
}

// 处理头像上传成功
const handleAvatarSuccess = (response) => {
  if (response.code === 1 || response.code === 200) {
    userInfo.value.avatar = response.data.url || response.data
    // 更新 userStore
    if (userStore.userInfo) {
      userStore.userInfo.avatar = userInfo.value.avatar
    }
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

// 处理快捷操作点击
const handleQuickAction = (path) => {
  router.push(path)
}

// 处理待处理事项点击
const handlePendingClick = (type) => {
  switch(type) {
    case 'unpaid':
      router.push('/user/reservations?status=unpaid')
      break
    case 'unused':
      router.push('/user/reservations?status=unused')
      break
    case 'unread':
      router.push('/user/notifications')
      break
  }
}

// 更新个人资料
const handleUpdateProfile = async () => {
  try {
    // 验证表单
    if (!editForm.username || !editForm.username.trim()) {
      ElMessage.warning('请输入昵称')
      return
    }
    
    if (editForm.phone && !/^1[3-9]\d{9}$/.test(editForm.phone)) {
      ElMessage.warning('请输入正确的手机号')
      return
    }
    
    if (editForm.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(editForm.email)) {
      ElMessage.warning('请输入正确的邮箱地址')
      return
    }
    
    try {
      // TODO: 调用API更新资料
      // await updateUserInfo(editForm)
      
      // 检查完善资料任务
      // import { completeProfileTask } from '@/api/user'
      // const taskRes = await completeProfileTask()
      // if (taskRes.code === 1 || taskRes.code === 200) {
      //   if (taskRes.data?.completed && !taskRes.data?.alreadyCompleted) {
      //     ElMessage.success('恭喜！完成完善资料任务，获得30积分')
      //   }
      // }
    } catch (error) {
      console.error('更新资料失败:', error)
    }
    
    // 更新本地数据
    userInfo.value.username = editForm.username
    userInfo.value.phone = editForm.phone
    userInfo.value.email = editForm.email
    
    // 更新 userStore
    if (userStore.userInfo) {
      userStore.userInfo.username = editForm.username
      userStore.userInfo.phone = editForm.phone
      userStore.userInfo.email = editForm.email
    }
    
    ElMessage.success('资料更新成功')
    editProfileDialog.value = false
  } catch (error) {
    console.error('更新资料失败:', error)
    ElMessage.error('更新失败，请稍后重试')
  }
}

// 修改密码
const handleUpdatePassword = async () => {
  try {
    // 验证表单
    if (!passwordForm.oldPassword) {
      ElMessage.warning('请输入原密码')
      return
    }
    
    if (!passwordForm.newPassword) {
      ElMessage.warning('请输入新密码')
      return
    }
    
    if (passwordForm.newPassword.length < 6) {
      ElMessage.warning('新密码长度至少6位')
      return
    }
    
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      ElMessage.error('两次密码输入不一致')
      return
    }
    
    if (passwordForm.oldPassword === passwordForm.newPassword) {
      ElMessage.warning('新密码不能与原密码相同')
      return
    }
    
    // TODO: 调用API修改密码
    // await updatePassword(passwordForm)
    
    ElMessage.success('密码修改成功，请重新登录')
    
    // 清空表单
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    
    showPasswordDialog.value = false
    
    // 可选：自动退出登录
    // setTimeout(() => {
    //   userStore.logout()
    //   router.push('/login')
    // }, 1500)
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error(error.msg || '修改失败，请稍后重试')
  }
}

// 购买VIP
const handlePurchaseVip = async () => {
  try {
    const plan = vipPlans.value.find(p => p.id === selectedPlan.value)
    
    if (!plan) {
      ElMessage.error('请选择套餐')
      return
    }
    
    // 确认购买
    await ElMessageBox.confirm(
      `确定要开通${plan.name}吗？费用为¥${plan.price}`,
      '确认开通',
      {
        confirmButtonText: '确定开通',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // TODO: 调用API开通VIP
    // await purchaseVip({ planId: plan.id })
    
    ElMessage.success(`开通${plan.name}成功！`)
    
    // 更新VIP等级
    userInfo.value.vipLevel = plan.id
    if (userStore.userInfo) {
      userStore.userInfo.vipLevel = plan.id
    }
    
    showVipDialog.value = false
    
    // 刷新页面数据
    loadUserInfo()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('开通VIP失败:', error)
      ElMessage.error(error.msg || '开通失败，请稍后重试')
    }
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}

onMounted(() => {
  loadUserInfo()
  loadStats()
})
</script>

<script>
export default {
  name: 'ProfileEnhanced'
}
</script>

<style scoped>
.profile-enhanced {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 用户信息卡片 */
.user-info-card {
  margin-bottom: 20px;
}

.user-header {
  display: flex;
  gap: 30px;
  align-items: flex-start;
}

.user-avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.upload-btn {
  font-size: 12px;
}

.user-info {
  flex: 1;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.user-name-row h2 {
  margin: 0;
  font-size: 28px;
  color: #303133;
}

.user-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
  font-size: 14px;
}

.user-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

/* 统计卡片 */
.stats-section {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 12px;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px 0;
}

.stat-icon {
  opacity: 0.8;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  line-height: 1;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-link {
  width: 100%;
  margin-top: 10px;
  border-top: 1px dashed #eee;
  padding-top: 10px;
}

.stat-primary { border-top: 3px solid #409EFF; }
.stat-primary .stat-value { color: #409EFF; }

.stat-success { border-top: 3px solid #67C23A; }
.stat-success .stat-value { color: #67C23A; }

.stat-warning { border-top: 3px solid #E6A23C; }
.stat-warning .stat-value { color: #E6A23C; }

.stat-danger { border-top: 3px solid #F56C6C; }
.stat-danger .stat-value { color: #F56C6C; }

/* 待处理事项 */
.pending-section {
  margin-bottom: 20px;
}

.pending-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 15px;
}

.pending-item:hover {
  background: #fff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateX(5px);
}

.pending-icon {
  flex-shrink: 0;
}

.pending-info {
  flex: 1;
}

.pending-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.pending-desc {
  font-size: 14px;
  color: #909399;
}

.pending-arrow {
  opacity: 0;
  transition: opacity 0.3s;
}

.pending-item:hover .pending-arrow {
  opacity: 1;
}

.pending-warning { border-left: 4px solid #E6A23C; }
.pending-success { border-left: 4px solid #67C23A; }
.pending-primary { border-left: 4px solid #409EFF; }

/* 快捷操作 */
.quick-actions-section {
  margin-bottom: 20px;
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px 10px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 15px;
}

.quick-action-item:hover {
  background: #fff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-3px);
}

.action-name {
  font-size: 14px;
  color: #606266;
  text-align: center;
}

/* 成就展示 */
.achievements-section {
  margin-bottom: 20px;
}

.achievement-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  text-align: center;
  margin-bottom: 15px;
  opacity: 0.6;
  transition: all 0.3s;
}

.achievement-item.achievement-unlocked {
  opacity: 1;
  background: linear-gradient(135deg, #FFF9E6 0%, #FFF 100%);
  border: 2px solid #E6A23C;
}

.achievement-icon {
  margin-bottom: 10px;
  color: #909399;
}

.achievement-unlocked .achievement-icon {
  color: #E6A23C;
}

.achievement-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.achievement-desc {
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
}

/* VIP套餐 */
.vip-plans {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  padding: 20px 0;
}

.vip-plan {
  position: relative;
  padding: 30px 20px;
  border: 2px solid #eee;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.vip-plan:hover {
  border-color: #E6A23C;
  box-shadow: 0 8px 16px rgba(230, 162, 60, 0.2);
}

.vip-plan-selected {
  border-color: #E6A23C;
  background: #FFF9E6;
}

.plan-tag {
  position: absolute;
  top: 10px;
  right: 10px;
}

.vip-plan h3 {
  margin: 0 0 15px;
  font-size: 20px;
  text-align: center;
}

.plan-price {
  text-align: center;
  margin-bottom: 20px;
}

.plan-price .price {
  font-size: 36px;
  font-weight: bold;
  color: #E6A23C;
}

.plan-price .unit {
  font-size: 14px;
  color: #909399;
}

.plan-features {
  list-style: none;
  padding: 0;
  margin: 0;
}

.plan-features li {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  font-size: 14px;
  color: #606266;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

/* 响应式 */
@media (max-width: 768px) {
  .user-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .user-meta {
    flex-direction: column;
    gap: 10px;
  }
  
  .vip-plans {
    grid-template-columns: 1fr;
  }
}
</style>
