<template>
  <div class="favorites-enhanced">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <h2>我的收藏</h2>
        <el-tag type="info" size="large">共 {{ total }} 个剧本</el-tag>
      </div>
      
      <div class="toolbar-right">
        <!-- 视图切换 -->
        <el-radio-group v-model="viewMode" size="default">
          <el-radio-button label="card">
            <el-icon><Grid /></el-icon>
            卡片
          </el-radio-button>
          <el-radio-button label="list">
            <el-icon><List /></el-icon>
            列表
          </el-radio-button>
        </el-radio-group>

        <!-- 刷新按钮 -->
        <el-button :icon="Refresh" @click="handleRefresh" :loading="loading">
          刷新
        </el-button>

        <!-- 筛选按钮 -->
        <el-button :icon="Filter" @click="showFilterPanel = !showFilterPanel">
          筛选 {{ activeFiltersCount > 0 ? `(${activeFiltersCount})` : '' }}
        </el-button>
      </div>
    </div>

    <!-- 筛选面板 -->
    <el-collapse-transition>
      <div class="filter-panel" v-show="showFilterPanel">
        <el-card shadow="never">
          <el-form :model="filters" label-width="80px" size="default">
            <el-row :gutter="20">
              <!-- 分类筛选 -->
              <el-col :span="8">
                <el-form-item label="剧本分类">
                  <el-select v-model="filters.category" placeholder="全部分类" clearable>
                    <el-option label="全部" value="" />
                    <el-option 
                      v-for="cat in categories" 
                      :key="cat.id" 
                      :label="cat.name" 
                      :value="cat.id" 
                    />
                  </el-select>
                </el-form-item>
              </el-col>

              <!-- 人数筛选 -->
              <el-col :span="8">
                <el-form-item label="适合人数">
                  <el-select v-model="filters.playerCount" placeholder="全部人数" clearable>
                    <el-option label="全部" value="" />
                    <el-option label="4-6人" value="4-6" />
                    <el-option label="6-8人" value="6-8" />
                    <el-option label="8-10人" value="8-10" />
                    <el-option label="10人以上" value="10+" />
                  </el-select>
                </el-form-item>
              </el-col>

              <!-- 难度筛选 -->
              <el-col :span="8">
                <el-form-item label="难度">
                  <el-select v-model="filters.difficulty" placeholder="全部难度" clearable>
                    <el-option label="全部" value="" />
                    <el-option label="简单" value="简单" />
                    <el-option label="中等" value="中等" />
                    <el-option label="困难" value="困难" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <!-- 价格区间 -->
              <el-col :span="8">
                <el-form-item label="价格区间">
                  <el-slider 
                    v-model="filters.priceRange" 
                    range 
                    :min="0" 
                    :max="500" 
                    :step="10"
                    :marks="{ 0: '¥0', 250: '¥250', 500: '¥500' }"
                  />
                </el-form-item>
              </el-col>

              <!-- 评分筛选 -->
              <el-col :span="8">
                <el-form-item label="最低评分">
                  <el-rate v-model="filters.minRating" show-text />
                </el-form-item>
              </el-col>

              <!-- 收藏时间 -->
              <el-col :span="8">
                <el-form-item label="收藏时间">
                  <el-date-picker
                    v-model="filters.dateRange"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 操作按钮 -->
            <el-form-item>
              <el-button type="primary" @click="applyFilters">应用筛选</el-button>
              <el-button @click="resetFilters">重置</el-button>
              <el-button type="success" @click="saveFilters" text>
                <el-icon><Star /></el-icon>
                保存筛选条件
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </el-collapse-transition>

    <!-- 快速筛选标签 -->
    <div class="quick-filters" v-if="activeFiltersCount > 0">
      <el-tag 
        v-for="(tag, index) in activeFilterTags" 
        :key="index"
        closable
        @close="removeFilter(tag.key)"
        type="info"
        size="large"
      >
        {{ tag.label }}: {{ tag.value }}
      </el-tag>
      <el-button size="small" text @click="resetFilters">清空全部</el-button>
    </div>

    <!-- 排序栏 -->
    <div class="sort-bar">
      <span class="sort-label">排序：</span>
      <el-radio-group v-model="sortBy" size="default" @change="loadFavorites">
        <el-radio-button label="latest">最新收藏</el-radio-button>
        <el-radio-button label="rating">评分最高</el-radio-button>
        <el-radio-button label="price-asc">价格由低到高</el-radio-button>
        <el-radio-button label="price-desc">价格由高到低</el-radio-button>
        <el-radio-button label="popular">最受欢迎</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 内容区域 -->
    <div v-loading="loading" class="content-area">
      <!-- 卡片视图 -->
      <div v-if="viewMode === 'card'" class="card-view">
        <el-row :gutter="20">
          <el-col 
            :xs="24" :sm="12" :md="8" :lg="6" 
            v-for="script in scriptList" 
            :key="script.id"
          >
            <el-card class="script-card" shadow="hover">
              <div class="card-cover" @click="goToDetail(script.id)">
                <img :src="script.cover || '/default-script.jpg'" :alt="script.name" />
                <div class="card-overlay">
                  <el-button type="primary" circle size="large">
                    <el-icon><View /></el-icon>
                  </el-button>
                </div>
                
                <!-- 标签角标 -->
                <div class="card-badges">
                  <el-tag v-if="script.isHot" type="danger" size="small">热门</el-tag>
                  <el-tag v-if="script.isNew" type="success" size="small">新品</el-tag>
                </div>
              </div>
              
              <div class="card-content">
                <h3 class="card-title" @click="goToDetail(script.id)">{{ script.name }}</h3>
                
                <div class="card-tags">
                  <el-tag size="small" type="success">{{ script.categoryName || '未分类' }}</el-tag>
                  <el-tag size="small">{{ script.playerCount }}人</el-tag>
                  <el-tag size="small">{{ script.duration }}小时</el-tag>
                </div>
                
                <div class="card-rating">
                  <el-rate v-model="script.rating" disabled size="small" show-score text-color="#ff9900" />
                  <span class="review-count">({{ script.reviewCount || 0 }})</span>
                </div>
                
                <div class="card-footer">
                  <div class="card-price">
                    <span class="price">¥{{ script.price }}</span>
                    <span class="unit">/人</span>
                  </div>
                  <div class="card-actions">
                    <el-button type="primary" size="small" @click="handleReserve(script.id)" plain>
                      预约
                    </el-button>
                    <el-button size="small" type="danger" @click="handleUnfavorite(script.id)" plain>
                      取消
                    </el-button>
                  </div>
                </div>

                <div class="card-meta">
                  <span class="favorite-time">
                    <el-icon><Clock /></el-icon>
                    收藏于 {{ formatDate(script.favoriteTime) }}
                  </span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 列表视图 -->
      <div v-else class="list-view">
        <el-table :data="scriptList" style="width: 100%" stripe>
          <el-table-column width="120">
            <template #default="{ row }">
              <img 
                :src="row.cover || '/default-script.jpg'" 
                class="list-cover"
                @click="goToDetail(row.id)"
              />
            </template>
          </el-table-column>

          <el-table-column label="剧本名称" min-width="200">
            <template #default="{ row }">
              <div class="list-name" @click="goToDetail(row.id)">
                {{ row.name }}
                <div class="list-tags">
                  <el-tag size="small" type="success">{{ row.categoryName }}</el-tag>
                  <el-tag size="small">{{ row.playerCount }}人</el-tag>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="评分" width="180">
            <template #default="{ row }">
              <el-rate v-model="row.rating" disabled size="small" show-score />
            </template>
          </el-table-column>

          <el-table-column label="价格" width="120">
            <template #default="{ row }">
              <span class="list-price">¥{{ row.price }}/人</span>
            </template>
          </el-table-column>

          <el-table-column label="收藏时间" width="180">
            <template #default="{ row }">
              {{ formatDateTime(row.favoriteTime) }}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="handleReserve(row.id)">
                预约
              </el-button>
              <el-button type="danger" size="small" @click="handleUnfavorite(row.id)">
                取消收藏
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 空状态 -->
      <EmptyState
        v-if="scriptList.length === 0 && !loading"
        type="no-favorite"
        @action="$router.push('/script')"
      />

      <!-- 分页 -->
      <el-pagination
        v-if="total > 0"
        class="pagination"
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[12, 24, 36, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 回到顶部 -->
    <el-backtop :right="40" :bottom="40" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import EmptyState from '@/components/EmptyState.vue'
import { getFavoriteScripts, unfavoriteScript } from '@/api/script'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Grid, 
  List, 
  Refresh, 
  Filter, 
  Star, 
  View, 
  Clock,
  Calendar,
  Delete
} from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const scriptList = ref([])
const total = ref(0)
const viewMode = ref('card') // card or list
const sortBy = ref('latest')
const showFilterPanel = ref(false)
const categories = ref([
  { id: 1, name: '推理' },
  { id: 2, name: '恐怖' },
  { id: 3, name: '情感' },
  { id: 4, name: '欢乐' },
  { id: 5, name: '机制' },
])

