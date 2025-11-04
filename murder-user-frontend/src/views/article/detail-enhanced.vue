<template>
  <div class="article-detail-container" v-loading="loading">
    <!-- 返回按钮 -->
    <div class="back-button-wrapper">
      <el-button @click="goBack" :icon="ArrowLeft" circle size="large" class="back-button" />
    </div>

    <!-- 文章头部 -->
    <div class="article-header" v-if="article">
      <div class="header-background">
        <div class="header-overlay"></div>
        <img v-if="article.coverImage" :src="article.coverImage" class="header-bg-image" />
      </div>
      
      <div class="header-content">
        <div class="category-badge-wrapper">
          <span class="category-badge" :style="{ background: getCategoryColor(article.category) }">
            {{ getCategoryIcon(article.category) }} {{ article.categoryName }}
          </span>
          <span class="badge-top" v-if="article.isTop === 1">📌 置顶</span>
        </div>
        
        <h1 class="article-title-main">{{ article.title }}</h1>
        
        <div class="article-meta-bar">
          <div class="meta-left">
            <span class="meta-item author-item">
              <el-avatar :size="32" class="author-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="author-name">{{ article.authorName || '匿名作者' }}</span>
            </span>
            <span class="meta-item">
              <el-icon><Calendar /></el-icon>
              {{ formatDate(article.publishTime) }}
            </span>
          </div>
          
          <div class="meta-right">
            <span class="stat-badge">
              <el-icon><View /></el-icon>
              {{ formatNumber(article.viewCount) }}
            </span>
            <span class="stat-badge">
              <el-icon><StarFilled /></el-icon>
              {{ formatNumber(article.likeCount) }}
            </span>
            <span class="stat-badge">
              <el-icon><ChatDotRound /></el-icon>
              {{ formatNumber(article.commentCount || 0) }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 文章主体 -->
    <div class="article-main" v-if="article">
      <!-- 文章摘要 -->
      <div class="article-summary-box" v-if="article.summary">
        <div class="summary-icon">💡</div>
        <p class="summary-text">{{ article.summary }}</p>
      </div>

      <!-- 文章内容 -->
      <div class="article-content">
        <div v-html="article.content"></div>
      </div>

      <!-- 文章标签 -->
      <div class="article-tags" v-if="article.categoryName">
        <el-tag type="primary" size="large" effect="plain">
          # {{ article.categoryName }}
        </el-tag>
      </div>
    </div>

    <!-- 文章操作栏 -->
    <div class="article-actions-bar" v-if="article">
      <div class="actions-container">
        <el-button 
          size="large"
          round
          :icon="liked ? StarFilled : Star" 
          @click="handleLike" 
          :loading="liking"
          :class="['action-button', 'like-button', { 'is-liked': liked }]"
        >
          <span class="action-text">{{ liked ? '已点赞' : '点赞' }}</span>
          <span class="action-count">{{ formatNumber(article.likeCount) }}</span>
        </el-button>
        
        <el-button 
          size="large"
          round
          :icon="Share" 
          @click="handleShare"
          class="action-button share-button"
        >
          分享文章
        </el-button>
      </div>
    </div>

    <!-- 评论区域 -->
    <div class="comment-section" v-if="article">
      <div class="comment-header">
        <h3>
          <el-icon><ChatDotRound /></el-icon>
          评论 ({{ comments.length }})
        </h3>
      </div>

      <!-- 发表评论 -->
      <div class="comment-editor">
        <el-input
          v-model="commentContent"
          type="textarea"
          :rows="4"
          placeholder="说说你的看法..."
          maxlength="500"
          show-word-limit
        />
        <div class="comment-actions">
          <el-button type="primary" @click="submitComment" :loading="commenting">
            发表评论
          </el-button>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comment-list" v-loading="loadingComments">
        <div v-if="comments.length === 0" class="empty-comments">
          <el-empty description="暂无评论，快来发表第一条评论吧！" />
        </div>
        <div v-else>
          <div 
            class="comment-item" 
            v-for="comment in comments" 
            :key="comment.id"
          >
            <div class="comment-avatar">
              <el-avatar :size="40" :src="comment.userAvatar">
                {{ comment.userName ? comment.userName[0] : 'U' }}
              </el-avatar>
            </div>
            <div class="comment-main">
              <div class="comment-user">
                <span class="user-name">{{ comment.userName || '匿名用户' }}</span>
                <span class="comment-time">{{ formatCommentTime(comment.createTime) }}</span>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
              <div class="comment-footer">
                <el-button 
                  text 
                  size="small" 
                  :icon="comment.userLiked ? StarFilled : Star"
                  @click="likeComment(comment)"
                  :class="{ 'liked': comment.userLiked }"
                >
                  {{ comment.likeCount || 0 }}
                </el-button>
                <el-button 
                  text 
                  size="small" 
                  :icon="ChatLineRound"
                  @click="replyComment(comment)"
                >
                  回复
                </el-button>
              </div>

              <!-- 回复输入框 -->
              <div class="reply-editor" v-if="replyingTo === comment.id">
                <el-input
                  v-model="replyContent"
                  type="textarea"
                  :rows="3"
                  :placeholder="`回复 @${comment.userName}...`"
                  maxlength="300"
                  show-word-limit
                />
                <div class="reply-actions">
                  <el-button size="small" @click="cancelReply">取消</el-button>
                  <el-button size="small" type="primary" @click="submitReply(comment)" :loading="replying">
                    回复
                  </el-button>
                </div>
              </div>

              <!-- 回复列表 -->
              <div class="reply-list" v-if="comment.replies && comment.replies.length > 0">
                <div 
                  class="reply-item" 
                  v-for="reply in comment.replies" 
                  :key="reply.id"
                >
                  <div class="reply-avatar">
                    <el-avatar :size="30" :src="reply.userAvatar">
                      {{ reply.userName ? reply.userName[0] : 'U' }}
                    </el-avatar>
                  </div>
                  <div class="reply-main">
                    <div class="reply-user">
                      <span class="user-name">{{ reply.userName || '匿名用户' }}</span>
                      <span class="reply-to" v-if="reply.replyToUserName">
                        回复 @{{ reply.replyToUserName }}
                      </span>
                      <span class="reply-time">{{ formatCommentTime(reply.createTime) }}</span>
                    </div>
                    <div class="reply-content">{{ reply.content }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, getCurrentInstance } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  getArticleDetail, 
  likeArticle, 
  unlikeArticle,
  getArticleComments,
  addArticleComment,
  likeArticleComment,
  unlikeArticleComment
} from '@/api/article'
import { ElMessage } from 'element-plus'
import { 
  User, Calendar, View, StarFilled, Star, Share, 
  ChatDotRound, ChatLineRound, ArrowLeft 
} from '@element-plus/icons-vue'

