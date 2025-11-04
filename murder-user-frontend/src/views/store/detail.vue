<template>
  <div class="store-detail-container" v-loading="loading">
    <el-card v-if="store">
      <!-- 门店头部信息 -->
      <div class="store-header">
        <el-row :gutter="30">
          <el-col :xs="24" :md="12">
            <div class="store-images">
              <el-carousel height="400px">
                <el-carousel-item v-for="(image, index) in storeImages" :key="index">
                  <img :src="image" :alt="store.name" />
                </el-carousel-item>
              </el-carousel>
            </div>
          </el-col>
          
          <el-col :xs="24" :md="12">
            <div class="store-basic-info">
              <h1>{{ store.name }}</h1>
              
              <div class="store-rating">
                <el-rate v-model="store.rating" disabled show-score size="large" />
                <span class="review-count">({{ store.reviewCount || 0 }}条评价)</span>
              </div>
              
              <el-descriptions :column="1" class="store-desc">
                <el-descriptions-item>
                  <template #label>
                    <el-icon><Location /></el-icon>
                    地址
                  </template>
                  <div class="address-with-distance">
                    <span>{{ store.address }}</span>
                    <el-tag v-if="distance" type="info" size="small" class="distance-tag">
                      <el-icon><LocationInformation /></el-icon>
                      距离我 {{ distance }}
                    </el-tag>
                    <el-button 
                      v-if="!distance && !locationError" 
                      type="primary" 
                      text 
                      size="small"
                      @click="getMyLocation"
                      :loading="locationLoading"
                    >
                      <el-icon><Location /></el-icon>
                      获取距离
                    </el-button>
                    <el-tooltip v-if="locationError" :content="locationError" placement="top">
                      <el-tag type="warning" size="small">
                        <el-icon><Warning /></el-icon>
                        无法获取位置
                      </el-tag>
                    </el-tooltip>
                  </div>
                </el-descriptions-item>
                <el-descriptions-item>
                  <template #label>
                    <el-icon><Phone /></el-icon>
                    电话
                  </template>
                  {{ store.phone }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template #label>
                    <el-icon><Clock /></el-icon>
                    营业时间
                  </template>
                  {{ store.openTime }} - {{ store.closeTime }}
                </el-descriptions-item>
              </el-descriptions>
              
              <div class="store-tags">
                <el-tag v-for="tag in store.tags" :key="tag" type="success">{{ tag }}</el-tag>
              </div>
              
              <div class="action-buttons">
                <el-button type="primary" size="large" @click="handleReserve">
                  <el-icon><Calendar /></el-icon>
                  立即预约
                </el-button>
                <el-button size="large" @click="handleCall">
                  <el-icon><Phone /></el-icon>
                  电话咨询
                </el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
    
    <!-- 门店介绍 -->
    <el-card class="detail-card store-intro-card">
      <template #header>
        <div class="card-header">
        <span>门店介绍</span>
          <el-tag type="primary" size="small">
            <el-icon><Star /></el-icon>
            {{ store?.rating || 4.8 }} 分
          </el-tag>
        </div>
      </template>
      
      <!-- 门店特色标签 -->
      <div class="store-highlights" v-if="store?.highlights && store.highlights.length > 0">
        <h4 class="section-title">
          <el-icon><TrendCharts /></el-icon>
          门店特色
        </h4>
        <div class="highlight-tags">
          <el-tag 
            v-for="(highlight, index) in store.highlights" 
            :key="index"
            :type="getHighlightType(index)"
            effect="plain"
            size="large"
            class="highlight-tag"
          >
            <el-icon><Check /></el-icon>
            {{ highlight }}
          </el-tag>
        </div>
      </div>
      
      <!-- 门店设施 -->
      <div class="store-facilities" v-if="store?.facilities && store.facilities.length > 0">
        <h4 class="section-title">
          <el-icon><Medal /></el-icon>
          门店设施
        </h4>
        <el-row :gutter="15" class="facility-list">
          <el-col :xs="12" :sm="8" :md="6" v-for="(facility, index) in store.facilities" :key="index">
            <div class="facility-item">
              <el-icon :color="getFacilityColor(index)">
                <component :is="getFacilityIcon(facility)" />
              </el-icon>
              <span>{{ facility }}</span>
            </div>
          </el-col>
        </el-row>
      </div>
      
      <!-- 门店简介 -->
      <div class="store-description-section">
        <h4 class="section-title">
          <el-icon><Document /></el-icon>
          门店简介
        </h4>
        <div class="store-description" v-html="store?.description || '这是一家优质的剧本杀门店，环境舒适，服务专业，拥有丰富的剧本资源和专业的DM团队。'"></div>
      </div>
      
      <!-- 营业信息 -->
      <div class="store-business-info">
        <h4 class="section-title">
          <el-icon><Clock /></el-icon>
          营业信息
        </h4>
        <el-row :gutter="20" class="business-info-grid">
          <el-col :xs="24" :sm="12" :md="6">
            <div class="info-card">
              <div class="info-icon">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="info-content">
                <div class="info-label">营业时间</div>
                <div class="info-value">{{ store?.openTime || '10:00' }} - {{ store?.closeTime || '22:00' }}</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <div class="info-card">
              <div class="info-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="info-content">
                <div class="info-label">已服务</div>
                <div class="info-value">{{ store?.serviceCount || '5000+' }} 人次</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <div class="info-card">
              <div class="info-icon">
                <el-icon><Reading /></el-icon>
              </div>
              <div class="info-content">
                <div class="info-label">剧本数量</div>
                <div class="info-value">{{ store?.scriptCount || '200+' }} 个</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <div class="info-card">
              <div class="info-icon">
                <el-icon><House /></el-icon>
              </div>
              <div class="info-content">
                <div class="info-label">房间数量</div>
                <div class="info-value">{{ rooms.length }} 间</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      
      <!-- 门店环境（图片展示） -->
      <div class="store-environment" v-if="store?.environmentImages && store.environmentImages.length > 0">
        <h4 class="section-title">
          <el-icon><Picture /></el-icon>
          门店环境
        </h4>
        <el-row :gutter="15" class="environment-gallery">
          <el-col :xs="12" :sm="8" :md="6" v-for="(image, index) in store.environmentImages" :key="index">
            <div class="environment-image" @click="previewImage(image)">
              <img :src="image" :alt="`门店环境 ${index + 1}`" />
              <div class="image-overlay">
                <el-icon><ZoomIn /></el-icon>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
    
    <!-- 房间信息 -->
    <el-card class="detail-card">
      <template #header>
        <div class="card-header">
        <span>房间信息</span>
          <el-tag v-if="availableRoomCount > 0" type="success" size="small">
            <el-icon><SuccessFilled /></el-icon>
            {{ availableRoomCount }} 间可用
          </el-tag>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="room in rooms" :key="room.id">
          <div class="room-card" :class="{ 'room-unavailable': room.status !== 1 }">
            <!-- 房间类型标签 -->
            <div class="room-type-badge" :class="`room-type-${room.type}`">
              {{ getRoomTypeName(room.type) }}
            </div>
            
            <!-- 房间状态角标 -->
            <div class="room-status-corner" :class="{ 'available': room.status === 1 }">
              <el-icon v-if="room.status === 1"><CircleCheck /></el-icon>
              <el-icon v-else><CircleClose /></el-icon>
            </div>
            
            <div class="room-header">
              <h4>{{ room.name }}</h4>
              <el-tag :type="room.status === 1 ? 'success' : 'info'" size="small">
                {{ room.status === 1 ? '可预约' : '已占用' }}
              </el-tag>
            </div>
            
            <div class="room-info">
              <div class="info-item">
                <el-icon><User /></el-icon>
                <span>容纳人数：<strong>{{ room.capacity }}</strong> 人</span>
              </div>
              
              <div class="info-item" v-if="room.type">
                <el-icon><House /></el-icon>
                <span>房间类型：{{ getRoomTypeDesc(room.type) }}</span>
              </div>
              
              <div class="info-item" v-if="room.description">
                <el-icon><InfoFilled /></el-icon>
                <span>{{ room.description }}</span>
              </div>
            </div>
            
            <!-- 预约按钮 -->
            <div class="room-action" v-if="room.status === 1">
              <el-button type="primary" size="small" @click.stop="handleRoomReserve(room)" plain>
                <el-icon><Calendar /></el-icon>
                预约此房间
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <el-empty v-if="rooms.length === 0" description="暂无房间信息">
        <el-button type="primary" @click="handleReserve">立即预约</el-button>
      </el-empty>
    </el-card>
    
    <!-- 用户评价 -->
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
    <el-dialog v-model="showReviewDialog" title="评价门店" width="500px">
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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getStoreDetail, getStoreRooms, getStoreReviews, addStoreReview } from '@/api/store'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { getUserLocation, getDistanceText } from '@/utils/location'
import { getStoreCover } from '@/assets/store-covers'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const store = ref(null)
const rooms = ref([])
const reviews = ref([])
const showReviewDialog = ref(false)
const distance = ref('')
const locationLoading = ref(false)
const locationError = ref('')