const queryParams = reactive({
  page: 1,
  pageSize: 12
})

const filters = reactive({
  category: '',
  playerCount: '',
  difficulty: '',
  priceRange: [0, 500],
  minRating: 0,
  dateRange: null
})

// 活跃的筛选条件数量
const activeFiltersCount = computed(() => {
  let count = 0
  if (filters.category) count++
  if (filters.playerCount) count++
  if (filters.difficulty) count++
  if (filters.minRating > 0) count++
  if (filters.dateRange && filters.dateRange.length === 2) count++
  if (filters.priceRange[0] > 0 || filters.priceRange[1] < 500) count++
  return count
})

// 活跃的筛选标签
const activeFilterTags = computed(() => {
  const tags = []
  if (filters.category) {
    const cat = categories.value.find(c => c.id === filters.category)
    tags.push({ key: 'category', label: '分类', value: cat?.name || filters.category })
  }
  if (filters.playerCount) {
    tags.push({ key: 'playerCount', label: '人数', value: filters.playerCount })
  }
  if (filters.difficulty) {
    tags.push({ key: 'difficulty', label: '难度', value: filters.difficulty })
  }
  if (filters.minRating > 0) {
    tags.push({ key: 'minRating', label: '评分', value: `≥${filters.minRating}星` })
  }
  if (filters.priceRange[0] > 0 || filters.priceRange[1] < 500) {
    tags.push({ 
      key: 'priceRange', 
      label: '价格', 
      value: `¥${filters.priceRange[0]}-¥${filters.priceRange[1]}` 
    })
  }
  return tags
})

