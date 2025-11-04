<template>
  <div class="global-search">
    <!-- 搜索输入框 -->
    <el-popover
      v-model:visible="showPanel"
      :width="600"
      placement="bottom-start"
      trigger="manual"
      popper-class="search-popover"
    >
      <template #reference>
        <div class="search-input-wrapper">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索剧本、门店..."
            :prefix-icon="Search"
            clearable
            @focus="showPanel = true"
            @blur="handleBlur"
            @input="handleInput"
            @keyup.enter="handleSearch"
            class="search-input"
          />
        </div>
      </template>

      <!-- 搜索面板 -->
      <div class="search-panel" v-loading="loading">
        <!-- 搜索建议 -->
        <div v-if="!searchKeyword && !loading" class="search-suggestions">
          <!-- 搜索历史 -->
          <div v-if="searchHistory.length > 0" class="suggestion-section">
            <div class="section-header">
              <span class="section-title">
                <el-icon><Clock /></el-icon>
                搜索历史
              </span>
              <el-button text size="small" @click="handleClearHistory">
                清空
              </el-button>
            </div>
            <div class="history-tags">
              <el-tag
                v-for="(item, index) in searchHistory"
                :key="index"
                closable
                @close="handleRemoveHistory(item)"
                @click="handleClickHistory(item)"
                class="history-tag"
              >
                {{ item }}
              </el-tag>
            </div>
          </div>

          <!-- 热门搜索 -->
          <div v-if="hotSearches.length > 0" class="suggestion-section">
            <div class="section-header">
              <span class="section-title">
                <el-icon><TrendCharts /></el-icon>
                热门搜索
              </span>
            </div>
            <div class="hot-searches">
              <div
                v-for="(item, index) in hotSearches"
                :key="index"
                class="hot-item"
                @click="handleClickHistory(item)"
              >
                <span class="hot-rank" :class="getRankClass(index)">{{ index + 1 }}</span>
                <span class="hot-keyword">{{ item }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 搜索结果 -->
        <div v-else-if="searchKeyword" class="search-results">
          <!-- 剧本结果 -->
          <div v-if="scriptResults.length > 0" class="result-section">
            <div class="section-header">
              <span class="section-title">
                <el-icon><Document /></el-icon>
                剧本 ({{ scriptResults.length }})
              </span>
              <el-button text size="small" @click="handleViewMore('script')">
                查看更多 <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
            <div class="result-list">
              <div
                v-for="script in scriptResults"
                :key="script.id"
                class="result-item"
                @click="handleClickScript(script)"
              >
                <el-image
                  :src="script.coverImage || PLACEHOLDERS.SCRIPT"
                  class="result-image"
                  fit="cover"
                />
                <div class="result-info">
                  <div class="result-title" v-html="highlightKeyword(script.name)"></div>
                  <div class="result-desc">
                    <el-tag size="small">{{ script.categoryName }}</el-tag>
                    <span class="result-meta">{{ script.playerCount }}人</span>
                    <span class="result-meta">{{ script.duration }}分钟</span>
                  </div>
                  <div class="result-price">¥{{ script.price }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 门店结果 -->
          <div v-if="storeResults.length > 0" class="result-section">
            <div class="section-header">
              <span class="section-title">
                <el-icon><Shop /></el-icon>
                门店 ({{ storeResults.length }})
              </span>
              <el-button text size="small" @click="handleViewMore('store')">
                查看更多 <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
            <div class="result-list">
              <div
                v-for="store in storeResults"
                :key="store.id"
                class="result-item"
                @click="handleClickStore(store)"
              >
                <el-image
                  :src="store.image || '/default-store.jpg'"
                  class="result-image"
                  fit="cover"
                />
                <div class="result-info">
                  <div class="result-title" v-html="highlightKeyword(store.name)"></div>
                  <div class="result-desc">
                    <el-rate
                      v-model="store.rating"
                      disabled
                      size="small"
                      :show-score="false"
                    />
                    <span class="result-meta">{{ store.address }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 无结果 -->
          <el-empty
            v-if="!loading && scriptResults.length === 0 && storeResults.length === 0"
            description="未找到相关内容"
            :image-size="120"
          >
            <el-button type="primary" @click="showPanel = false">关闭</el-button>
          </el-empty>
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Clock,
  TrendCharts,
  Document,
  Shop,
  ArrowRight
} from '@element-plus/icons-vue'
import {
  searchScripts,
  searchStores,
  saveSearchHistory,
  getSearchHistory,
  clearSearchHistory,
  removeSearchHistory
} from '@/api/search'
import { PLACEHOLDERS } from '@/assets/placeholders'

const router = useRouter()

// 搜索状态
const searchKeyword = ref('')
const showPanel = ref(false)
const loading = ref(false)

// 搜索历史和热门搜索
const searchHistory = ref([])
const hotSearches = ref([
  '机械迷城',
  '红蝶',
  '年轮',
  '云使',
  '长安',
  '迷失庄园',
  '时空旅行',
  '黑暗森林'
])

// 搜索结果
const scriptResults = ref([])
const storeResults = ref([])

// 防抖定时器
let debounceTimer = null

// 加载搜索历史
const loadSearchHistory = () => {
  searchHistory.value = getSearchHistory()
}

// 高亮关键词
const highlightKeyword = (text) => {
  if (!searchKeyword.value || !text) return text
  const keyword = searchKeyword.value.trim()
  const regex = new RegExp(`(${keyword})`, 'gi')
  return text.replace(regex, '<span class="highlight">$1</span>')
}

// 获取排名样式
const getRankClass = (index) => {
  if (index === 0) return 'rank-1'
  if (index === 1) return 'rank-2'
  if (index === 2) return 'rank-3'
  return ''
}

// 失焦处理（延迟关闭，给用户时间点击下拉选项）
const handleBlur = () => {
  // 延迟关闭，确保点击下拉项时不会因为失焦而立即关闭
  setTimeout(() => {
    // 如果鼠标在下拉面板上，不关闭
    const popover = document.querySelector('.search-popover')
    if (popover && popover.matches(':hover')) {
      return
    }
    // 失焦时关闭面板
    showPanel.value = false
  }, 200)
}

// 输入处理（防抖搜索）
const handleInput = () => {
  clearTimeout(debounceTimer)
  
  if (!searchKeyword.value.trim()) {
    scriptResults.value = []
    storeResults.value = []
    return
  }
  
  debounceTimer = setTimeout(() => {
    performSearch()
  }, 500)
}

// 执行搜索
const performSearch = async () => {
  if (!searchKeyword.value.trim()) return
  
  loading.value = true
  
  try {
    // 并发搜索剧本和门店
    const [scriptRes, storeRes] = await Promise.all([
      searchScripts(searchKeyword.value, { page: 1, pageSize: 5 }),
      searchStores(searchKeyword.value, { page: 1, pageSize: 5 })
    ])
    
    scriptResults.value = scriptRes.data?.records || []
    storeResults.value = storeRes.data?.records || []
  } catch (error) {
    console.error('搜索失败:', error)
  } finally {
    loading.value = false
  }
}

// 按回车搜索
const handleSearch = () => {
  if (!searchKeyword.value.trim()) return
  
  // 保存搜索历史
  saveSearchHistory(searchKeyword.value)
  loadSearchHistory()
  
  // 跳转到搜索结果页
  router.push({
    path: '/search',
    query: { keyword: searchKeyword.value }
  })
  
  showPanel.value = false
}

// 点击搜索历史
const handleClickHistory = (keyword) => {
  searchKeyword.value = keyword
  handleSearch()
}

// 清空搜索历史
const handleClearHistory = () => {
  ElMessageBox.confirm('确定要清空搜索历史吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    clearSearchHistory()
    loadSearchHistory()
    ElMessage.success('已清空搜索历史')
  }).catch(() => {})
}

// 删除单条历史
const handleRemoveHistory = (keyword) => {
  removeSearchHistory(keyword)
  loadSearchHistory()
}

// 查看更多
const handleViewMore = (type) => {
  if (!searchKeyword.value.trim()) return
  
  saveSearchHistory(searchKeyword.value)
  
  router.push({
    path: '/search',
    query: { 
      keyword: searchKeyword.value,
      type
    }
  })
  
  showPanel.value = false
}

// 点击剧本
const handleClickScript = (script) => {
  saveSearchHistory(searchKeyword.value)
  router.push(`/script/${script.id}`)
  showPanel.value = false
}

// 点击门店
const handleClickStore = (store) => {
  saveSearchHistory(searchKeyword.value)
  router.push(`/store/${store.id}`)
  showPanel.value = false
}

// 点击外部关闭面板（已禁用，改为一直显示）
// const handleClickOutside = (event) => {
//   const searchPanel = document.querySelector('.search-popover')
//   const searchInput = document.querySelector('.search-input-wrapper')
//   
//   if (searchPanel && !searchPanel.contains(event.target) && 
//       searchInput && !searchInput.contains(event.target)) {
//     showPanel.value = false
//   }
// }

onMounted(() => {
  loadSearchHistory()
  // 注释掉自动关闭监听，让下拉框一直显示
  // document.addEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.global-search {
  width: 300px;
}

.search-input-wrapper {
  width: 100%;
}

.search-input {
  width: 100%;
}

.search-panel {
  max-height: 500px;
  overflow-y: auto;
}

.search-suggestions,
.search-results {
  padding: 8px 0;
}

.suggestion-section,
.result-section {
  margin-bottom: 20px;
}

.suggestion-section:last-child,
.result-section:last-child {
  margin-bottom: 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  margin-bottom: 8px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: bold;
  color: #303133;
}

.history-tags {
  padding: 0 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.history-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.history-tag:hover {
  transform: translateY(-2px);
}

.hot-searches {
  padding: 0 12px;
}

.hot-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
}

.hot-item:hover {
  background: #f5f7fa;
}

.hot-rank {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  border-radius: 4px;
  background: #e4e7ed;
  color: #909399;
}

.hot-rank.rank-1 {
  background: #f56c6c;
  color: white;
}

.hot-rank.rank-2 {
  background: #e6a23c;
  color: white;
}

.hot-rank.rank-3 {
  background: #409eff;
  color: white;
}

.hot-keyword {
  flex: 1;
  font-size: 14px;
  color: #606266;
}

.result-list {
  padding: 0 12px;
}

.result-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s;
  margin-bottom: 8px;
}

.result-item:hover {
  background: #f5f7fa;
}

.result-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  flex-shrink: 0;
}

.result-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.result-title {
  font-size: 15px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.result-title :deep(.highlight) {
  color: #409eff;
  background: #ecf5ff;
  padding: 0 4px;
  border-radius: 2px;
}

.result-desc {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.result-meta {
  font-size: 12px;
}

.result-price {
  font-size: 16px;
  font-weight: bold;
  color: #f56c6c;
}
</style>

<style>
.search-popover {
  padding: 8px 0 !important;
}
</style>
