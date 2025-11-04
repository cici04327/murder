<template>
  <div class="script-list-container">
    <el-card class="search-card">
      <!-- 当前筛选条件显示 -->
      <div v-if="hasActiveFilters" class="active-filters">
        <span class="filter-label">当前筛选：</span>
        <el-tag v-if="searchForm.keyword" closable @close="searchForm.keyword = ''; handleSearch()">
          关键词: {{ searchForm.keyword }}
        </el-tag>
        <el-tag v-if="searchForm.categoryId" closable @close="searchForm.categoryId = null; handleSearch()">
          分类: {{ getCategoryName(searchForm.categoryId) }}
        </el-tag>
        <el-tag v-if="searchForm.difficulty" closable @close="searchForm.difficulty = null; handleSearch()">
          难度: {{ difficultyMap[searchForm.difficulty] }}
        </el-tag>
        <el-tag v-if="searchForm.playerCount" closable @close="searchForm.playerCount = null; handleSearch()">
          人数: {{ searchForm.playerCount }}人
        </el-tag>
        <el-tag v-if="searchForm.sortBy && searchForm.sortBy !== 'hot'" type="info">
          排序: {{ sortByMap[searchForm.sortBy] }}
        </el-tag>
      </div>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索剧本名称"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        
        <el-form-item label="分类">
          <el-select
            v-model="searchForm.categoryId"
            placeholder="选择分类"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="难度">
          <el-select
            v-model="searchForm.difficulty"
            placeholder="选择难度"
            clearable
            @change="handleSearch"
          >
            <el-option label="简单" :value="1" />
            <el-option label="普通" :value="2" />
            <el-option label="困难" :value="3" />
            <el-option label="硬核" :value="4" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="人数">
          <el-select
            v-model="searchForm.playerCount"
            placeholder="选择人数"
            clearable
            @change="handleSearch"
          >
            <el-option label="4人" :value="4" />
            <el-option label="5人" :value="5" />
            <el-option label="6人" :value="6" />
            <el-option label="7人" :value="7" />
            <el-option label="8人+" :value="8" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="list-header">
      <span class="total">共 {{ total }} 个剧本</span>
      <el-radio-group v-model="searchForm.sortBy" @change="handleSearch">
        <el-radio-button label="hot">最热门</el-radio-button>
        <el-radio-button label="rating">评分最高</el-radio-button>
        <el-radio-button label="newest">最新</el-radio-button>
        <el-radio-button label="price">价格</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 骨架屏 -->
    <SkeletonScriptList v-if="loading" :count="searchForm.pageSize" />
    
    <!-- 真实内容 -->
    <el-row :gutter="20" v-else>
      <el-col
        :xs="24"
        :sm="12"
        :md="8"
        :lg="6"
        v-for="script in scripts"
        :key="script.id"
      >
        <div class="script-card" @click="goToDetail(script.id)">
          <div class="script-image">
            <LazyImage
              :src="script.coverImage || getScriptCover(script.name, script.categoryName)"
              :alt="script.name"
              :height="200"
              @error="handleImageError"
            />
            <div class="script-tag">{{ script.categoryName }}</div>
          </div>
          <div class="script-info">
            <h4>{{ script.name }}</h4>
            <div class="script-desc">{{ script.description }}</div>
            <div class="script-meta">
              <el-tag size="small">{{ difficultyMap[script.difficulty] || script.difficulty }}</el-tag>
              <el-tag size="small" type="info">{{ script.playerCount }}人</el-tag>
              <el-tag size="small" type="warning">{{ script.duration }}小时</el-tag>
            </div>
            <div class="script-rating">
              <el-rate v-model="script.rating" disabled show-score size="small" />
            </div>
            <div class="script-footer">
              <span class="script-price">¥{{ script.price }}/人</span>
              <el-button type="primary" size="small" @click.stop="handleReserve(script)">
                立即预约
              </el-button>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && scripts.length === 0" description="暂无剧本" />

    <el-pagination
      v-if="total > 0"
      class="pagination"
      v-model:current-page="searchForm.page"
      v-model:page-size="searchForm.pageSize"
      :total="total"
      :page-sizes="[12, 24, 48]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getScriptList, getScriptCategories } from '@/api/script'
import { PLACEHOLDERS } from '@/assets/placeholders'
import { getScriptCover } from '@/assets/script-covers'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import SkeletonScriptList from '@/components/Skeleton/SkeletonScriptList.vue'
import LazyImage from '@/components/LazyImage.vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const scripts = ref([])
const categories = ref([])
const total = ref(0)

const searchForm = reactive({
  keyword: '',
  categoryId: null,
  difficulty: null,
  playerCount: null,
  sortBy: 'hot',
  page: 1,
  pageSize: 12
})

// 难度映射
const difficultyMap = {
  1: '简单',
  2: '普通',
  3: '困难',
  4: '硬核'
}