const reviewForm = reactive({
  rating: 5,
  content: ''
})

// 计算可用房间数量
const availableRoomCount = computed(() => {
  return rooms.value.filter(room => room.status === 1).length
})

const storeImages = computed(() => {
  if (store.value?.coverImage) {
    return [store.value.coverImage]
  }
  // 使用智能匹配的门店封面
  if (store.value?.name) {
    return [getStoreCover(store.value.name)]
  }
  return ['https://via.placeholder.com/800x400']
})

const loadStoreDetail = async () => {
  loading.value = true
  try {
    const res = await getStoreDetail(route.params.id)
    if (res.data) {
      store.value = res.data
      store.value.tags = store.value.tags || ['环境优雅', '服务专业', '交通便利']
      
      // 如果门店有经纬度信息，自动计算距离
      if (store.value.latitude && store.value.longitude) {
        calculateDistance()
      }
    }
  } catch (error) {
    console.error('加载门店详情失败:', error)
    // 模拟数据
    store.value = {
      id: route.params.id,
      name: '探案密室·沉浸式剧本体验馆',
      address: '北京市朝阳区三里屯路19号',
      phone: '010-12345678',
      openTime: '10:00',
      closeTime: '22:00',
      rating: 4.8,
      reviewCount: 128,
      serviceCount: '5000+',
      scriptCount: '200+',
      tags: ['环境优雅', '服务专业', '交通便利'],
      
      // 门店特色
      highlights: [
        '5年老店·口碑保证',
        '专业DM团队·剧情还原度高',
        '海量剧本·更新快',
        '豪华装修·沉浸式体验',
        '地铁直达·交通便利',
        '免费饮品·舒适环境'
      ],
      
      // 门店设施
      facilities: [
        'WiFi',
        '空调',
        '饮品',
        '零食',
        '停车场',
        '独立包厢',
        '投影设备',
        '音响系统',
        '桌游区',
        '休息区',
        '卫生间',
        '充电设施'
      ],
      
      // 门店描述
      description: `
        <p><strong>探案密室</strong>成立于2019年，是北京地区知名的沉浸式剧本杀体验馆。</p>
        
        <p>🎭 <strong>专业团队：</strong>我们拥有10余名全职DM，平均从业经验3年以上，对每个剧本都有深入研究，能够为玩家提供极致的沉浸式体验。</p>
        
        <p>📚 <strong>海量剧本：</strong>门店现有200+优质剧本，涵盖本格推理、情感沉浸、恐怖惊悚、欢乐互动等多种类型，每月更新10+新本，紧跟市场潮流。</p>
        
        <p>🏠 <strong>豪华环境：</strong>总面积800㎡，6间独立主题房间，每间房都经过精心设计和装修，配备专业灯光音响设备，营造最佳游戏氛围。</p>
        
        <p>🎯 <strong>贴心服务：</strong>提供免费WiFi、饮品、零食，以及舒适的休息区。游戏过程中DM全程跟进，确保每位玩家都能获得最佳体验。</p>
        
        <p>🚇 <strong>交通便利：</strong>地铁10号线三里屯站A出口步行5分钟即达，门店附近有多个停车场，自驾也很方便。</p>
        
        <p>⭐ <strong>好评如潮：</strong>累计服务5000+人次，用户好评率98%，是北京地区评分最高的剧本杀门店之一。</p>
      `,
      
      // 环境图片
      environmentImages: [
        'https://via.placeholder.com/300x200/667eea/ffffff?text=大厅',
        'https://via.placeholder.com/300x200/f093fb/ffffff?text=推理房',
        'https://via.placeholder.com/300x200/4facfe/ffffff?text=沉浸房',
        'https://via.placeholder.com/300x200/43e97b/ffffff?text=休息区',
        'https://via.placeholder.com/300x200/fa709a/ffffff?text=恐怖房',
        'https://via.placeholder.com/300x200/fee140/333333?text=欢乐房'
      ],
      
      coverImage: '',
      latitude: 39.908815, // 示例坐标：北京天安门
      longitude: 116.397529
    }
    // 自动计算距离
    calculateDistance()
  } finally {
    loading.value = false
  }
}