// 确保组件在路由上下文中
const instance = getCurrentInstance()
let router, route

try {
  router = useRouter()
  route = useRoute()
} catch (error) {
  console.error('Router injection failed:', error)
  // 如果在非路由上下文中，尝试从实例获取
  if (instance) {
    router = instance.appContext.config.globalProperties.$router
    route = instance.appContext.config.globalProperties.$route
  }
}

const loading = ref(false)
const liking = ref(false)
const commenting = ref(false)
const replying = ref(false)
const loadingComments = ref(false)

const article = ref(null)
const liked = ref(false)

const commentContent = ref('')
const replyContent = ref('')
const replyingTo = ref(null)
const comments = ref([])

const loadArticle = async () => {
  loading.value = true
  try {
    const res = await getArticleDetail(route.params.id)
    if (res.code === 1 || res.code === 200) {
      article.value = res.data
      // 加载评论
      loadComments()
    }
  } catch (error) {
    console.error('加载文章详情失败:', error)
    ElMessage.error('加载文章失败')
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  loadingComments.value = true
  try {
    const res = await getArticleComments(route.params.id)
    if (res.code === 1 || res.code === 200) {
      comments.value = res.data || []
    }
  } catch (error) {
    console.error('加载评论失败:', error)
  } finally {
    loadingComments.value = false
  }
}

const handleLike = async () => {
  liking.value = true
  try {
    if (!liked.value) {
      await likeArticle(route.params.id)
      article.value.likeCount++
      liked.value = true
      ElMessage.success('点赞成功')
    } else {
      await unlikeArticle(route.params.id)
      article.value.likeCount--
      liked.value = false
      ElMessage.success('取消点赞')
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error(error.response?.data?.msg || '操作失败')
  } finally {
    liking.value = false
  }
}

const handleShare = () => {
  const url = window.location.href
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

const submitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  commenting.value = true
  try {
    await addArticleComment(route.params.id, {
      content: commentContent.value
    })
    
    commentContent.value = ''
    ElMessage.success('评论发表成功')
    
    // 重新加载评论列表和文章信息
    await Promise.all([loadComments(), loadArticle()])
  } catch (error) {
    console.error('发表评论失败:', error)
    ElMessage.error(error.response?.data?.msg || '发表失败')
  } finally {
    commenting.value = false
  }
}

const replyComment = (comment) => {
  replyingTo.value = comment.id
  replyContent.value = ''
}

const cancelReply = () => {
  replyingTo.value = null
  replyContent.value = ''
}

const submitReply = async (comment) => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  replying.value = true
  try {
    await addArticleComment(route.params.id, {
      content: replyContent.value,
      parentId: comment.id,
      replyToUserId: comment.userId
    })
    
    cancelReply()
    ElMessage.success('回复成功')
    
    // 重新加载评论列表
    await loadComments()
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error(error.response?.data?.msg || '回复失败')
  } finally {
    replying.value = false
  }
}

const likeComment = async (comment) => {
  try {
    if (!comment.userLiked) {
      await likeArticleComment(comment.id)
      comment.likeCount = (comment.likeCount || 0) + 1
      comment.userLiked = true
    } else {
      await unlikeArticleComment(comment.id)
      comment.likeCount = Math.max(0, (comment.likeCount || 0) - 1)
      comment.userLiked = false
    }
  } catch (error) {
    console.error('点赞评论失败:', error)
    ElMessage.error(error.response?.data?.msg || '操作失败')
  }
}

const goBack = () => {
  router.push('/article')
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const formatCommentTime = (dateStr) => {
  if (!dateStr) return ''
  
  const now = new Date()
  const date = new Date(dateStr)
  const diff = now - date
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString('zh-CN')
}

const formatNumber = (num) => {
  if (!num) return 0
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num
}

const getCategoryColor = (category) => {
  const colors = {
    1: '#409eff',
    2: '#67c23a',
    3: '#e6a23c',
    4: '#f56c6c',
    5: '#909399'
  }
  return colors[category] || '#909399'
}

const getCategoryIcon = (category) => {
  const icons = {
    1: '🎓',
    2: '🎯',
    3: '🏆',
    4: '📊',
    5: '💭'
  }
  return icons[category] || '📚'
}

onMounted(() => {
  loadArticle()
})
</script>

<style scoped>
.article-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 返回按钮 */
.back-button-wrapper {
  position: fixed;
  top: 80px;
  left: 20px;
  z-index: 100;
}

.back-button {
  background: #fff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  transition: all 0.3s ease;
}

.back-button:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(0,0,0,0.25);
}

/* 文章头部 */
.article-header {
  position: relative;
  height: 400px;
  margin-bottom: 40px;
  overflow: hidden;
}

.header-background {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
}

.header-bg-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: blur(3px);
  transform: scale(1.1);
}

