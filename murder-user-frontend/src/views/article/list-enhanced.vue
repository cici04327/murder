<template>
  <div class="article-list-container">
    <!-- 头部横幅 -->
    <div class="header-banner">
      <div class="banner-content">
        <h2>📰 资讯攻略中心</h2>
        <p class="subtitle">剧本杀玩家必读 · 新手入门攻略 · 行业最新动态</p>
        <div class="stats">
          <span><strong>{{ total }}</strong> 篇文章</span>
          <span><strong>10000+</strong> 次阅读</span>
          <span><strong>500+</strong> 条评论</span>
        </div>
      </div>
    </div>

    <!-- 快速导航 -->
    <div class="quick-nav">
      <el-button 
        v-for="(cat, index) in categories" 
        :key="index"
        :type="queryParams.category === cat.value ? 'primary' : ''"
        @click="selectCategory(cat.value)"
        size="large"
      >
        <span class="nav-icon">{{ cat.icon }}</span>
        {{ cat.label }}
      </el-button>
    </div>

    <!-- 搜索和排序 -->
    <div class="filter-bar">
      <el-input
        v-model="queryParams.keyword"
        placeholder="搜索文章标题或内容..."
        clearable
        @keyup.enter="loadArticles"
        class="search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
        <template #append>
          <el-button :icon="Search" @click="loadArticles">搜索</el-button>
        </template>
      </el-input>
      
      <el-select v-model="queryParams.sortBy" placeholder="排序" @change="handleSortChange" class="sort-select">
        <el-option label="最新发布" value="time" />
        <el-option label="最多浏览" value="view" />
        <el-option label="最多点赞" value="like" />
        <el-option label="最多评论" value="comment" />
      </el-select>
    </div>

    <!-- 文章列表 -->
    <div class="article-list" v-loading="loading">
      <!-- 推荐文章（置顶） -->
      <div v-if="topArticles.length > 0 && !queryParams.keyword" class="top-section">
        <h3 class="section-title">
          <el-icon><Star /></el-icon>
          置顶推荐
        </h3>
        <div 
          class="article-card top-article" 
          v-for="article in topArticles" 
          :key="'top-' + article.id"
          @click="goToDetail(article.id)"
        >
          <div class="article-cover">
            <img :src="article.coverImage" :alt="article.title" />
            <div class="article-badges">
              <span class="badge badge-top">置顶</span>
              <span class="badge badge-category" :style="{ background: getCategoryColor(article.category) }">
                {{ article.categoryName }}
              </span>
            </div>
          </div>
          <div class="article-info-wrapper">
            <h3 class="article-title">{{ article.title }}</h3>
            <p class="article-summary">{{ article.summary }}</p>
            <div class="article-meta">
              <span class="meta-item">
                <el-icon><User /></el-icon>
                {{ article.authorName || '匿名' }}
              </span>
              <span class="meta-item">
                <el-icon><Calendar /></el-icon>
                {{ formatDate(article.publishTime) }}
              </span>
              <div class="meta-stats">
                <span class="stat-item">
                  <el-icon><View /></el-icon>
                  {{ formatNumber(article.viewCount) }}
                </span>
                <span class="stat-item">
                  <el-icon><StarFilled /></el-icon>
                  {{ formatNumber(article.likeCount) }}
                </span>
                <span class="stat-item" v-if="article.commentCount > 0">
                  <el-icon><ChatDotRound /></el-icon>
                  {{ formatNumber(article.commentCount) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 普通文章 -->
      <div class="normal-section">
        <h3 class="section-title" v-if="topArticles.length > 0 && !queryParams.keyword">
          <el-icon><Document /></el-icon>
          全部文章
        </h3>
        
        <div class="articles-grid">
          <div 
            class="article-card" 
            v-for="article in normalArticles" 
            :key="article.id"
            @click="goToDetail(article.id)"
          >
            <div class="article-cover">
              <img :src="article.coverImage" :alt="article.title" />
              <div class="article-badges">
                <span class="badge badge-category" :style="{ background: getCategoryColor(article.category) }">
                  {{ article.categoryName }}
                </span>
                <span class="badge badge-hot" v-if="article.viewCount > 2000">
                  🔥 热门
                </span>
              </div>
            </div>
            <div class="article-info-wrapper">
              <h3 class="article-title">{{ article.title }}</h3>
              <p class="article-summary">{{ article.summary }}</p>
              <div class="article-meta">
                <span class="meta-item">
                  <el-icon><User /></el-icon>
                  {{ article.authorName || '匿名' }}
                </span>
                <span class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  {{ formatDate(article.publishTime) }}
                </span>
                <div class="meta-stats">
                  <span class="stat-item">
                    <el-icon><View /></el-icon>
                    {{ formatNumber(article.viewCount) }}
                  </span>
                  <span class="stat-item">
                    <el-icon><StarFilled /></el-icon>
                    {{ formatNumber(article.likeCount) }}
                  </span>
                  <span class="stat-item" v-if="article.commentCount > 0">
                    <el-icon><ChatDotRound /></el-icon>
                    {{ formatNumber(article.commentCount) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty v-if="!loading && articleList.length === 0" description="暂无文章" />
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[9, 18, 27, 36]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getArticleList } from '@/api/article'
import { 
  Search, User, Calendar, View, StarFilled, ChatDotRound, 
  Star, Document 
} from '@element-plus/icons-vue'

const router = useRouter()

const loading = ref(false)
const articleList = ref([])
const total = ref(0)

const categories = [
  { label: '全部', value: null, icon: '📚' },
  { label: '新手攻略', value: 1, icon: '🎓' },
  { label: '选本技巧', value: 2, icon: '🎯' },
  { label: '榜单推荐', value: 3, icon: '🏆' },
  { label: '行业动态', value: 4, icon: '📊' },
  { label: '玩家心得', value: 5, icon: '💭' }
]

const queryParams = ref({
  page: 1,
  pageSize: 9,
  category: null,
  keyword: '',
  sortBy: 'time' // 默认按最新发布排序
})

// 分离置顶和普通文章
const topArticles = computed(() => {
  return articleList.value.filter(article => article.isTop === 1)
})

const normalArticles = computed(() => {
  return articleList.value.filter(article => article.isTop !== 1)
})

const loadArticles = async () => {
  loading.value = true
  try {
    console.log('加载文章，参数:', queryParams.value)
    const res = await getArticleList(queryParams.value)
    if (res.code === 1 || res.code === 200) {
      articleList.value = res.data.records
      total.value = res.data.total
      console.log('文章加载成功，总数:', total.value, '当前页:', queryParams.value.page)
    }
  } catch (error) {
    console.error('加载文章失败:', error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (newPage) => {
  console.log('页码变化:', newPage)
  queryParams.value.page = newPage
  loadArticles()
}

const handleSizeChange = (newSize) => {
  console.log('每页大小变化:', newSize)
  queryParams.value.pageSize = newSize
  queryParams.value.page = 1
  loadArticles()
}

const selectCategory = (category) => {
  queryParams.value.category = category
  queryParams.value.page = 1
  loadArticles()
}

const handleSortChange = () => {
  queryParams.value.page = 1
  loadArticles()
}

const goToDetail = (id) => {
  router.push(`/article/${id}`)
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (24 * 60 * 60 * 1000))
  
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' })
}

const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num
}

const getCategoryColor = (category) => {
  const colors = {
    1: '#409eff', // 新手攻略 - 蓝色
    2: '#67c23a', // 选本技巧 - 绿色
    3: '#e6a23c', // 榜单推荐 - 橙色
    4: '#f56c6c', // 行业动态 - 红色
    5: '#909399'  // 玩家心得 - 灰色
  }
  return colors[category] || '#909399'
}

onMounted(() => {
  loadArticles()
})
</script>

<style scoped>
.article-list-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px 40px;
}

/* 头部横幅 */
.header-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 60px 40px;
  border-radius: 16px;
  margin: 20px 0 30px 0;
  text-align: center;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
}

.header-banner::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  animation: pulse 15s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.3; }
  50% { transform: scale(1.1); opacity: 0.5; }
}

