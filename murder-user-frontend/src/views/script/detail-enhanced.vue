<template>
  <div class="script-detail-enhanced">
    <!-- 骨架屏 -->
    <SkeletonScriptDetail v-if="loading" />
    
    <!-- 真实内容 -->
    <div class="detail-container" v-else-if="script">
      <!-- 顶部导航面包屑 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/script' }">剧本大厅</el-breadcrumb-item>
        <el-breadcrumb-item>{{ script.name }}</el-breadcrumb-item>
      </el-breadcrumb>

      <!-- 主要内容区 -->
      <div class="main-content">
        <!-- 左侧图片展示区 -->
        <div class="left-section">
          <!-- 图片轮播/预览 -->
          <div class="image-gallery">
            <el-carousel :interval="5000" height="500px" indicator-position="outside">
              <el-carousel-item v-for="(image, index) in scriptImages" :key="index">
                <el-image 
                  :src="image" 
                  fit="cover" 
                  class="carousel-image"
                  :preview-src-list="scriptImages"
                  :initial-index="index"
                  preview-teleported
                >
                  <template #error>
                    <div class="image-slot">
                      <el-icon><Picture /></el-icon>
                      <span>暂无图片</span>
                    </div>
                  </template>
                </el-image>
              </el-carousel-item>
            </el-carousel>
          </div>

          <!-- 视频介绍 -->
          <div class="video-section" v-if="script.videoUrl">
            <h3><el-icon><VideoCamera /></el-icon> 视频介绍</h3>
            <video :src="script.videoUrl" controls class="video-player"></video>
          </div>

          <!-- 标签页：详情、角色、评价 -->
          <el-tabs v-model="activeTab" class="detail-tabs">
            <!-- 剧本详情 -->
            <el-tab-pane label="剧本详情" name="detail">
              <div class="script-description" v-html="script.description"></div>
            </el-tab-pane>

            <!-- 角色介绍 -->
            <el-tab-pane label="角色介绍" name="roles">
              <div class="roles-list">
                <div class="role-card" v-for="role in roles" :key="role.id">
                  <div class="role-avatar">
                    <el-avatar :size="80" :src="role.avatar">
                      <el-icon><User /></el-icon>
                    </el-avatar>
                  </div>
                  <div class="role-info">
                    <h4>{{ role.name }}</h4>
                    <el-tag :type="role.gender === '男' ? 'primary' : 'danger'" size="small">
                      {{ role.gender }}
                    </el-tag>
                    <p class="role-desc">{{ role.description }}</p>
                  </div>
                </div>
                <el-empty v-if="roles.length === 0" description="暂无角色信息" />
              </div>
            </el-tab-pane>

            <!-- 用户评价 -->
            <el-tab-pane name="reviews">
              <template #label>
                <span>用户评价 <el-badge :value="reviewTotal" class="badge" /></span>
              </template>
              
              <!-- 评分分布图 -->
              <div class="rating-distribution">
                <div class="rating-summary">
                  <div class="avg-rating">
                    <span class="score">{{ script.rating || 0 }}</span>
                    <el-rate v-model="script.rating" disabled show-score text-color="#ff9900" />
                    <span class="total">基于 {{ reviewTotal }} 条评价</span>
                  </div>
                  <div class="rating-bars">
                    <div class="rating-bar" v-for="star in 5" :key="star">
                      <span class="star-label">{{ 6 - star }}星</span>
                      <el-progress 
                        :percentage="getRatingPercentage(6 - star)" 
                        :show-text="false"
                        :stroke-width="10"
                      />
                      <span class="count">{{ getRatingCount(6 - star) }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 评价列表 -->
              <div class="reviews-list">
                <div class="review-sort">
                  <el-radio-group v-model="reviewSort" size="small" @change="loadReviews">
                    <el-radio-button label="hot">热门评价</el-radio-button>
                    <el-radio-button label="latest">最新评价</el-radio-button>
                    <el-radio-button label="highest">评分最高</el-radio-button>
                  </el-radio-group>
                </div>

                <div class="review-item" v-for="review in reviews" :key="review.id">
                  <div class="review-header">
                    <el-avatar :src="review.userAvatar">{{ review.userName }}</el-avatar>
                    <div class="review-user">
                      <span class="username">{{ review.userName }}</span>
                      <el-rate v-model="review.rating" disabled size="small" />
                    </div>
                    <span class="review-time">{{ review.createTime }}</span>
                  </div>
                  <div class="review-content">{{ review.content }}</div>
                  <div class="review-actions">
                    <el-button text size="small">
                      <el-icon><ChatDotRound /></el-icon>
                      回复 ({{ review.replyCount || 0 }})
                    </el-button>
                    <el-button text size="small">
                      <el-icon><Star /></el-icon>
                      点赞 ({{ review.likeCount || 0 }})
                    </el-button>
                  </div>
                </div>

                <el-pagination
                  v-if="reviewTotal > 0"
                  v-model:current-page="reviewPage"
                  v-model:page-size="reviewPageSize"
                  :total="reviewTotal"
                  layout="prev, pager, next"
                  @current-change="loadReviews"
                  class="review-pagination"
                />
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>

        <!-- 右侧信息和操作区 -->
        <div class="right-section">
          <el-affix :offset="20">
            <div class="script-info-card">
              <h1 class="script-title">{{ script.name }}</h1>
              
              <div class="script-meta">
                <el-tag type="success">{{ script.categoryName }}</el-tag>
                <el-tag>{{ script.playerCount }}人</el-tag>
                <el-tag>{{ script.difficulty }}</el-tag>
                <el-tag>{{ script.duration }}小时</el-tag>
              </div>

              <div class="script-rating">
                <el-rate v-model="script.rating" disabled show-score text-color="#ff9900" />
                <span class="rating-text">{{ reviewTotal }} 条评价</span>
              </div>

              <el-divider />

              <div class="price-section">
                <span class="price-label">价格：</span>
                <span class="price">¥{{ script.price }}</span>
                <span class="price-unit">/人</span>
              </div>

              <div class="action-buttons">
                <el-button type="primary" size="large" @click="handleReserve" block>
                  <el-icon><Calendar /></el-icon>
                  立即预约
                </el-button>
                <div class="sub-actions">
                  <el-button 
                    size="default" 
                    :type="isFavorited ? 'warning' : 'default'"
                    @click="handleCollect"
                  >
                    <el-icon><Star :filled="isFavorited" /></el-icon>
                    {{ isFavorited ? '已收藏' : '收藏' }}
                  </el-button>
                  <el-button size="default" @click="handleShare">
                    <el-icon><Share /></el-icon>
                    分享
                  </el-button>
                </div>
              </div>

              <el-divider />

              <!-- 其他信息 -->
              <div class="extra-info">
                <div class="info-item">
                  <el-icon><User /></el-icon>
                  <span>适合人数：{{ script.playerCount }}人</span>
                </div>
                <div class="info-item">
                  <el-icon><Clock /></el-icon>
                  <span>游戏时长：{{ script.duration }}小时</span>
                </div>
                <div class="info-item">
                  <el-icon><TrendCharts /></el-icon>
                  <span>难度：{{ script.difficulty }}</span>
                </div>
              </div>
            </div>
          </el-affix>
        </div>
      </div>

      <!-- 相关推荐 -->
      <div class="recommendations" v-if="recommendedScripts.length > 0">
        <h2>相关推荐</h2>
        <el-row :gutter="20">
          <el-col :xs="12" :sm="8" :md="6" v-for="item in recommendedScripts" :key="item.id">
            <el-card class="recommend-card" shadow="hover" @click="goToScript(item.id)">
              <LazyImage :src="item.cover || '/default-script.jpg'" alt="推荐剧本" :height="120" class="recommend-cover" />
              <div class="recommend-info">
                <h4>{{ item.name }}</h4>
                <el-rate v-model="item.rating" disabled size="small" />
                <p class="recommend-price">¥{{ item.price }}/人</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 快速预约悬浮按钮 -->
    <el-affix :offset="80" position="bottom">
      <div class="float-actions">
        <el-button 
          type="primary" 
          :icon="Calendar" 
          circle 
          size="large"
          @click="handleReserve"
          class="float-btn"
        >
        </el-button>
      </div>
    </el-affix>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import SkeletonScriptDetail from '@/components/Skeleton/SkeletonScriptDetail.vue'
import LazyImage from '@/components/LazyImage.vue'
import { 
  getScriptDetail, 
  getScriptRoles, 
  getScriptReviews, 
  favoriteScript, 
  unfavoriteScript, 
  checkScriptFavoriteStatus 
} from '@/api/script'
import { checkFavoriteTask } from '@/api/user'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { 
  Calendar, 
  Star, 
  Share, 
  User, 
  Clock, 
  TrendCharts,
  Picture,
  VideoCamera,
  ChatDotRound
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const script = ref(null)
const roles = ref([])
const reviews = ref([])
const isFavorited = ref(false)
const activeTab = ref('detail')
const reviewSort = ref('hot')
const reviewPage = ref(1)
const reviewPageSize = ref(10)
const reviewTotal = ref(0)
const recommendedScripts = ref([])

// 图片列表（支持多图）
const scriptImages = computed(() => {
  if (!script.value) return ['/default-script.jpg']
  if (script.value.images && script.value.images.length > 0) {
    return script.value.images
  }
  return script.value.cover ? [script.value.cover] : ['/default-script.jpg']
})

// 加载剧本详情
const loadScriptDetail = async () => {
  loading.value = true
  try {
    const res = await getScriptDetail(route.params.id)
    if (res.code === 1 || res.code === 200) {
      script.value = res.data
      
      // 保存浏览历史
      saveBrowseHistory(script.value)
    }
  } catch (error) {
    console.error('加载剧本详情失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
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

// 加载角色信息
const loadRoles = async () => {
  try {
    const res = await getScriptRoles(route.params.id)
    if (res.code === 1 || res.code === 200) {
      roles.value = res.data || []
    }
  } catch (error) {
    console.error('加载角色信息失败:', error)
  }
}

// 加载评价
const loadReviews = async () => {
  try {
    const res = await getScriptReviews({
      scriptId: route.params.id,
      page: reviewPage.value,
      pageSize: reviewPageSize.value,
      sort: reviewSort.value
    })
    if (res.code === 1 || res.code === 200) {
      reviews.value = res.data.records || []
      reviewTotal.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载评价失败:', error)
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

// 处理收藏
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

// 处理预约
const handleReserve = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push({
    path: '/reservation/create',
    query: { scriptId: route.params.id }
  })
}

// 处理分享
const handleShare = () => {
  const url = window.location.href
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

// 获取评分百分比
const getRatingPercentage = (star) => {
  if (reviewTotal.value === 0) return 0
  const count = getRatingCount(star)
  return Math.round((count / reviewTotal.value) * 100)
}

// 获取评分数量
const getRatingCount = (star) => {
  // 这里应该从后端获取，暂时模拟数据
  return reviews.value.filter(r => Math.floor(r.rating) === star).length
}

// 跳转到其他剧本
const goToScript = (id) => {
  router.push(`/script/${id}`)
  // 重新加载数据
  loadScriptDetail()
  loadRoles()
  loadReviews()
  checkFavoriteStatus()
}

onMounted(() => {
  loadScriptDetail()
  loadRoles()
  loadReviews()
  checkFavoriteStatus()
  
  // 加载推荐剧本
  // recommendedScripts.value = ... 从API获取
})
</script>

<style scoped>
.script-detail-enhanced {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 80px;
}

.detail-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.breadcrumb {
  margin-bottom: 20px;
  background: white;
  padding: 15px 20px;
  border-radius: 8px;
}

.main-content {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 20px;
}

/* 左侧区域 */
.left-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.image-gallery {
  margin-bottom: 30px;
}

.carousel-image {
  width: 100%;
  height: 500px;
  border-radius: 8px;
}

.image-slot {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
}

.video-section {
  margin-bottom: 30px;
}

.video-section h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
  font-size: 18px;
  color: #303133;
}

.video-player {
  width: 100%;
  border-radius: 8px;
}

.detail-tabs {
  margin-top: 20px;
}

.script-description {
  padding: 20px 0;
  line-height: 1.8;
  color: #606266;
}

/* 角色列表 */
.roles-list {
  padding: 20px 0;
}

.role-card {
  display: flex;
  gap: 20px;
  padding: 20px;
  margin-bottom: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s;
}

.role-card:hover {
  background: #ebeef5;
  transform: translateX(5px);
}

.role-info h4 {
  margin: 0 0 10px;
  font-size: 16px;
}

.role-desc {
  margin: 10px 0 0;
  color: #606266;
  line-height: 1.6;
}

/* 评价区域 */
.rating-distribution {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.rating-summary {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 40px;
}

.avg-rating {
  text-align: center;
}

.avg-rating .score {
  display: block;
  font-size: 48px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.avg-rating .total {
  display: block;
  margin-top: 10px;
  color: #909399;
  font-size: 14px;
}

.rating-bars {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.rating-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}

.star-label {
  width: 40px;
  font-size: 14px;
  color: #606266;
}

.rating-bar .el-progress {
  flex: 1;
}

.rating-bar .count {
  width: 40px;
  text-align: right;
  font-size: 14px;
  color: #909399;
}

.review-sort {
  margin-bottom: 20px;
}

.review-item {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.review-user {
  flex: 1;
}

.username {
  display: block;
  font-weight: bold;
  margin-bottom: 5px;
}

.review-time {
  color: #909399;
  font-size: 12px;
}

.review-content {
  padding: 10px 0;
  line-height: 1.6;
  color: #606266;
}

.review-actions {
  display: flex;
  gap: 15px;
}

.review-pagination {
  margin-top: 20px;
  justify-content: center;
}

/* 右侧信息卡片 */
.right-section {
  position: sticky;
  top: 20px;
}

.script-info-card {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.script-title {
  font-size: 24px;
  margin: 0 0 15px;
  color: #303133;
}

.script-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
}

.script-rating {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.rating-text {
  color: #909399;
  font-size: 14px;
}

.price-section {
  display: flex;
  align-items: baseline;
  margin-bottom: 20px;
}

.price-label {
  font-size: 16px;
  color: #606266;
}

.price {
  font-size: 32px;
  font-weight: bold;
  color: #f56c6c;
  margin: 0 5px;
}

.price-unit {
  font-size: 14px;
  color: #909399;
}

.action-buttons {
  margin-bottom: 20px;
}

.sub-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-top: 10px;
}

.extra-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #606266;
  font-size: 14px;
}

/* 推荐区域 */
.recommendations {
  margin-top: 40px;
  background: white;
  border-radius: 8px;
  padding: 30px;
}

.recommendations h2 {
  margin: 0 0 20px;
  font-size: 20px;
}

.recommend-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.recommend-card:hover {
  transform: translateY(-5px);
}

.recommend-cover {
  width: 100%;
  height: 180px;
  object-fit: cover;
  border-radius: 4px;
}

.recommend-info {
  padding: 10px 0;
}

.recommend-info h4 {
  margin: 0 0 10px;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recommend-price {
  margin: 10px 0 0;
  color: #f56c6c;
  font-weight: bold;
}

/* 悬浮按钮 */
.float-actions {
  position: fixed;
  right: 40px;
  bottom: 40px;
}

.float-btn {
  width: 60px;
  height: 60px;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

/* 响应式 */
@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .right-section {
    position: static;
  }
}

@media (max-width: 768px) {
  .rating-summary {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .carousel-image {
    height: 300px;
  }
}
</style>