// 获取用户位置并计算距离
const getMyLocation = async () => {
  if (!store.value) return
  
  locationLoading.value = true
  locationError.value = ''
  
  try {
    const userLocation = await getUserLocation()
    
    if (store.value.latitude && store.value.longitude) {
      const targetLocation = {
        latitude: store.value.latitude,
        longitude: store.value.longitude
      }
      
      distance.value = getDistanceText(userLocation, targetLocation)
      ElMessage.success('距离计算成功')
    } else {
      ElMessage.warning('门店位置信息不完整')
    }
  } catch (error) {
    console.error('获取位置失败:', error)
    
    if (error.code === 1) {
      locationError.value = '位置权限被拒绝，请在浏览器设置中允许位置访问'
      ElMessage.warning('请允许浏览器访问您的位置')
    } else if (error.code === 2) {
      locationError.value = '无法获取位置信息'
      ElMessage.error('无法获取您的位置信息')
    } else if (error.code === 3) {
      locationError.value = '获取位置超时'
      ElMessage.error('获取位置超时，请重试')
    } else {
      locationError.value = '浏览器不支持地理定位'
      ElMessage.error('您的浏览器不支持地理定位功能')
    }
  } finally {
    locationLoading.value = false
  }
}

// 自动计算距离（静默模式，不显示错误）
const calculateDistance = async () => {
  if (!store.value?.latitude || !store.value?.longitude) return
  
  try {
    const userLocation = await getUserLocation()
    const targetLocation = {
      latitude: store.value.latitude,
      longitude: store.value.longitude
    }
    
    distance.value = getDistanceText(userLocation, targetLocation)
  } catch (error) {
    // 静默失败，不影响页面显示
    console.log('自动获取距离失败，用户可手动获取')
  }
}