.header-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, rgba(0,0,0,0.3), rgba(0,0,0,0.7));
}

.header-content {
  position: relative;
  z-index: 2;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
  color: #fff;
}

.category-badge-wrapper {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  animation: fadeInDown 0.6s ease-out;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.category-badge {
  padding: 8px 20px;
  border-radius: 25px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 15px rgba(0,0,0,0.2);
}

.badge-top {
  padding: 8px 20px;
  border-radius: 25px;
  font-size: 14px;
  font-weight: 600;
  background: rgba(245, 108, 108, 0.9);
  color: #fff;
  backdrop-filter: blur(10px);
}

.article-title-main {
  font-size: 42px;
  font-weight: 800;
  color: #fff;
  margin-bottom: 30px;
  line-height: 1.3;
  text-align: center;
  text-shadow: 0 4px 20px rgba(0,0,0,0.5);
  animation: fadeIn 0.8s ease-out 0.2s both;
  max-width: 900px;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.article-meta-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 30px;
  width: 100%;
  max-width: 900px;
  padding: 20px 30px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  border-radius: 50px;
  animation: fadeInUp 0.8s ease-out 0.4s both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.meta-left,
.meta-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.95);
}

.author-item {
  background: rgba(255, 255, 255, 0.2);
  padding: 5px 15px 5px 5px;
  border-radius: 25px;
}

.author-avatar {
  background: rgba(255, 255, 255, 0.3);
}

.author-name {
  font-weight: 600;
}

.stat-badge {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 15px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}

/* 文章主体 */
.article-main {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 20px 40px;
}

/* 文章摘要盒子 */
.article-summary-box {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 25px 30px;
  border-radius: 16px;
  margin-bottom: 30px;
  display: flex;
  gap: 20px;
  align-items: flex-start;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
  animation: fadeInUp 0.6s ease-out;
}

