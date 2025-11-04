<template>
  <div class="store-list-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索门店名称"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        
        <el-form-item label="地区">
          <el-input
            v-model="searchForm.address"
            placeholder="输入地址"
            clearable
            @clear="handleSearch"
          />
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

    <!-- 位置提示 -->
    <transition name="slide-fade">
      <el-alert
        v-if="!userLocation"
        type="info"
        :closable="true"
        show-icon
        class="location-tip"
      >
        <template #title>
          <span style="display: flex; align-items: center; gap: 8px;">
            <el-icon><LocationInformation /></el-icon>
            允许位置访问，查看门店距离
          </span>
        </template>
        <div style="font-size: 13px; margin-top: 5px;">
          浏览器将请求您的位置权限，允许后可以看到每个门店离您的距离
        </div>
      </el-alert>
    </transition>

    <div class="list-header">
      <span class="total">
        共 {{ total }} 家门店
        <el-tag v-if="userLocation" type="success" size="small" style="margin-left: 10px;">
          <el-icon><LocationInformation /></el-icon>
          距离已显示
        </el-tag>
      </span>
      <div class="sort-buttons">
        <div class="radio-group-wrapper">
          <el-radio-group v-model="searchForm.sortBy" @change="handleSortChange">
            <el-radio-button label="distance">
              <el-icon><Location /></el-icon>
              距离最近
            </el-radio-button>
            <el-radio-button label="rating">
              <el-icon><Star /></el-icon>
              评分最高
            </el-radio-button>
            <el-radio-button label="hot">
              <el-icon><TrendCharts /></el-icon>
              最热门
            </el-radio-button>
          </el-radio-group>
          <transition name="fade">
            <span v-if="searchForm.sortBy === 'distance' && searchForm.latitude" class="location-info">
              <el-icon><CircleCheck /></el-icon>
              已定位
            </span>
          </transition>
        </div>
      </div>
    </div>

    <!-- 骨架屏 -->
    <div v-if="loading">
      <SkeletonStoreCard v-for="i in searchForm.pageSize" :key="i" />
    </div>
    
    <!-- 真实内容 -->
    <el-row :gutter="20" v-else>
      <el-col :xs="24" :md="12" v-for="store in stores" :key="store.id">
        <div class="store-card" @click="goToDetail(store.id)">
          <div class="store-image">
            <LazyImage :src="store.coverImage || getStoreCover(store.name)" :alt="store.name" :height="180" />
          </div>
          <div class="store-info">
            <h3>{{ store.name }}</h3>
            <div class="store-address">
              <el-icon><Location /></el-icon>
              {{ store.address }}
              <span v-if="store.distance" class="distance-tag">
                {{ formatDistance(store.distance) }}
              </span>
            </div>
            <div class="store-phone" v-if="store.phone">
              <el-icon><Phone /></el-icon>
              {{ store.phone }}
            </div>
            <div class="store-time" v-if="store.openTime && store.closeTime">
              <el-icon><Clock /></el-icon>
              营业时间：{{ store.openTime }} - {{ store.closeTime }}
            </div>
            <div class="store-rating">
              <el-rate v-model="store.rating" disabled show-score size="small" />
              <span class="review-count">({{ store.reviewCount || 0 }}条评价)</span>
            </div>
            <div class="store-tags">
              <el-tag size="small" v-for="tag in store.tags" :key="tag">{{ tag }}</el-tag>
            </div>
            <div class="store-footer">
              <el-button type="primary" size="small" @click.stop="handleReserve(store)">
                立即预约
              </el-button>
              <el-button size="small" @click.stop="handleCall(store)">
                <el-icon><Phone /></el-icon>
                电话咨询
              </el-button>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && stores.length === 0" description="暂无门店" />

    <el-pagination
      v-if="total > 0"
      class="pagination"
      v-model:current-page="searchForm.page"
      v-model:page-size="searchForm.pageSize"
      :total="total"
      :page-sizes="[10, 20, 30]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getStoreListAdvanced } from '@/api/store'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { getUserLocation as getUserLocationUtil, formatDistance as formatDistanceUtil, calculateDistance } from '@/utils/location'
import SkeletonStoreCard from '@/components/Skeleton/SkeletonStoreCard.vue'
import LazyImage from '@/components/LazyImage.vue'
import { getStoreCover } from '@/assets/store-covers'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const stores = ref([])
const total = ref(0)
const userLocation = ref(null) // 存储用户位置

const searchForm = reactive({
  keyword: '',
  address: '',
  sortBy: 'rating',
  latitude: null,
  longitude: null,
  page: 1,
  pageSize: 10
})