const loadRooms = async () => {
  try {
    const res = await getStoreRooms(route.params.id)
    if (res.data) {
      rooms.value = res.data
    }
  } catch (error) {
    console.error('加载房间信息失败:', error)
    // 模拟数据
    rooms.value = [
      { 
        id: 1, 
        name: '推理房A', 
        type: 1,
        capacity: 4, 
        description: '适合新手玩家，温馨舒适的小型推理房间',
        status: 1 
      },
      { 
        id: 2, 
        name: '推理房B', 
        type: 2,
        capacity: 6, 
        description: '中型房间，配备专业道具和设施',
        status: 1 
      },
      { 
        id: 3, 
        name: '沉浸房C', 
        type: 3,
        capacity: 8, 
        description: '大型沉浸式剧本房间，提供最佳游戏体验',
        status: 0 
      },
      { 
        id: 4, 
        name: '欢乐房D', 
        type: 2,
        capacity: 6, 
        description: '适合欢乐向剧本，氛围轻松',
        status: 1 
      },
      { 
        id: 5, 
        name: '机制房E', 
        type: 2,
        capacity: 7, 
        description: '配备多种机关道具，适合机制本',
        status: 1 
      },
      { 
        id: 6, 
        name: '恐怖房F', 
        type: 1,
        capacity: 5, 
        description: '专为恐怖剧本设计，音效灯光俱全',
        status: 0 
      }
    ]
  }
}

