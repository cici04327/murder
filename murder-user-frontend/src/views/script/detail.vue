<template>
  <div class="script-detail-container" v-loading="loading">
    <el-card v-if="script">
      <el-row :gutter="30">
        <el-col :xs="24" :md="10">
          <div class="script-cover">
            <img :src="script.cover || PLACEHOLDERS.SCRIPT" :alt="script.name" onerror="this.src=PLACEHOLDERS.SCRIPT" />
          </div>
        </el-col>
        
        <el-col :xs="24" :md="14">
          <div class="script-header">
            <h1>{{ script.name }}</h1>
            <div class="script-tags">
              <el-tag type="success">{{ script.categoryName }}</el-tag>
              <el-tag>{{ getDifficultyText(script.difficulty) }}</el-tag>
              <el-tag type="info">{{ script.playerCount }}人</el-tag>
              <el-tag type="warning">{{ script.duration }}小时</el-tag>
            </div>
          </div>
          
          <div class="script-rating">
            <el-rate v-model="script.rating" disabled show-score />
            <span class="review-count">({{ script.reviewCount || 0 }}条评价)</span>
          </div>
          
          <div class="script-price">
            <span class="price-label">参考价格：</span>
            <span class="price-value">¥{{ script.price }}/人</span>
          </div>
          
          <el-descriptions :column="1" border class="script-info">
            <el-descriptions-item label="剧本类型">{{ script.categoryName }}</el-descriptions-item>
            <el-descriptions-item label="游戏人数">{{ script.playerCount }}人</el-descriptions-item>
            <el-descriptions-item label="游戏时长">{{ script.duration }}小时</el-descriptions-item>
            <el-descriptions-item label="难度等级">{{ getDifficultyText(script.difficulty) }}</el-descriptions-item>
            <el-descriptions-item label="发布时间">{{ script.publishDate }}</el-descriptions-item>
          </el-descriptions>
          
          <div class="action-buttons">
            <el-button type="primary" size="large" @click="handleReserve">
              <el-icon><Calendar /></el-icon>
              立即预约
            </el-button>
            <el-button 
              size="large" 
              :type="isFavorited ? 'warning' : 'default'"
              @click="handleCollect"
            >
              <el-icon><Star :filled="isFavorited" /></el-icon>
              {{ isFavorited ? '已收藏' : '收藏' }}
            </el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>
    
    <el-card class="detail-card">
      <template #header>
        <div class="card-header">
          <span>剧本简介</span>
        </div>
      </template>
      <div class="script-description" v-html="script?.description || '暂无简介'"></div>
    </el-card>
    
    <el-card class="detail-card" v-if="roles.length > 0">
      <template #header>
        <div class="card-header">
          <span>角色介绍</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="role in roles" :key="role.id">
          <div class="role-card">
            <div class="role-avatar">
              <img :src="role.avatar || PLACEHOLDERS.AVATAR" :alt="role.name" onerror="this.src=PLACEHOLDERS.AVATAR" />
            </div>
            <div class="role-info">
              <h4>{{ role.roleName }}</h4>
              <el-tag size="small" :type="getGenderType(role.gender)">
                {{ getGenderText(role.gender) }}
              </el-tag>
              <p>{{ role.description }}</p>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
    
    <el-card class="detail-card">
      <template #header>
        <div class="card-header">
          <span>用户评价 ({{ reviews.length }})</span>
          <el-button type="primary" size="small" @click="showReviewDialog = true">
            写评价
          </el-button>
        </div>
      </template>
      
      <div class="reviews-list">
        <div class="review-item" v-for="review in reviews" :key="review.id">
          <div class="review-header">
            <el-avatar :src="review.userAvatar" :size="40" />
            <div class="review-user">
              <div class="username">{{ review.username }}</div>
              <el-rate v-model="review.rating" disabled size="small" />
            </div>
            <div class="review-date">{{ review.createTime }}</div>
          </div>
          <div class="review-content">{{ review.content }}</div>
        </div>
        
        <el-empty v-if="reviews.length === 0" description="暂无评价" />
      </div>
    </el-card>
    
    <!-- 评价对话框 -->
    <el-dialog v-model="showReviewDialog" title="评价剧本" width="500px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" show-text />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="5"
            placeholder="请输入评价内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReviewDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReview">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getScriptDetail, getScriptRoles, getScriptReviews, addScriptReview, getScriptCategories, favoriteScript, unfavoriteScript, checkScriptFavoriteStatus } from '@/api/script'