.banner-content {
  position: relative;
  z-index: 1;
}

.banner-content h2 {
  font-size: 36px;
  margin-bottom: 15px;
  font-weight: 700;
  text-shadow: 0 2px 10px rgba(0,0,0,0.2);
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

.subtitle {
  font-size: 16px;
  opacity: 0.95;
  margin-bottom: 30px;
  animation: fadeIn 0.8s ease-out 0.2s both;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.stats {
  display: flex;
  justify-content: center;
  gap: 50px;
  font-size: 14px;
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

.stats span {
  background: rgba(255, 255, 255, 0.15);
  padding: 15px 25px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.stats span:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-3px);
}

.stats span strong {
  font-size: 28px;
  display: block;
  margin-bottom: 5px;
  font-weight: 800;
}

/* 快速导航 */
.quick-nav {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 30px;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}

.quick-nav .el-button {
  border-radius: 20px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.quick-nav .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.nav-icon {
  margin-right: 6px;
  font-size: 18px;
}

/* 搜索和排序 */
.filter-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
}

.search-input {
  flex: 1;
  max-width: 600px;
}

.sort-select {
  width: 150px;
}

/* 文章卡片 */
.section-title {
  font-size: 20px;
  color: #303133;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.top-section {
  margin-bottom: 40px;
}

.article-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 20px;
  border: 2px solid transparent;
}

.article-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.18);
  border-color: #409eff;
}

.article-card:hover .article-title {
  color: #409eff;
}

.top-article {
  display: flex;
  gap: 20px;
}

.top-article .article-cover {
  width: 400px;
  height: 250px;
  flex-shrink: 0;
}

.article-cover {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f5f7fa;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.article-card:hover .article-cover img {
  transform: scale(1.05);
}

.article-badges {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  color: #fff;
  font-weight: 500;
}

.badge-top {
  background: #f56c6c;
}

.badge-category {
  background: #409eff;
}

.badge-hot {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
}

.article-info-wrapper {
  padding: 20px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.article-title {
  font-size: 18px;
  color: #303133;
  margin-bottom: 12px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-weight: 600;
}

.article-summary {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
  font-size: 13px;
  color: #909399;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-stats {
  display: flex;
  gap: 15px;
  margin-left: auto;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 网格布局 */
.articles-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

/* 分页 */
.pagination-wrapper {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}

/* 响应式 */
@media (max-width: 1200px) {
  .articles-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .header-banner {
    padding: 40px 20px;
  }

  .banner-content h2 {
    font-size: 28px;
  }

  .stats {
    flex-direction: column;
    gap: 15px;
  }

  .quick-nav {
    justify-content: flex-start;
    overflow-x: auto;
    flex-wrap: nowrap;
  }

  .filter-bar {
    flex-direction: column;
  }

  .search-input,
  .sort-select {
    max-width: 100%;
    width: 100%;
  }

  .articles-grid {
    grid-template-columns: 1fr;
  }

  .top-article {
    flex-direction: column;
  }

  .top-article .article-cover {
    width: 100%;
    height: 200px;
  }
}
</style>