const loadReviews = async () => {
  try {
    const res = await getStoreReviews({
      storeId: route.params.id,
      page: 1,
      pageSize: 10
    })
    if (res.data) {
      reviews.value = res.data.records || res.data.list || []
    }
  } catch (error) {
    console.error('加载评价失败:', error)
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
    query: { storeId: store.value.id }
  })
}

const handleCall = () => {
  if (store.value?.phone) {
    window.location.href = `tel:${store.value.phone}`
  } else {
    ElMessage.info('暂无联系电话')
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
    await addStoreReview({
      storeId: route.params.id,
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

// 获取房间类型名称
const getRoomTypeName = (type) => {
  const typeMap = {
    1: '小房',
    2: '中房',
    3: '大房'
  }
  return typeMap[type] || '普通'
}

// 获取房间类型描述
const getRoomTypeDesc = (type) => {
  const descMap = {
    1: '小型房间（2-4人）',
    2: '中型房间（5-7人）',
    3: '大型房间（8人以上）'
  }
  return descMap[type] || '标准房间'
}

// 预约指定房间
const handleRoomReserve = (room) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push({
    path: '/reservation/create',
    query: { 
      storeId: store.value.id,
      roomId: room.id,
      roomName: room.name
    }
  })
}

// 获取特色标签类型
const getHighlightType = (index) => {
  const types = ['success', 'primary', 'warning', 'danger', 'info']
  return types[index % types.length]
}

// 获取设施图标
const getFacilityIcon = (facility) => {
  const iconMap = {
    'WiFi': 'Wifi',
    'Wi-Fi': 'Wifi',
    '无线网络': 'Wifi',
    '空调': 'Wind',
    '暖气': 'Sunny',
    '饮品': 'CoffeeCup',
    '零食': 'Food',
    '停车': 'Van',
    '包厢': 'House',
    '投影': 'VideoCamera',
    '音响': 'Headset',
    '桌游': 'Grid',
    '休息区': 'Coffee',
    '卫生间': 'Location',
    '充电': 'Lightning'
  }
  
  // 模糊匹配
  for (const [key, icon] of Object.entries(iconMap)) {
    if (facility.includes(key)) {
      return icon
    }
  }
  
  return 'CircleCheck'
}

// 获取设施颜色
const getFacilityColor = (index) => {
  const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#36cfc9']
  return colors[index % colors.length]
}

// 预览图片
const previewImage = (image) => {
  // 可以使用 Element Plus 的图片预览组件
  ElMessage.info('点击查看大图')
  // 这里可以集成图片预览功能
}

onMounted(() => {
  loadStoreDetail()
  loadRooms()
  loadReviews()
})
</script>

<style scoped>
.store-detail-container {
  padding: 20px;
}

.store-images img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.store-basic-info h1 {
  font-size: 32px;
  margin: 0 0 20px;
  color: #333;
}

.store-rating {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 25px;
}

.review-count {
  color: #999;
  font-size: 14px;
}

.store-desc {
  margin-bottom: 20px;
}

.store-tags {
  margin-bottom: 25px;
}

.store-tags .el-tag {
  margin-right: 10px;
  margin-bottom: 10px;
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

/* 门店介绍卡片 */
.store-intro-card {
  background: linear-gradient(135deg, #fff 0%, #f9fafb 100%);
}

/* 章节标题 */
.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e8e8e8;
}

.section-title .el-icon {
  font-size: 20px;
  color: #409eff;
}

/* 门店特色标签 */
.store-highlights {
  margin-bottom: 30px;
}

.highlight-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.highlight-tag {
  padding: 12px 20px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 20px;
  cursor: default;
  transition: all 0.3s;
}

.highlight-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.highlight-tag .el-icon {
  margin-right: 4px;
}

/* 门店设施 */
.store-facilities {
  margin-bottom: 30px;
}

.facility-list {
  margin: 0;
}

.facility-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 10px;
  transition: all 0.3s;
  cursor: default;
}

.facility-item:hover {
  background: #f0f0f0;
  transform: translateX(5px);
}

.facility-item .el-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.facility-item span {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

/* 门店简介 */
.store-description-section {
  margin-bottom: 30px;
}

.store-description {
  line-height: 2;
  color: #666;
  font-size: 15px;
}

.store-description p {
  margin: 0 0 15px;
}

.store-description strong {
  color: #333;
  font-weight: 600;
}

/* 营业信息 */
.store-business-info {
  margin-bottom: 30px;
}

.business-info-grid {
  margin: 0;
}

.info-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #fff 100%);
  border-radius: 12px;
  border: 1px solid #e8e8e8;
  margin-bottom: 15px;
  transition: all 0.3s;
}

.info-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
}

.info-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
  flex-shrink: 0;
}

.info-content {
  flex: 1;
}

.info-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
}