import { checkFavoriteTask } from '@/api/user'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { Calendar, Star } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const script = ref(null)
const roles = ref([])
const reviews = ref([])
const showReviewDialog = ref(false)
const categories = ref([])
const isFavorited = ref(false)

const reviewForm = reactive({
  rating: 5,
  content: ''
})

// 难度映射
const difficultyMap = {
  1: '简单',
  2: '普通',
  3: '困难',
  4: '硬核'
}

// 获取难度文本
const getDifficultyText = (difficulty) => {
  return difficultyMap[difficulty] || difficulty
}

// 获取性别文本
const getGenderText = (gender) => {
  const genderMap = {
    1: '男',
    2: '女',
    3: '不限'
  }
  return genderMap[gender] || '不限'
}

// 获取性别标签类型
const getGenderType = (gender) => {
  if (gender === 1) return 'primary'
  if (gender === 2) return 'danger'
  return 'info'
}

const loadScriptDetail = async () => {
  loading.value = true
  try {
    const res = await getScriptDetail(route.params.id)
    if (res.code === 200 && res.data) {
      script.value = res.data
      // 补充缺失的字段
      if (!script.value.publishDate) {
        script.value.publishDate = script.value.createTime ? script.value.createTime.split('T')[0] : ''
      }
      if (!script.value.reviewCount) {
        script.value.reviewCount = 0
      }
      // 获取分类名称
      if (script.value.categoryId && categories.value.length > 0) {
        const category = categories.value.find(c => c.id === script.value.categoryId)
        script.value.categoryName = category ? category.name : '未分类'
      }
      
      // 保存浏览历史
      saveBrowseHistory(script.value)
    } else {
      ElMessage.error(res.msg || '加载剧本详情失败')
      router.push('/script')
    }
  } catch (error) {
    console.error('加载剧本详情失败:', error)
    ElMessage.error('请求失败，请稍后重试')
    router.push('/script')
  } finally {
    loading.value = false
  }
}

const loadRoles = async () => {
  try {
    const res = await getScriptRoles(route.params.id)
    if (res.data) {
      roles.value = res.data
    }
  } catch (error) {
    console.error('加载角色信息失败:', error)
    // 角色信息接口暂未实现，不显示角色卡片
    roles.value = []
  }
}

const loadReviews = async () => {
  try {
    const res = await getScriptReviews({
      scriptId: route.params.id,
      page: 1,
      pageSize: 10
    })
    if (res.code === 200 && res.data) {
      reviews.value = res.data.records || res.data.list || []
    } else {
      reviews.value = []
    }
  } catch (error) {
    console.error('加载评价失败:', error)
    reviews.value = []
  }
}

const handleReserve = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push({
    path: '/reservation/create',
    query: { scriptId: script.value.id }
  })
}

const handleCollect = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    if (!isFavorited.value) {
      const res = await favoriteScript(route.params.id)
      isFavorited.value = true
      ElMessage.success(res.data || '收藏成功！')
      
      // 检查收藏任务
      try {
        const taskRes = await checkFavoriteTask()
        if (taskRes.code === 1 || taskRes.code === 200) {
          if (taskRes.data?.completed && !taskRes.data?.alreadyCompleted) {
            ElMessage.success('恭喜！完成收藏任务，获得20积分')
          } else if (taskRes.data?.currentFavorites) {
            const current = taskRes.data.currentFavorites
            const required = taskRes.data.requiredFavorites || 5
            if (current < required) {
              ElMessage.info(`已收藏${current}个剧本，还需${required - current}个即可获得20积分`)
            }
          }
        }
      } catch (err) {
        console.error('检查收藏任务失败:', err)
      }
    } else {
      await unfavoriteScript(route.params.id)
      isFavorited.value = false
      ElMessage.success('取消收藏')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    const errorMsg = error.response?.data?.msg || error.msg || '操作失败'
    ElMessage.error(errorMsg)
  }
}