// 加载收藏列表
const loadFavorites = async () => {
  loading.value = true
  try {
    console.log('加载收藏列表，参数:', { page: queryParams.page, pageSize: queryParams.pageSize })
    // 后端不支持筛选参数，只传基本分页参数
    const params = {
      page: queryParams.page,
      pageSize: queryParams.pageSize
    }
    
    console.log('请求收藏列表，参数:', params)
    const res = await getFavoriteScripts(params)
    console.log('收藏列表返回数据:', res)
    
    if (res.code === 1 || res.code === 200) {
      // 后端返回的是 PageResult，结构是 {total, records}
      if (res.data) {
        let list = res.data.records || []
        
        // 前端筛选（因为后端暂不支持筛选参数）
        if (filters.category) {
          list = list.filter(s => s.categoryId === filters.category)
        }
        if (filters.playerCount) {
          const range = filters.playerCount.split('-')
          if (range.length === 2) {
            const min = parseInt(range[0])
            const max = range[1] === '+' ? 999 : parseInt(range[1])
            list = list.filter(s => s.playerCount >= min && s.playerCount <= max)
          }
        }
        if (filters.difficulty) {
          list = list.filter(s => s.difficulty === filters.difficulty)
        }
        if (filters.minRating > 0) {
          list = list.filter(s => (s.rating || 0) >= filters.minRating)
        }
        if (filters.priceRange[0] > 0 || filters.priceRange[1] < 500) {
          list = list.filter(s => s.price >= filters.priceRange[0] && s.price <= filters.priceRange[1])
        }
        
        // 前端排序
        if (sortBy.value === 'rating') {
          list.sort((a, b) => (b.rating || 0) - (a.rating || 0))
        } else if (sortBy.value === 'price-asc') {
          list.sort((a, b) => a.price - b.price)
        } else if (sortBy.value === 'price-desc') {
          list.sort((a, b) => b.price - a.price)
        }
        
        scriptList.value = list
        // 如果 total 为 0 但有数据，使用数据长度
        total.value = res.data.total || list.length
      } else {
        scriptList.value = []
        total.value = 0
      }
      
      console.log('处理后的收藏列表:', scriptList.value)
      console.log('收藏总数:', total.value)
    } else {
      ElMessage.error(res.msg || '加载失败')
    }
  } catch (error) {
    console.error('加载收藏列表失败:', error)
    ElMessage.error('加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 应用筛选
const applyFilters = () => {
  queryParams.page = 1
  loadFavorites()
  showFilterPanel.value = false
}

// 重置筛选
const resetFilters = () => {
  filters.category = ''
  filters.playerCount = ''
  filters.difficulty = ''
  filters.priceRange = [0, 500]
  filters.minRating = 0
  filters.dateRange = null
  queryParams.page = 1
  loadFavorites()
}

// 移除单个筛选条件
const removeFilter = (key) => {
  if (key === 'priceRange') {
    filters[key] = [0, 500]
  } else if (key === 'minRating') {
    filters[key] = 0
  } else {
    filters[key] = ''
  }
  applyFilters()
}

// 保存筛选条件
const saveFilters = () => {
  localStorage.setItem('favorites_filters', JSON.stringify(filters))
  ElMessage.success('筛选条件已保存')
}

// 加载保存的筛选条件
const loadSavedFilters = () => {
  const saved = localStorage.getItem('favorites_filters')
  if (saved) {
    try {
      const savedFilters = JSON.parse(saved)
      Object.assign(filters, savedFilters)
    } catch (e) {
      console.error('加载保存的筛选条件失败:', e)
    }
  }
}

// 刷新
const handleRefresh = () => {
  loadFavorites()
  ElMessage.success('刷新成功')
}

// 跳转到详情页
const goToDetail = (id) => {
  router.push(`/script/${id}`)
}

// 预约剧本
const handleReserve = (scriptId) => {
  router.push({
    path: '/reservation/create',
    query: { scriptId }
  })
}

// 取消收藏
const handleUnfavorite = async (scriptId) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏这个剧本吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await unfavoriteScript(scriptId)
    ElMessage.success('取消收藏成功')
    
    // 刷新列表
    loadFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 分页变化
const handlePageChange = (newPage) => {
  console.log('收藏列表页码变化:', newPage)
  queryParams.page = newPage
  loadFavorites()
}

const handleSizeChange = (newSize) => {
  console.log('收藏列表每页大小变化:', newSize)
  queryParams.pageSize = newSize
  queryParams.page = 1
  loadFavorites()
}

const handleCurrentChange = () => {
  loadFavorites()
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString()
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

onMounted(() => {
  loadSavedFilters()
  loadFavorites()
})
</script>

<style scoped>
.favorites-enhanced {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

/* 工具栏 */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.toolbar-left h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.toolbar-right {
  display: flex;
  gap: 10px;
}

/* 筛选面板 */
.filter-panel {
  margin-bottom: 20px;
}

.filter-panel .el-card {
  border-radius: 8px;
}

/* 快速筛选标签 */
.quick-filters {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  background: white;
  padding: 15px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

/* 排序栏 */
.sort-bar {
  display: flex;
  align-items: center;
  gap: 15px;
  background: white;
  padding: 15px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.sort-label {
  font-weight: bold;
  color: #606266;
}

/* 内容区域 */
.content-area {
  min-height: 400px;
}

/* 卡片视图 */
.card-view {
  margin-bottom: 20px;
}

.script-card {
  margin-bottom: 20px;
  transition: all 0.3s;
  border-radius: 8px;
  overflow: hidden;
}

.script-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.1);
}

.card-cover {
  position: relative;
  width: 100%;
  height: 240px;
  overflow: hidden;
  cursor: pointer;
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.card-cover:hover img {
  transform: scale(1.1);
}

.card-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.card-cover:hover .card-overlay {
  opacity: 1;
}

.card-badges {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  gap: 5px;
}

.card-content {
  padding: 15px;
}

.card-title {
  font-size: 16px;
  margin: 0 0 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  color: #303133;
  transition: color 0.3s;
}

.card-title:hover {
  color: #409eff;
}

.card-tags {
  display: flex;
  gap: 5px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.card-rating {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-bottom: 10px;
}

.review-count {
  color: #909399;
  font-size: 12px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid #ebeef5;
  margin-bottom: 10px;
}

.card-price {
  display: flex;
  align-items: baseline;
}

.price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}

.unit {
  color: #909399;
  font-size: 12px;
  margin-left: 2px;
}

.card-actions {
  display: flex;
  gap: 5px;
}

.card-meta {
  padding-top: 10px;
  border-top: 1px solid #ebeef5;
}

.favorite-time {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #909399;
  font-size: 12px;
}

/* 列表视图 */
.list-view {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.list-cover {
  width: 100px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  transition: transform 0.3s;
}

.list-cover:hover {
  transform: scale(1.05);
}

.list-name {
  cursor: pointer;
  transition: color 0.3s;
}

.list-name:hover {
  color: #409eff;
}

.list-tags {
  display: flex;
  gap: 5px;
  margin-top: 5px;
}

.list-price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
}

/* 响应式 */
@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    gap: 15px;
  }
  
  .toolbar-left,
  .toolbar-right {
    width: 100%;
    justify-content: space-between;
  }
  
  .sort-bar {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .card-footer {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .card-actions {
    width: 100%;
  }
  
  .card-actions .el-button {
    flex: 1;
  }
}
</style>