.info-value {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

/* 门店环境图片 */
.store-environment {
  margin-bottom: 0;
}

.environment-gallery {
  margin: 0;
}

.environment-image {
  position: relative;
  height: 150px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  margin-bottom: 15px;
  transition: all 0.3s;
}

.environment-image:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.environment-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.environment-image:hover img {
  transform: scale(1.1);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.environment-image:hover .image-overlay {
  opacity: 1;
}

.image-overlay .el-icon {
  font-size: 32px;
  color: #fff;
}

/* 房间卡片样式 */
.room-card {
  position: relative;
  background: linear-gradient(135deg, #fff 0%, #f9f9f9 100%);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 15px;
  border: 2px solid #e8e8e8;
  transition: all 0.3s ease;
  overflow: hidden;
}

.room-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #409eff;
}

.room-card.room-unavailable {
  opacity: 0.7;
  background: linear-gradient(135deg, #f5f5f5 0%, #e9e9e9 100%);
}

.room-card.room-unavailable:hover {
  transform: none;
  box-shadow: none;
}

/* 房间类型标签 */
.room-type-badge {
  position: absolute;
  top: 0;
  left: 0;
  padding: 6px 16px;
  font-size: 12px;
  font-weight: bold;
  color: #fff;
  border-radius: 0 0 12px 0;
  z-index: 2;
}

.room-type-badge.room-type-1 {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.room-type-badge.room-type-2 {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.room-type-badge.room-type-3 {
  background: linear-gradient(135deg, #e6a23c 0%, #f56c6c 100%);
}

/* 房间状态角标 */
.room-status-corner {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  background: #f5f5f5;
  color: #999;
  z-index: 2;
}

.room-status-corner.available {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: #fff;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.9;
  }
}

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 30px 0 15px;
}

.room-header h4 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.room-info {
  margin-bottom: 15px;
}

.room-info .info-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 10px;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}

.room-info .info-item strong {
  color: #409eff;
  font-size: 16px;
}

.room-info .info-item .el-icon {
  margin-top: 2px;
  flex-shrink: 0;
}

/* 房间预约按钮 */
.room-action {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #e8e8e8;
}

.room-action .el-button {
  width: 100%;
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

/* 距离显示样式 */
.address-with-distance {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.address-with-distance > span {
  line-height: 1.6;
}

.distance-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: default;
  animation: fadeIn 0.3s ease-in;
}

.distance-tag .el-icon {
  font-size: 14px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式布局 */
@media (max-width: 768px) {
  .store-basic-info h1 {
    font-size: 24px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .action-buttons .el-button {
    width: 100%;
  }
  
  .address-with-distance {
    font-size: 14px;
  }
}
</style>
