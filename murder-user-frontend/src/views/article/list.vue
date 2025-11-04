<template>
  <div class="article-list-container">
    <div class="header">
      <h2>📰 资讯攻略</h2>
      <p class="subtitle">剧本杀玩家必读，新手入门攻略</p>
    </div>

    <!-- 分类筛选 -->
    <div class="category-filter">
      <el-radio-group v-model="queryParams.category" @change="handleCategoryChange">
        <el-radio-button :label="null">全部</el-radio-button>
        <el-radio-button :label="1">新手攻略</el-radio-button>
        <el-radio-button :label="2">选本技巧</el-radio-button>
        <el-radio-button :label="3">榜单推荐</el-radio-button>
        <el-radio-button :label="4">行业动态</el-radio-button>
        <el-radio-button :label="5">玩家心得</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 排序和搜索 -->
    <div class="filter-bar">
      <!-- 排序选项 -->
      <div class="sort-filter">
        <span class="sort-label">排序：</span>
        <el-radio-group v-model="queryParams.sortBy" @change="handleSortChange" size="small">
          <el-radio-button label="time">最新发布</el-radio-button>
          <el-radio-button label="view">最多浏览</el-radio-button>
          <el-radio-button label="like">最多点赞</el-radio-button>
          <el-radio-button label="comment">最多评论</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 搜索框 -->
      <div class="search-bar">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索文章标题或内容..."
          clearable
          @keyup.enter="loadArticles"
          style="width: 300px"
        >
          <template #append>
            <el-button :icon="Search" @click="loadArticles">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 文章列表 -->
    <div class="article-list" v-loading="loading">
      <div class="article-item" v-for="article in articleList" :key="article.id" @click="goToDetail(article.id)">
        <div class="article-cover">
          <img :src="article.coverImage" :alt="article.title" />
          <div class="article-category">{{ article.categoryName }}</div>
          <div class="top-badge" v-if="article.isTop === 1">置顶</div>
        </div>
        <div class="article-content">
          <h3>{{ article.title }}</h3>
          <p class="summary">{{ article.summary }}</p>
          <div class="article-meta">
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
              {{ article.viewCount }}
            </span>
            <span class="likes">
              <el-icon><StarFilled /></el-icon>
              {{ article.likeCount }}
            </span>
            <span class="comments" v-if="article.commentCount > 0">
              <el-icon><ChatDotRound /></el-icon>
              {{ article.commentCount }}
            </span>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="!loading && articleList.length === 0" description="暂无文章" />
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getArticleList } from '@/api/article'
import { Search, User, Calendar, View, StarFilled, ChatDotRound } from '@element-plus/icons-vue'

const router = useRouter()

const loading = ref(false)
const articleList = ref([])
const total = ref(0)

const queryParams = ref({
  page: 1,
  pageSize: 10,
  category: null,
  keyword: '',
  sortBy: 'time' // 默认按最新发布排序
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

const handleCategoryChange = () => {
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
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadArticles()
})
</script>

<style scoped>
.article-list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.header h2 {
  font-size: 32px;
  color: #303133;
  margin-bottom: 10px;
}

.subtitle {
  color: #909399;
  font-size: 14px;
}

.category-filter {
  margin-bottom: 20px;
  text-align: center;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  gap: 20px;
}

.sort-filter {
  display: flex;
  align-items: center;
  gap: 10px;
}

.sort-label {
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}

.search-bar {
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-bar {
    width: 100%;
  }

  .search-bar .el-input {
    width: 100% !important;
  }
}

.article-list {
  min-height: 400px;
}

.article-item {
  display: flex;
  gap: 20px;
  background: #fff;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s;
}

.article-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.article-cover {
  width: 280px;
  height: 180px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  position: relative;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.article-category {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(64, 158, 255, 0.9);
  color: #fff;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
}

.top-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(245, 108, 108, 0.9);
  color: #fff;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
}

.article-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.article-content h3 {
  font-size: 20px;
  color: #303133;
  margin-bottom: 10px;
  line-height: 1.4;
}

.summary {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: auto;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  gap: 20px;
  color: #909399;
  font-size: 13px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.article-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .article-item {
    flex-direction: column;
  }

  .article-cover {
    width: 100%;
    height: 200px;
  }
}
</style>