const loadStores = async () => {
  loading.value = true
  try {
    console.log('加载门店列表，参数:', { page: searchForm.page, pageSize: searchForm.pageSize })
    // 构建查询参数
    const queryData = {
      name: searchForm.keyword,
      address: searchForm.address,
      sortBy: searchForm.sortBy,
      page: searchForm.page,
      pageSize: searchForm.pageSize
    }
    
    // 如果是距离排序，添加经纬度信息
    if (searchForm.sortBy === 'distance' && searchForm.latitude && searchForm.longitude) {
      queryData.latitude = searchForm.latitude
      queryData.longitude = searchForm.longitude
    }
    
    const res = await getStoreListAdvanced(queryData)
    if (res.data) {
      if (Array.isArray(res.data)) {
        stores.value = res.data
        total.value = res.data.length
      } else {
        stores.value = res.data.records || res.data.list || []
        total.value = res.data.total || 0
      }
      
      // 自动计算所有门店的距离
      calculateStoresDistance()
    }
  } catch (error) {
    console.error('加载门店列表失败:', error)
    ElMessage.error('加载门店列表失败，请稍后重试')
    stores.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 计算所有门店的距离
const calculateStoresDistance = () => {
  // 如果没有用户位置，不计算
  if (!userLocation.value) return
  
  // 为每个门店计算距离
  stores.value.forEach(store => {
    if (store.latitude && store.longitude) {
      // 计算距离（公里）
      const distance = calculateDistance(
        userLocation.value.latitude,
        userLocation.value.longitude,
        store.latitude,
        store.longitude
      )
      // 添加distance属性
      store.distance = distance
    }
  })
}

const handlePageChange = (newPage) => {
  console.log('门店列表页码变化:', newPage)
  searchForm.page = newPage
  loadStores()
}

const handleSizeChange = (newSize) => {
  console.log('门店列表每页大小变化:', newSize)
  searchForm.pageSize = newSize
  searchForm.page = 1
  loadStores()
}

const handleSearch = () => {
  searchForm.page = 1
  // 如果选择距离排序但没有位置信息，先获取位置
  if (searchForm.sortBy === 'distance' && !searchForm.latitude) {
    getUserLocation()
  } else {
    loadStores()
  }
}

// 处理排序方式改变
const handleSortChange = (value) => {
  searchForm.page = 1
  if (value === 'distance' && !searchForm.latitude) {
    getUserLocation()
  } else {
    loadStores()
  }
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.address = ''
  searchForm.sortBy = 'rating'
  handleSearch()
}

const goToDetail = (id) => {
  router.push(`/store/${id}`)
}

const handleReserve = (store) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push({
    path: '/reservation/create',
    query: { storeId: store.id }
  })
}

const handleCall = (store) => {
  if (store.phone) {
    window.location.href = `tel:${store.phone}`
  } else {
    ElMessage.info('暂无联系电话')
  }
}

// 格式化距离显示（使用统一工具函数）
const formatDistance = (distance) => {
  return formatDistanceUtil(distance)
}

// 获取用户地理位置（使用统一工具函数）
const getUserLocation = async () => {
  loading.value = true
  ElMessage.info('正在获取您的位置...')
  
  try {
    const location = await getUserLocationUtil()
    searchForm.latitude = location.latitude
    searchForm.longitude = location.longitude
    console.log('获取位置成功:', location)
    ElMessage.success('位置获取成功')
    loadStores()
  } catch (error) {
    loading.value = false
    console.error('获取位置失败:', error)
    
    let errorMsg = '获取位置失败，将按评分排序'
    if (error.code === 1) {
      errorMsg = '您拒绝了位置权限请求，将按评分排序'
    } else if (error.code === 2) {
      errorMsg = '位置信息不可用，将按评分排序'
    } else if (error.code === 3) {
      errorMsg = '获取位置超时，将按评分排序'
    } else if (error.message && error.message.includes('不支持')) {
      errorMsg = '您的浏览器不支持地理定位，将按评分排序'
    }
    
    ElMessage.warning(errorMsg)
    // 降级到评分排序
    searchForm.sortBy = 'rating'
    loadStores()
  }
}

// 初始化用户位置（静默模式，不显示错误）
const initUserLocation = async () => {
  try {
    const location = await getUserLocationUtil()
    userLocation.value = location
    searchForm.latitude = location.latitude
    searchForm.longitude = location.longitude
    console.log('用户位置获取成功:', location)
    
    // 重新计算已加载门店的距离
    if (stores.value.length > 0) {
      calculateStoresDistance()
    }
  } catch (error) {
    // 静默失败，不影响页面使用
    console.log('自动获取位置失败（不影响使用）:', error.message || error)
  }
}

onMounted(() => {
  // 先加载门店列表
  loadStores()
  // 同时异步获取用户位置
  initUserLocation()
})
</script>

<style scoped>
.store-list-container {
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

.sort-buttons {
  display: flex;
  align-items: center;
}

.radio-group-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.sort-buttons .el-radio-button :deep(.el-radio-button__inner) {
  display: flex;
  align-items: center;
  gap: 5px;
}

.location-info {
  position: absolute;
  right: -90px;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #67c23a;
  padding: 5px 10px;
  background: #f0f9ff;
  border-radius: 4px;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 位置提示样式 */
.location-tip {
  margin-bottom: 20px;
  border-radius: 8px;
}

/* 淡入淡出动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateX(-10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateX(10px);
}

/* 滑动淡入动画 */
.slide-fade-enter-active {
  transition: all 0.4s ease;
}

.slide-fade-leave-active {
  transition: all 0.3s ease;
}

.slide-fade-enter-from {
  opacity: 0;
  transform: translateY(-20px);
}

.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.store-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
}

.store-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.store-image {
  width: 300px;
  height: 240px;
  flex-shrink: 0;
  overflow: hidden;
}

.store-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.store-info {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.store-info h3 {
  margin: 0 0 15px;
  font-size: 20px;
  color: #333;
}

.store-address,
.store-phone,
.store-time {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.distance-tag {
  margin-left: auto;
  padding: 2px 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 12px;
  border-radius: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.store-rating {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 10px 0;
}

.review-count {
  font-size: 12px;
  color: #999;
}

.store-tags {
  margin: 10px 0;
}

.store-tags .el-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

.store-footer {
  margin-top: auto;
  display: flex;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .store-card {
    flex-direction: column;
  }
  
  .store-image {
    width: 100%;
    height: 200px;
  }
}
</style>