.summary-icon {
  font-size: 32px;
  flex-shrink: 0;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.summary-text {
  color: #fff;
  font-size: 16px;
  line-height: 1.8;
  margin: 0;
  font-weight: 500;
}

/* 文章内容 */
.article-content {
  background: #fff;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  font-size: 17px;
  line-height: 1.9;
  color: #333;
  margin-bottom: 30px;
}

.article-content :deep(h2) {
  font-size: 28px;
  margin: 40px 0 20px;
  color: #1a1a1a;
  font-weight: 700;
  padding-left: 15px;
  border-left: 5px solid #409eff;
  position: relative;
}

.article-content :deep(h2::before) {
  content: '';
  position: absolute;
  left: -40px;
  top: 50%;
  transform: translateY(-50%);
  width: 30px;
  height: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  opacity: 0.2;
}

.article-content :deep(h3) {
  font-size: 22px;
  margin: 30px 0 15px;
  color: #333;
  font-weight: 600;
  padding-left: 12px;
  border-left: 3px solid #67c23a;
}

.article-content :deep(p) {
  margin-bottom: 20px;
  text-align: justify;
}

.article-content :deep(strong) {
  color: #409eff;
  font-weight: 600;
}

.article-content :deep(ul), 
.article-content :deep(ol) {
  margin: 20px 0;
  padding-left: 30px;
}

.article-content :deep(li) {
  margin-bottom: 12px;
  line-height: 1.8;
}

.article-content :deep(li::marker) {
  color: #409eff;
  font-weight: bold;
}

.article-content :deep(blockquote) {
  margin: 25px 0;
  padding: 20px 25px;
  background: #f8f9fa;
  border-left: 4px solid #409eff;
  border-radius: 8px;
  font-style: italic;
  color: #666;
}

/* 文章标签 */
.article-tags {
  margin-top: 30px;
  padding-top: 25px;
  border-top: 2px dashed #e4e7ed;
}

/* 文章操作栏 */
.article-actions-bar {
  max-width: 900px;
  margin: 0 auto 40px;
  padding: 0 20px;
}

.actions-container {
  background: #fff;
  padding: 25px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  display: flex;
  justify-content: center;
  gap: 20px;
  align-items: center;
}

.action-button {
  font-size: 16px;
  font-weight: 600;
  padding: 12px 30px;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.action-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.15);
}

.like-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
}

.like-button:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

.like-button.is-liked {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  animation: pulse 0.6s ease-out;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}

.action-text {
  margin-right: 8px;
}

.action-count {
  background: rgba(255, 255, 255, 0.3);
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 14px;
}

.share-button {
  background: #fff;
  color: #409eff;
  border-color: #409eff;
}

.share-button:hover {
  background: #409eff;
  color: #fff;
}

/* 评论区样式 */
.comment-section {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 20px 40px;
}

.comment-header {
  margin-bottom: 25px;
}

.comment-header h3 {
  font-size: 24px;
  color: #1a1a1a;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 25px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.comment-editor {
  background: #fff;
  padding: 25px;
  border-radius: 16px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  border: 2px solid #f0f0f0;
  transition: all 0.3s ease;
}

.comment-editor:hover {
  border-color: #409eff;
  box-shadow: 0 4px 20px rgba(64, 158, 255, 0.2);
}

.comment-actions {
  margin-top: 15px;
  text-align: right;
}

.comment-list {
  min-height: 200px;
  background: #fff;
  padding: 25px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

.empty-comments {
  padding: 40px 0;
}

.comment-item {
  display: flex;
  gap: 15px;
  padding: 25px;
  margin-bottom: 15px;
  background: #fafafa;
  border-radius: 12px;
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.comment-item:hover {
  background: #fff;
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
  transform: translateX(5px);
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-avatar .el-avatar {
  border: 3px solid #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.comment-main {
  flex: 1;
  min-width: 0;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.user-name {
  font-weight: 600;
  color: #303133;
}

.comment-time,
.reply-time {
  font-size: 13px;
  color: #909399;
}

.comment-content {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 10px;
  word-break: break-word;
}

.comment-footer {
  display: flex;
  gap: 20px;
}

.comment-footer .liked {
  color: #409eff;
}

/* 回复样式 */
.reply-editor {
  margin-top: 15px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 6px;
}

.reply-actions {
  margin-top: 10px;
  text-align: right;
}

.reply-list {
  margin-top: 15px;
  padding-left: 20px;
}

.reply-item {
  display: flex;
  gap: 10px;
  padding: 12px 0;
  border-top: 1px solid #f0f0f0;
}

.reply-avatar {
  flex-shrink: 0;
}

.reply-main {
  flex: 1;
  min-width: 0;
}

.reply-user {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
  font-size: 13px;
}

.reply-to {
  color: #409eff;
}

.reply-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

@media (max-width: 768px) {
  .article-header h1 {
    font-size: 24px;
  }

  .article-info {
    gap: 15px;
  }

  .article-content {
    font-size: 15px;
  }

  .article-actions {
    flex-wrap: wrap;
  }

  .comment-item {
    flex-direction: column;
  }
}
</style>