// 保存浏览历史到localStorage
const saveBrowseHistory = (scriptData) => {
  try {
    // 验证数据有效性
    if (!scriptData || !scriptData.id || !scriptData.name) {
      console.warn('剧本数据无效，无法保存浏览历史:', scriptData)
      return
    }
    
    let history = []
    const savedHistory = localStorage.getItem('browseHistory')
    if (savedHistory) {
      history = JSON.parse(savedHistory)
    }
    
    // 添加新记录（去重）
    history = history.filter(item => item.id !== scriptData.id)
    
    const historyItem = {
      id: scriptData.id,
      name: scriptData.name,
      cover: scriptData.coverImage || scriptData.cover || '/default-script.jpg',
      categoryName: scriptData.categoryName || '未分类',
      difficulty: scriptData.difficulty || 2,
      playerCount: scriptData.playerCount || 6,
      rating: scriptData.rating || 4.5,
      price: scriptData.price || 0,
      viewTime: new Date().toISOString()
    }
    
    history.unshift(historyItem)
    
    // 只保留最近30条
    history = history.slice(0, 30)
    
    localStorage.setItem('browseHistory', JSON.stringify(history))
    console.log('浏览历史已保存:', scriptData.name, historyItem)
  } catch (error) {
    console.error('保存浏览历史失败:', error)
  }
}

const handleSubmitReview = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (!reviewForm.content.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }
  
  try {
    await addScriptReview({
      scriptId: route.params.id,
      rating: reviewForm.rating,
      content: reviewForm.content
    })
    ElMessage.success('评价成功')
    showReviewDialog.value = false
    reviewForm.rating = 5
    reviewForm.content = ''
    loadReviews()
  } catch (error) {
    console.error('提交评价失败:', error)
  }
}

const loadCategories = async () => {
  try {
    const res = await getScriptCategories()
    if (res.data) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

// 检查收藏状态
const checkFavoriteStatus = async () => {
  if (!userStore.isLoggedIn) {
    isFavorited.value = false
    return
  }
  
  try {
    const res = await checkScriptFavoriteStatus(route.params.id)
    if (res.code === 1 || res.code === 200) {
      isFavorited.value = res.data === true
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
    isFavorited.value = false
  }
}

onMounted(async () => {
  // 先加载分类，再加载剧本详情
  await loadCategories()
  await loadScriptDetail()
  loadRoles()
  loadReviews()
  checkFavoriteStatus()
})
</script>

<style scoped>
.script-detail-container {
  padding: 20px;
}

.script-cover {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
}

.script-cover img {
  width: 100%;
  height: auto;
}

.script-header {
  margin-bottom: 20px;
}

.script-header h1 {
  font-size: 32px;
  margin: 0 0 15px;
  color: #333;
}

.script-tags .el-tag {
  margin-right: 10px;
}

.script-rating {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.review-count {
  color: #999;
  font-size: 14px;
}

.script-price {
  margin-bottom: 20px;
  font-size: 18px;
}

.price-label {
  color: #666;
}

.price-value {
  font-size: 28px;
  font-weight: bold;
  color: #f56c6c;
  margin-left: 10px;
}

.script-info {
  margin-bottom: 30px;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.action-buttons .el-button {
  flex: 1;
}

.detail-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.script-description {
  line-height: 1.8;
  color: #666;
}

.role-card {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  display: flex;
  gap: 15px;
}

.role-avatar img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.role-info h4 {
  margin: 0 0 10px;
  font-size: 16px;
}

.role-info p {
  margin: 10px 0 0;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.reviews-list {
  max-height: 600px;
  overflow-y: auto;
}

.review-item {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.review-user {
  flex: 1;
}

.username {
  font-weight: bold;
  margin-bottom: 5px;
}

.review-date {
  color: #999;
  font-size: 12px;
}

.review-content {
  color: #666;
  line-height: 1.6;
  padding-left: 55px;
}
</style>
