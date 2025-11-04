<template>
  <div class="article-detail-container" v-loading="loading">
    <!-- 文章头部 -->
    <div class="article-header" v-if="article">
      <div class="category-badge">{{ article.categoryName }}</div>
      <h1>{{ article.title }}</h1>
      <div class="article-info">
        <span class="author">
          <el-icon><User /></el-icon>
          {{ article.authorName || '匿名' }}
        </span>
        <span class="date">
          <el-icon><Calendar /></el-icon>
          {{ formatDate(article.publishTime) }}
        </span>
        <span class="views">
          <el-icon><View /></el-icon>
          {{ article.viewCount }} 浏览
        </span>
        <span class="likes">
          <el-icon><StarFilled /></el-icon>
          {{ article.likeCount }} 点赞
        </span>
        <span class="comments">
          <el-icon><ChatDotRound /></el-icon>
          {{ article.commentCount || 0 }} 评论
        </span>
      </div>
    </div>

    <!-- 文章封面 -->
    <div class="article-cover" v-if="article && article.coverImage">
      <img :src="article.coverImage" :alt="article.title" />
    </div>

    <!-- 文章内容 -->
    <div class="article-content" v-if="article">
      <div v-html="article.content"></div>
    </div>

    <!-- 文章操作栏 -->
    <div class="article-actions" v-if="article">
      <el-button 
        type="primary" 
        :icon="liked ? StarFilled : Star" 
        @click="handleLike" 
        :loading="liking"
        :class="{ 'is-liked': liked }"
      >
        {{ liked ? '已点赞' : '点赞' }} ({{ article.likeCount }})
      </el-button>
      <el-button 
        type="warning" 
        :icon="favorited ? Star : StarFilled" 
        @click="handleFavorite" 
        :loading="favoriting"
        :class="{ 'is-favorited': favorited }"
      >
        {{ favorited ? '已收藏' : '收藏' }}
      </el-button>
      <el-button :icon="Share" @click="handleShare">分享</el-button>
      <el-button @click="goBack">返回列表</el-button>
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
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getArticleDetail, likeArticle } from '@/api/article'
import { ElMessage } from 'element-plus'
import { 
  User, Calendar, View, StarFilled, Star, Share, 
  ChatDotRound, ChatLineRound 
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const liking = ref(false)
const favoriting = ref(false)
const commenting = ref(false)
const replying = ref(false)
const loadingComments = ref(false)

const article = ref(null)
const liked = ref(false)
const favorited = ref(false)

const commentContent = ref('')
const replyContent = ref('')
const replyingTo = ref(null)
const comments = ref([])

// 模拟评论数据（后续需要从API获取）
const mockComments = ref([
  {
    id: 1,
    userName: '剧本杀爱好者',
    userAvatar: null,
    content: '这篇文章写得太详细了！作为新手，终于知道剧本杀的完整流程了，感谢分享！👍',
    likeCount: 15,
    userLiked: false,
    createTime: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
    replies: []
  },
  {
    id: 2,
    userName: '推理达人',
    userAvatar: null,
    content: '流程讲解很清晰，特别是游戏阶段的详解部分，对新手非常友好。建议收藏！',
    likeCount: 12,
    userLiked: false,
    createTime: new Date(Date.now() - 5 * 60 * 60 * 1000).toISOString(),
    replies: []
  },
  {
    id: 3,
    userName: '小白玩家',
    userAvatar: null,
    content: '看完这篇文章，我终于敢去玩剧本杀了！之前一直担心不知道怎么玩会很尴尬。',
    likeCount: 8,
    userLiked: false,
    createTime: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString(),
    replies: [
      {
        id: 101,
        userName: '老玩家',
        userAvatar: null,
        content: '加油！剧本杀很有趣的，不用担心~',
        replyToUserName: '小白玩家',
        createTime: new Date(Date.now() - 20 * 60 * 60 * 1000).toISOString()
      }
    ]
  }
])

const loadArticle = async () => {
  loading.value = true
  try {
    const res = await getArticleDetail(route.params.id)
    if (res.code === 1 || res.code === 200) {
      article.value = res.data
      // 模拟加载评论
      loadComments()
    }
  } catch (error) {
    console.error('加载文章详情失败:', error)
    ElMessage.error('加载文章失败')
  } finally {
    loading.value = false
  }
}

const loadComments = () => {
  // 根据文章ID加载不同的评论
  const articleId = parseInt(route.params.id)
  if (articleId === 6) {
    comments.value = mockComments.value
  } else {
    comments.value = []
  }
}

const handleLike = async () => {
  liking.value = true
  try {
    await likeArticle(route.params.id)
    if (!liked.value) {
      article.value.likeCount++
      liked.value = true
      ElMessage.success('点赞成功')
    } else {
      article.value.likeCount--
      liked.value = false
      ElMessage.success('取消点赞')
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('操作失败')
  } finally {
    liking.value = false
  }
}

const handleFavorite = async () => {
  favoriting.value = true
  try {
    // TODO: 调用收藏API
    await new Promise(resolve => setTimeout(resolve, 500))
    if (!favorited.value) {
      favorited.value = true
      ElMessage.success('收藏成功')
    } else {
      favorited.value = false
      ElMessage.success('取消收藏')
    }
  } catch (error) {
    console.error('收藏失败:', error)
    ElMessage.error('操作失败')
  } finally {
    favoriting.value = false
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
    // TODO: 调用评论API
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const newComment = {
      id: Date.now(),
      userName: '当前用户',
      userAvatar: null,
      content: commentContent.value,
      likeCount: 0,
      userLiked: false,
      createTime: new Date().toISOString(),
      replies: []
    }
    
    comments.value.unshift(newComment)
    article.value.commentCount = (article.value.commentCount || 0) + 1
    commentContent.value = ''
    ElMessage.success('评论发表成功')
  } catch (error) {
    console.error('发表评论失败:', error)
    ElMessage.error('发表失败')
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
    // TODO: 调用回复API
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const newReply = {
      id: Date.now(),
      userName: '当前用户',
      userAvatar: null,
      content: replyContent.value,
      replyToUserName: comment.userName,
      createTime: new Date().toISOString()
    }
    
    if (!comment.replies) {
      comment.replies = []
    }
    comment.replies.push(newReply)
    
    cancelReply()
    ElMessage.success('回复成功')
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error('回复失败')
  } finally {
    replying.value = false
  }
}

const likeComment = async (comment) => {
  try {
    // TODO: 调用评论点赞API
    await new Promise(resolve => setTimeout(resolve, 300))
    
    if (!comment.userLiked) {
      comment.likeCount = (comment.likeCount || 0) + 1
      comment.userLiked = true
    } else {
      comment.likeCount = Math.max(0, (comment.likeCount || 0) - 1)
      comment.userLiked = false
    }
  } catch (error) {
    console.error('点赞评论失败:', error)
    ElMessage.error('操作失败')
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

onMounted(() => {
  loadArticle()
})
</script>

<style scoped>
.article-detail-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  background: #fff;
  min-height: 80vh;
}

.article-header {
  text-align: center;
  padding: 30px 0;
  border-bottom: 1px solid #ebeef5;
}

.category-badge {
  display: inline-block;
  background: #409eff;
  color: #fff;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  margin-bottom: 20px;
}

.article-header h1 {
  font-size: 32px;
  color: #303133;
  margin-bottom: 20px;
  line-height: 1.4;
}

.article-info {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 30px;
  color: #909399;
  font-size: 14px;
}

.article-info span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.article-cover {
  margin: 30px 0;
  border-radius: 8px;
  overflow: hidden;
}

.article-cover img {
  width: 100%;
  height: auto;
  display: block;
}

.article-content {
  padding: 30px 0;
  font-size: 16px;
  line-height: 1.8;
  color: #303133;
}

.article-content :deep(h2) {
  font-size: 24px;
  margin: 30px 0 15px;
  color: #303133;
}

.article-content :deep(h3) {
  font-size: 20px;
  margin: 25px 0 12px;
  color: #606266;
}

.article-content :deep(p) {
  margin-bottom: 15px;
}

.article-content :deep(ul), 
.article-content :deep(ol) {
  margin: 15px 0;
  padding-left: 30px;
}

.article-content :deep(li) {
  margin-bottom: 8px;
}

.article-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  padding: 30px 0;
  border-top: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
}

.article-actions .is-liked {
  background: #409eff;
  color: #fff;
}

.article-actions .is-favorited {
  background: #f56c6c;
  color: #fff;
}

/* 评论区样式 */
.comment-section {
  margin-top: 40px;
}

.comment-header {
  margin-bottom: 20px;
}

.comment-header h3 {
  font-size: 20px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.comment-editor {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
}

.comment-actions {
  margin-top: 10px;
  text-align: right;
}

.comment-list {
  min-height: 200px;
}

.empty-comments {
  padding: 40px 0;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
}

.comment-avatar {
  flex-shrink: 0;
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