// 排序映射
const sortByMap = {
  'hot': '最热门',
  'rating': '评分最高',
  'newest': '最新',
  'price': '价格'
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

const loadScripts = async () => {
  loading.value = true
  try {
    console.log('加载剧本列表，参数:', searchForm)
    const res = await getScriptList(searchForm)
    console.log('API返回数据结构:', res)
    
    if (res.data) {
      // 判断是否是分页对象
      if (res.data.records || res.data.list || res.data.total !== undefined) {
        // 后端已分页（PageResult格式）
        scripts.value = res.data.records || res.data.list || []
        total.value = res.data.total || 0
        
        console.log(`✅ 后端分页: 总数${total.value}, 当前页显示${scripts.value.length}条 (第${searchForm.page}页, 每页${searchForm.pageSize}条)`)
      } else if (Array.isArray(res.data)) {
        // 后端返回完整数组（未分页），前端手动分页
        const allScripts = res.data
        total.value = allScripts.length
        
        // 前端分页：计算当前页的起始和结束索引
        const start = (searchForm.page - 1) * searchForm.pageSize
        const end = start + searchForm.pageSize
        scripts.value = allScripts.slice(start, end)
        
        console.log(`✅ 前端分页: 总数${total.value}, 当前页显示${scripts.value.length}条 (第${searchForm.page}页, 每页${searchForm.pageSize}条)`)
      } else {
        console.error('❌ 未知的数据格式:', res.data)
        scripts.value = []
        total.value = 0
      }
    }
  } catch (error) {
    console.error('加载剧本列表失败:', error)
    // 使用模拟数据（48条，用于测试分页）
    const mockData = Array.from({ length: 48 }, (_, i) => ({
      id: i + 1,
      name: `剧本${i + 1}`,
      description: '这是一个精彩的剧本，充满悬念和惊喜...',
      categoryName: ['本格推理', '情感沉浸', '科幻机制', '恐怖惊悚'][i % 4],
      difficulty: (i % 4) + 1,
      playerCount: 4 + (i % 5),
      duration: 3 + (i % 3),
      price: 68 + (i * 10),
      rating: 4 + Math.random(),
      coverImage: ''
    }))
    
    // 前端分页模拟数据
    total.value = mockData.length
    const start = (searchForm.page - 1) * searchForm.pageSize
    const end = start + searchForm.pageSize
    scripts.value = mockData.slice(start, end)
    
    console.log(`模拟数据分页: 总数${total.value}, 当前显示${scripts.value.length}条`)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (newPage) => {
  console.log('剧本列表页码变化:', newPage)
  searchForm.page = newPage
  // 滚动到顶部，提升用户体验
  window.scrollTo({ top: 0, behavior: 'smooth' })
  loadScripts()
}

const handleSizeChange = (newSize) => {
  console.log('剧本列表每页大小变化:', newSize)
  searchForm.pageSize = newSize
  searchForm.page = 1
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
  loadScripts()
}

const handleSearch = () => {
  searchForm.page = 1
  loadScripts()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.categoryId = null
  searchForm.difficulty = null
  searchForm.playerCount = null
  searchForm.sortBy = 'hot'
  handleSearch()
}

const goToDetail = (id) => {
  // 保存浏览历史
  const script = scripts.value.find(s => s.id === id)
  if (script) {
    saveBrowseHistory(script)
  }
  router.push(`/script/${id}`)
}

// 图片加载失败处理
const handleImageError = (e) => {
  e.target.src = '/default-script.jpg'
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
    
    // 处理 difficulty：如果是字符串，转换为数字
    let difficultyValue = scriptData.difficulty || 2
    if (typeof difficultyValue === 'string') {
      const diffMap = { '简单': 1, '中等': 2, '普通': 2, '困难': 3, '硬核': 4 }
      difficultyValue = diffMap[difficultyValue] || 2
    }
    
    const historyItem = {
      id: scriptData.id,
      name: scriptData.name,
      cover: scriptData.coverImage || scriptData.cover || '/default-script.jpg',
      categoryName: scriptData.categoryName || '未分类',
      difficulty: difficultyValue,
      playerCount: scriptData.playerCount || 6,
      rating: typeof scriptData.rating === 'number' ? scriptData.rating : 4.5,
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

const handleReserve = (script) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push({
    path: '/reservation/create',
    query: { scriptId: script.id }
  })
}

// 判断是否有激活的筛选条件
const hasActiveFilters = computed(() => {
  return searchForm.keyword || 
         searchForm.categoryId || 
         searchForm.difficulty || 
         searchForm.playerCount ||
         (searchForm.sortBy && searchForm.sortBy !== 'hot')
})

// 获取分类名称
const getCategoryName = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : ''
}

onMounted(() => {
  loadCategories()
  loadScripts()
})
</script>

<style scoped>
.script-list-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin: 0;
}

.list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding: 15px;
  background: #fff;
  border-radius: 8px;
}

.total {
  font-size: 14px;
  color: #666;
}

.script-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.script-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.script-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

/* 列表项动画 */
.list-enter-active {
  transition: all 0.5s ease;
  transition-delay: calc(var(--index) * 0.05s);
}

.list-leave-active {
  transition: all 0.3s ease;
}

.list-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.list-leave-to {
  opacity: 0;
  transform: translateY(-30px);
}

.list-move {
  transition: transform 0.5s ease;
}

.script-image {
  position: relative;
  height: 300px;
  overflow: hidden;
}

.script-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.script-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 12px;
}

.script-info {
  padding: 15px;
}

.script-info h4 {
  margin: 0 0 10px;
  font-size: 16px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.script-desc {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.script-meta {
  margin-bottom: 10px;
}

.script-meta .el-tag {
  margin-right: 5px;
}

.script-rating {
  margin-bottom: 10px;
}

.script-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.script-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.active-filters {
  margin-bottom: 15px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  margin-right: 5px;
}

.active-filters .el-tag {
  margin: 0;
}
</style>
