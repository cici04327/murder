<template>
  <div class="home-container">
    <!-- 骨架屏 -->
    <SkeletonHome v-if="pageLoading" />
    
    <!-- 真实内容 -->
    <div v-else class="home-content">
    <!-- 轮播图 -->
    <el-carousel height="450px" class="banner" :interval="5000" arrow="always">
      <el-carousel-item v-for="item in banners" :key="item.id">
        <div class="banner-item" :style="{ backgroundImage: `url(${item.image})` }">
          <div class="banner-content">
            <div class="banner-badge" v-if="item.badge">{{ item.badge }}</div>
            <h2>{{ item.title }}</h2>
            <p>{{ item.description }}</p>
            <el-button type="primary" size="large" @click="router.push(item.link)" round>
              {{ item.buttonText }}
              <el-icon class="el-icon--right"><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 数据统计展示 -->
    <div class="statistics-section">
      <el-row :gutter="20" v-loading="statisticsLoading">
        <el-col :xs="12" :sm="6" v-for="stat in statistics" :key="stat.id">
          <div class="stat-card">
            <div class="stat-icon" :style="{ background: stat.color }">
              <el-icon :size="28">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 快速入口 -->
    <div class="quick-entry">
      <el-row :gutter="20">
        <el-col :xs="12" :sm="6" v-for="entry in quickEntries" :key="entry.id">
          <div class="entry-card" @click="router.push(entry.path)">
            <el-icon :size="40" :color="entry.color">
              <component :is="entry.icon" />
            </el-icon>
            <div class="entry-title">{{ entry.title }}</div>
            <div class="entry-desc">{{ entry.description }}</div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 热门剧本 - 自动播放 -->
    <div class="section">
      <div class="section-header">
        <div class="header-left">
          <h3>🔥 热门剧本</h3>
          <span class="header-subtitle">精选高评分剧本</span>
        </div>
        <div class="header-right">
          <el-button 
            :icon="autoPlayPaused ? 'VideoPlay' : 'VideoPause'" 
            circle 
            size="small"
            @click="toggleAutoPlay"
            :title="autoPlayPaused ? '开始自动播放' : '暂停自动播放'"
          />
          <el-link type="primary" @click="router.push('/script')">
            查看更多 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-link>
        </div>
      </div>
      <div 
        class="scroll-container auto-scroll" 
        ref="hotScriptsContainer"
        v-loading="hotScriptsLoading"
        @mouseenter="pauseAutoPlay"
        @mouseleave="resumeAutoPlay"
      >
        <div class="scroll-wrapper" :style="{ transform: `translateX(-${scrollOffset}px)` }">
          <!-- 原始剧本列表 -->
          <div class="script-card-horizontal" v-for="script in hotScripts" :key="'original-' + script.id" @click="router.push(`/script/${script.id}`)">
            <div class="script-image">
              <LazyImage
                :src="script.coverImage || getScriptCover(script.name, script.categoryName)"
                :alt="script.name"
                :height="200"
                :immediate="true"
                @error="handleImageError"
              />
              <div class="script-tag">{{ script.categoryName }}</div>
              <div class="hot-badge">HOT</div>
            </div>
            <div class="script-info">
              <h4>{{ script.name }}</h4>
              <div class="script-meta">
                <el-tag size="small">{{ getDifficultyText(script.difficulty) }}</el-tag>
                <el-tag size="small" type="info">{{ script.playerCount }}人</el-tag>
                <el-tag size="small" type="warning">{{ script.duration }}小时</el-tag>
              </div>
              <div class="script-rating">
                <el-rate v-model="script.rating" disabled show-score size="small" text-color="#ff9900" />
              </div>
              <div class="script-price">
                ¥<span class="price-value">{{ script.price }}</span>/人
              </div>
            </div>
          </div>
          <!-- 克隆的剧本列表（用于无缝循环） -->
          <div class="script-card-horizontal" v-for="script in hotScripts" :key="'clone-' + script.id" @click="router.push(`/script/${script.id}`)">
            <div class="script-image">
              <LazyImage
                :src="script.coverImage || getScriptCover(script.name, script.categoryName)"
                :alt="script.name"
                :height="200"
                :immediate="true"
                @error="handleImageError"
              />
              <div class="script-tag">{{ script.categoryName }}</div>
              <div class="hot-badge">HOT</div>
            </div>
            <div class="script-info">
              <h4>{{ script.name }}</h4>
              <div class="script-meta">
                <el-tag size="small">{{ getDifficultyText(script.difficulty) }}</el-tag>
                <el-tag size="small" type="info">{{ script.playerCount }}人</el-tag>
                <el-tag size="small" type="warning">{{ script.duration }}小时</el-tag>
              </div>
              <div class="script-rating">
                <el-rate v-model="script.rating" disabled show-score size="small" text-color="#ff9900" />
              </div>
              <div class="script-price">
                ¥<span class="price-value">{{ script.price }}</span>/人
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 个性化推荐 -->
    <div class="section recommendation-section">
      <div class="section-header">
        <div class="header-left">
          <h3>💎 为你推荐</h3>
          <span class="header-subtitle">根据你的喜好精选</span>
        </div>
        <el-link type="primary" @click="router.push('/recommend')">
          查看更多 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
        </el-link>
      </div>
      <el-row :gutter="20" v-loading="recommendedScriptsLoading">
        <el-col :xs="24" :sm="12" :md="6" v-for="script in recommendedScripts" :key="script.id">
          <div class="script-card" @click="router.push(`/script/${script.id}`)">
            <div class="script-image">
              <LazyImage
                :src="script.coverImage || getScriptCover(script.name, script.categoryName)"
                :alt="script.name"
                :height="220"
                :immediate="true"
                @error="handleImageError"
              />
              <div class="script-tag">{{ script.categoryName }}</div>
            </div>
            <div class="script-info">
              <h4>{{ script.name }}</h4>
              <div class="script-meta">
                <el-tag size="small">{{ getDifficultyText(script.difficulty) }}</el-tag>
                <el-tag size="small" type="info">{{ script.playerCount }}人</el-tag>
                <el-tag size="small" type="warning">{{ script.duration }}小时</el-tag>
              </div>
              <div class="script-rating">
                <el-rate v-model="script.rating" disabled show-score size="small" />
              </div>
              <div class="script-price">
                ¥{{ script.price }}/人
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 推荐门店 -->
    <div class="section">
      <div class="section-header">
        <div class="header-left">
          <h3>🏪 推荐门店</h3>
          <span class="header-subtitle">优质商家精选</span>
        </div>
        <el-link type="primary" @click="router.push('/store')">
          查看更多 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
        </el-link>
      </div>
      <el-row :gutter="20" v-loading="hotStoresLoading">
        <el-col :xs="24" :sm="12" :md="8" v-for="store in hotStores.slice(0, 6)" :key="store.id">
          <div class="store-card" @click="router.push(`/store/${store.id}`)">
            <div class="store-image">
              <LazyImage :src="store.coverImage || PLACEHOLDERS.STORE" :alt="store.name" :height="180" :immediate="true" />
              <div class="store-badge" v-if="store.rating >= 4.5">优质门店</div>
            </div>
            <div class="store-info">
              <h4>{{ store.name }}</h4>
              <div class="store-address">
                <el-icon><Location /></el-icon>
                {{ store.address }}
              </div>
              <div class="store-rating">
                <el-rate v-model="store.rating" disabled show-score size="small" />
                <span class="review-count">({{ store.reviewCount }}条评价)</span>
              </div>
              <div class="store-tags">
                <el-tag size="small" v-for="tag in store.tags" :key="tag">{{ tag }}</el-tag>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 最新资讯/攻略 -->
    <div class="section news-section">
      <div class="section-header">
        <div class="header-left">
          <h3>📰 最新资讯</h3>
          <span class="header-subtitle">剧本攻略 · 行业动态</span>
        </div>
        <el-link type="primary" @click="router.push('/article')">
          查看更多 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
        </el-link>
      </div>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6" v-for="news in newsList" :key="news.id">
          <div class="news-card" @click="handleNewsClick(news)">
            <div class="news-image">
              <LazyImage :src="news.image" :alt="news.title" :height="150" :immediate="true" />
              <div class="news-category">{{ news.category }}</div>
            </div>
            <div class="news-content">
              <h4>{{ news.title }}</h4>
              <p class="news-summary">{{ news.summary }}</p>
              <div class="news-meta">
                <span class="news-date">{{ news.date }}</span>
                <span class="news-views">
                  <el-icon><View /></el-icon>
                  {{ news.views }}
                </span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import SkeletonHome from '@/components/Skeleton/SkeletonHome.vue'
import LazyImage from '@/components/LazyImage.vue'
import { useRouter } from 'vue-router'
import { getHotScripts, getRecommendedScripts } from '@/api/script'
import { PLACEHOLDERS } from '@/assets/placeholders'
import { getScriptCover } from '@/assets/script-covers'
import { getHotStores } from '@/api/store'
import { getRecommendedArticles } from '@/api/article'
import request from '@/utils/request'

const router = useRouter()

// 自动播放相关
const hotScriptsContainer = ref(null)
const scrollOffset = ref(0)
const autoPlayTimer = ref(null)
const autoPlayPaused = ref(false)
const scrollSpeed = 1 // 每帧滚动的像素数
const cardWidth = 300 // 卡片宽度（280px + 20px gap）

const banners = ref([
  {
    id: 1,
    title: '探索精彩剧本世界',
    description: '海量优质剧本，总有一款适合你',
    image: PLACEHOLDERS.POSTER1,
    link: '/script',
    buttonText: '立即探索',
    badge: '热门推荐'
  },
  {
    id: 2,
    title: '寻找附近门店',
    description: '优质门店，舒适环境，专业服务',
    image: PLACEHOLDERS.POSTER2,
    link: '/store',
    buttonText: '查看门店',
    badge: '精选商家'
  },
  {
    id: 3,
    title: '在线预约，快速便捷',
    description: '随时随地预约，享受优惠',
    image: PLACEHOLDERS.POSTER3,
    link: '/reservation/create',
    buttonText: '立即预约',
    badge: '限时优惠'
  },
  {
    id: 4,
    title: '新剧本上线',
    description: '《时空裂痕》震撼来袭，立即体验',
    image: PLACEHOLDERS.POSTER4,
    link: '/script',
    buttonText: '查看详情',
    badge: '新品首发'
  }
])

const quickEntries = ref([
  {
    id: 1,
    title: '剧本大厅',
    description: '海量剧本',
    icon: 'Reading',
    color: '#409eff',
    path: '/script'
  },
  {
    id: 2,
    title: '门店列表',
    description: '精选门店',
    icon: 'Shop',
    color: '#67c23a',
    path: '/store'
  },
  {
    id: 3,
    title: '在线预约',
    description: '快速预约',
    icon: 'Calendar',
    color: '#e6a23c',
    path: '/reservation/create'
  },
  {
    id: 4,
    title: '我的优惠券',
    description: '优惠多多',
    icon: 'Ticket',
    color: '#f56c6c',
    path: '/user/coupons'
  }
])

const statistics = ref([
  {
    id: 1,
    label: '累计服务人次',
    value: '10000+',
    icon: 'User',
    color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    id: 2,
    label: '精选剧本',
    value: '500+',
    icon: 'Reading',
    color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    id: 3,
    label: '合作门店',
    value: '100+',
    icon: 'Shop',
    color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    id: 4,
    label: '用户好评',
    value: '98%',
    icon: 'Star',
    color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
])

// 页面整体加载状态
const pageLoading = ref(true)

const statisticsLoading = ref(false)
const hotScripts = ref([])
const hotScriptsLoading = ref(false)
const recommendedScripts = ref([])
const recommendedScriptsLoading = ref(false)
const hotStores = ref([])
const hotStoresLoading = ref(false)

const newsList = ref([
  {
    id: 1,
    title: '剧本杀新手入门指南',
    summary: '从零开始，带你了解剧本杀的魅力',
    category: '新手攻略',
    image: 'https://via.placeholder.com/300x200/667eea/ffffff?text=新手指南',
    date: '2024-01-15',
    views: 1234
  },
  {
    id: 2,
    title: '如何选择适合自己的剧本',
    summary: '根据难度、类型、人数选择最适合的剧本',
    category: '选本技巧',
    image: 'https://via.placeholder.com/300x200/f093fb/ffffff?text=选本技巧',
    date: '2024-01-14',
    views: 856
  },
  {
    id: 3,
    title: '2024年度热门剧本排行榜',
    summary: '盘点本年度最受欢迎的十大剧本',
    category: '榜单推荐',
    image: 'https://via.placeholder.com/300x200/4facfe/ffffff?text=热门榜单',
    date: '2024-01-13',
    views: 2341
  },
  {
    id: 4,
    title: '剧本杀行业发展趋势分析',
    summary: '探讨剧本杀行业的未来发展方向',
    category: '行业动态',
    image: 'https://via.placeholder.com/300x200/43e97b/ffffff?text=行业动态',
    date: '2024-01-12',
    views: 678
  }
])

// 难度映射
const difficultyMap = {
  1: '简单',
  2: '普通',
  3: '困难',
  4: '硬核'
}

// 获取难度文本
const getDifficultyText = (difficulty) => {
  return difficultyMap[difficulty] || difficulty
}

// 图片加载失败处理（LazyImage 组件不传递 event 对象）
const handleImageError = () => {
  // LazyImage 组件内部已经处理了错误显示，这里不需要做任何操作
  console.log('图片加载失败，已使用默认图片')
}

// 加载统计数据
const loadStatistics = async () => {
  statisticsLoading.value = true
  try {
    const res = await request({
      url: '/statistics/overview',
      method: 'get'
    })
    if (res.data) {
      // 更新统计数据
      if (res.data.totalUsers) {
        statistics.value[0].value = res.data.totalUsers >= 10000 ? `${Math.floor(res.data.totalUsers / 1000)}k+` : `${res.data.totalUsers}+`
      }
      if (res.data.totalScripts) {
        statistics.value[1].value = `${res.data.totalScripts}+`
      }
      if (res.data.totalStores) {
        statistics.value[2].value = `${res.data.totalStores}+`
      }
      if (res.data.satisfactionRate) {
        statistics.value[3].value = `${res.data.satisfactionRate}%`
      }
    }
  } catch (error) {
    // 静默失败，使用默认模拟数据
    console.log('使用默认统计数据')
  } finally {
    statisticsLoading.value = false
  }
}

const loadHotScripts = async () => {
  hotScriptsLoading.value = true
  try {
    const res = await getHotScripts()
    if (res.data) {
      hotScripts.value = res.data.slice(0, 8)
    }
  } catch (error) {
    console.error('加载热门剧本失败:', error)
    // 使用模拟数据（带精美封面图片）
    hotScripts.value = [
      {
        id: 1,
        name: '迷雾庄园',
        categoryName: '本格推理',
        difficulty: 2,
        playerCount: 6,
        duration: 4,
        price: 88,
        rating: 4.5,
        coverImage: 'https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=1200&auto=format&fit=crop' // 神秘庄园
      },
      {
        id: 2,
        name: '时光旅人',
        categoryName: '情感沉浸',
        difficulty: 1,
        playerCount: 5,
        duration: 3,
        price: 68,
        rating: 4.8,
        coverImage: 'https://images.unsplash.com/photo-1501139083538-0139583c060f?q=80&w=1200&auto=format&fit=crop' // 怀旧时钟
      },
      {
        id: 3,
        name: '末日余晖',
        categoryName: '科幻机制',
        difficulty: 3,
        playerCount: 7,
        duration: 5,
        price: 108,
        rating: 4.6,
        coverImage: 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?q=80&w=1200&auto=format&fit=crop' // 地球太空
      },
      {
        id: 4,
        name: '古堡迷踪',
        categoryName: '恐怖惊悚',
        difficulty: 2,
        playerCount: 6,
        duration: 4,
        price: 98,
        rating: 4.7,
        coverImage: 'https://images.unsplash.com/photo-1518562923427-c0ff595c8072?q=80&w=1200&auto=format&fit=crop' // 恐怖古堡
      },
      {
        id: 5,
        name: '星际穿越',
        categoryName: '科幻机制',
        difficulty: 3,
        playerCount: 6,
        duration: 5,
        price: 118,
        rating: 4.9,
        coverImage: 'https://images.unsplash.com/photo-1446776653964-20c1d3a81b06?q=80&w=1200&auto=format&fit=crop' // 星空宇宙
      },
      {
        id: 6,
        name: '云中谜案',
        categoryName: '本格推理',
        difficulty: 2,
        playerCount: 7,
        duration: 4,
        price: 98,
        rating: 4.6,
        coverImage: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=1200&auto=format&fit=crop' // 中式古典建筑
      },
      {
        id: 7,
        name: '时间囚徒',
        categoryName: '情感沉浸',
        difficulty: 2,
        playerCount: 5,
        duration: 3,
        price: 78,
        rating: 4.7,
        coverImage: 'https://images.unsplash.com/photo-1495364141860-b0d03eccd065?q=80&w=1200&auto=format&fit=crop' // 沙漏与时间
      },
      {
        id: 8,
        name: '幽灵旅馆',
        categoryName: '恐怖惊悚',
        difficulty: 3,
        playerCount: 6,
        duration: 4,
        price: 108,
        rating: 4.8,
        coverImage: 'https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?q=80&w=1200&auto=format&fit=crop' // 废弃酒店
      }
    ]
  } finally {
    hotScriptsLoading.value = false
  }
}

const loadRecommendedScripts = async () => {
  recommendedScriptsLoading.value = true
  try {
    const res = await getRecommendedScripts()
    if (res.data) {
      recommendedScripts.value = res.data.slice(0, 4)
    }
  } catch (error) {
    // 静默失败，使用模拟数据（带精美封面图片）
    console.log('使用默认推荐剧本数据')
    recommendedScripts.value = [
      {
        id: 11,
        name: '红楼梦境',
        categoryName: '情感沉浸',
        difficulty: 2,
        playerCount: 6,
        duration: 4,
        price: 88,
        rating: 4.7,
        coverImage: 'https://images.unsplash.com/photo-1528127269322-539801943592?q=80&w=1200&auto=format&fit=crop' // 中国古典园林
      },
      {
        id: 12,
        name: '深海秘境',
        categoryName: '冒险探索',
        difficulty: 2,
        playerCount: 5,
        duration: 3,
        price: 78,
        rating: 4.5,
        coverImage: 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?q=80&w=1200&auto=format&fit=crop' // 深海潜水
      },
      {
        id: 13,
        name: '黑暗骑士',
        categoryName: '机制对抗',
        difficulty: 3,
        playerCount: 7,
        duration: 5,
        price: 98,
        rating: 4.6,
        coverImage: 'https://images.unsplash.com/photo-1599423300746-b62533397364?q=80&w=1200&auto=format&fit=crop' // 中世纪盔甲
      },
      {
        id: 14,
        name: '魔法学院',
        categoryName: '欢乐互动',
        difficulty: 1,
        playerCount: 6,
        duration: 3,
        price: 68,
        rating: 4.8,
        coverImage: 'https://images.unsplash.com/photo-1519791883288-dc8bd696e667?q=80&w=1200&auto=format&fit=crop' // 图书馆魔法书
      }
    ]
  } finally {
    recommendedScriptsLoading.value = false
  }
}

const loadHotStores = async () => {
  hotStoresLoading.value = true
  try {
    const res = await getHotStores()
    if (res.data) {
      hotStores.value = res.data.slice(0, 3)
    }
  } catch (error) {
    console.error('加载推荐门店失败:', error)
    // 使用模拟数据
    hotStores.value = [
      {
        id: 1,
        name: '探案密室',
        address: '北京市朝阳区xxx街道xxx号',
        rating: 4.8,
        reviewCount: 128,
        tags: ['环境优雅', '服务专业', '交通便利'],
        coverImage: 'https://images.unsplash.com/photo-1593642532454-e138e28a63f4?q=80&w=1200&auto=format&fit=crop'
      },
      {
        id: 2,
        name: '时空剧本馆',
        address: '北京市海淀区xxx街道xxx号',
        rating: 4.6,
        reviewCount: 96,
        tags: ['剧本丰富', '价格实惠', '氛围好'],
        coverImage: 'https://images.unsplash.com/photo-1481277542470-605612bd2d61?q=80&w=1200&auto=format&fit=crop'
      },
      {
        id: 3,
        name: '沉浸式体验馆',
        address: '北京市东城区xxx街道xxx号',
        rating: 4.9,
        reviewCount: 156,
        tags: ['装修精美', 'DM专业', '停车方便'],
        coverImage: 'https://images.unsplash.com/photo-1524758631624-e2822e304c36?q=80&w=1200&auto=format&fit=crop'
      },
      {
        id: 4,
        name: '推理殿堂',
        address: '北京市西城区xxx街道xxx号',
        rating: 4.7,
        reviewCount: 142,
        tags: ['书香氛围', '推理专场', '咖啡厅'],
        coverImage: 'https://images.unsplash.com/photo-1495364141860-b0d03eccd065?q=80&w=1200&auto=format&fit=crop'
      },
      {
        id: 5,
        name: '剧本杀公馆',
        address: '北京市丰台区xxx街道xxx号',
        rating: 4.9,
        reviewCount: 189,
        tags: ['欧式风格', '高端品质', 'VIP包间'],
        coverImage: 'https://images.unsplash.com/photo-1556909212-d5b604d0c90d?q=80&w=1200&auto=format&fit=crop'
      },
      {
        id: 6,
        name: '谜境体验店',
        address: '北京市石景山区xxx街道xxx号',
        rating: 4.5,
        reviewCount: 117,
        tags: ['包间齐全', '服务周到', '停车方便'],
        coverImage: 'https://images.unsplash.com/photo-1514933651103-005eec06c04b?q=80&w=1200&auto=format&fit=crop'
      }
    ]
  } finally {
    hotStoresLoading.value = false
  }
}

// 加载资讯列表
const loadNews = async () => {
  try {
    const res = await getRecommendedArticles(4)
    if (res.code === 1 || res.code === 200) {
      newsList.value = res.data.map(article => ({
        id: article.id,
        title: article.title,
        summary: article.summary,
        category: article.categoryName,
        image: article.coverImage || PLACEHOLDERS.ARTICLE,
        date: article.publishTime ? new Date(article.publishTime).toLocaleDateString('zh-CN') : '',
        views: article.viewCount
      }))
    }
  } catch (error) {
    console.log('使用默认资讯数据')
  }
}

// 处理资讯点击
const handleNewsClick = (news) => {
  router.push(`/article/${news.id}`)
}

// 自动播放函数
const startAutoPlay = () => {
  if (autoPlayTimer.value) return
  
  autoPlayTimer.value = setInterval(() => {
    if (autoPlayPaused.value) return
    
    scrollOffset.value += scrollSpeed
    
    // 计算一半内容的宽度（因为我们复制了一份）
    const halfWidth = hotScripts.value.length * cardWidth
    
    // 当滚动到一半时，重置到起点（无缝循环）
    if (scrollOffset.value >= halfWidth) {
      scrollOffset.value = 0
    }
  }, 16) // 约60fps
}

const stopAutoPlay = () => {
  if (autoPlayTimer.value) {
    clearInterval(autoPlayTimer.value)
    autoPlayTimer.value = null
  }
}

const toggleAutoPlay = () => {
  autoPlayPaused.value = !autoPlayPaused.value
  if (autoPlayPaused.value) {
    stopAutoPlay()
  } else {
    startAutoPlay()
  }
}

const pauseAutoPlay = () => {
  if (!autoPlayPaused.value) {
    stopAutoPlay()
  }
}

const resumeAutoPlay = () => {
  if (!autoPlayPaused.value) {
    startAutoPlay()
  }
}

onMounted(async () => {
  try {
    // 并行加载所有数据
    await Promise.all([
      loadStatistics(),
      loadHotScripts(),
      loadRecommendedScripts(),
      loadHotStores(),
      loadNews()
    ])
    
    // 数据加载完成，关闭骨架屏
    pageLoading.value = false
    
    // 启动自动播放
    setTimeout(() => {
      if (hotScripts.value.length > 0) {
        startAutoPlay()
      }
    }, 500)
  } catch (error) {
    console.error('页面数据加载失败:', error)
    // 即使加载失败也关闭骨架屏
    pageLoading.value = false
  }
})

onBeforeUnmount(() => {
  stopAutoPlay()
})
</script>

<style scoped>
.home-container {
  padding: 0;
  background: #f5f7fa;
}

.banner {
  margin-bottom: 0;
  border-radius: 0;
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.banner-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.banner-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
}

.banner-content {
  position: relative;
  text-align: center;
  color: #fff;
  z-index: 1;
  max-width: 800px;
  padding: 0 20px;
}

.banner-badge {
  display: inline-block;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 14px;
  margin-bottom: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.banner-content h2 {
  font-size: 52px;
  margin-bottom: 20px;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.5);
  font-weight: 700;
  letter-spacing: 2px;
}

.banner-content p {
  font-size: 22px;
  margin-bottom: 35px;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
  opacity: 0.95;
}

/* 数据统计区域 */
.statistics-section {
  margin: 40px auto 40px;
  max-width: 1200px;
  padding: 0 20px;
  position: relative;
  z-index: 1;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 25px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  cursor: default;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 快速入口 */
.quick-entry {
  margin-bottom: 60px;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
  padding: 0 20px;
}

.entry-card {
  background: #fff;
  border-radius: 12px;
  padding: 30px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  height: 100%;
}

.entry-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
}

.entry-title {
  font-size: 18px;
  font-weight: bold;
  margin: 15px 0 8px;
  color: #333;
}

.entry-desc {
  font-size: 14px;
  color: #999;
}

/* 章节通用样式 */
.section {
  margin-bottom: 60px;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
  padding: 0 20px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 25px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.section-header h3 {
  font-size: 26px;
  color: #333;
  margin: 0;
  font-weight: 700;
}

.header-subtitle {
  font-size: 14px;
  color: #999;
}

/* 横向滚动容器 */
.scroll-container {
  position: relative;
  overflow-x: auto;
  overflow-y: hidden;
  padding-bottom: 10px;
}

.scroll-container::-webkit-scrollbar {
  height: 8px;
}

.scroll-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.scroll-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.scroll-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 自动播放容器 */
.scroll-container.auto-scroll {
  overflow: hidden;
}

.scroll-wrapper {
  display: flex;
  gap: 20px;
  padding-bottom: 5px;
  transition: transform 0.3s linear;
}

.auto-scroll .scroll-wrapper {
  transition: none;
}

/* 右侧控制区域 */
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 横向剧本卡片 */
.script-card-horizontal {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
  width: 280px;
}

.script-card-horizontal:hover {
  transform: translateY(-8px);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.15);
}

/* 普通剧本卡片 */
.script-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.script-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.15);
}

.script-image {
  position: relative;
  height: 280px;
  overflow: hidden;
  background: #f5f5f5;
}

.script-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.script-card:hover .script-image img,
.script-card-horizontal:hover .script-image img {
  transform: scale(1.1);
}

.script-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.75);
  backdrop-filter: blur(5px);
  color: #fff;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.hot-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  color: #fff;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: bold;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.script-info {
  padding: 18px;
}

.script-info h4 {
  margin: 0 0 12px;
  font-size: 17px;
  color: #333;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.script-meta {
  margin-bottom: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.script-meta .el-tag {
  margin: 0;
}

.script-rating {
  margin-bottom: 12px;
}

.script-price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.price-value {
  font-size: 24px;
}

/* 推荐区域特殊样式 */
.recommendation-section {
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  padding: 40px 20px;
  border-radius: 16px;
  margin-bottom: 60px;
}

/* 门店卡片 */
.store-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.store-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.15);
}

.store-image {
  position: relative;
  height: 220px;
  overflow: hidden;
  background: #f5f5f5;
}

.store-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.store-card:hover .store-image img {
  transform: scale(1.1);
}

.store-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: bold;
}

.store-info {
  padding: 18px;
}

.store-info h4 {
  margin: 0 0 12px;
  font-size: 17px;
  color: #333;
  font-weight: 600;
}

.store-address {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.store-rating {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.review-count {
  font-size: 13px;
  color: #999;
}

.store-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.store-tags .el-tag {
  margin: 0;
}

/* 资讯区域 */
.news-section {
  background: #fff;
  padding: 40px 20px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.news-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e8e8e8;
}

.news-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
}

.news-image {
  position: relative;
  height: 180px;
  overflow: hidden;
  background: #f5f5f5;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.news-card:hover .news-image img {
  transform: scale(1.1);
}

.news-category {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(64, 158, 255, 0.9);
  color: #fff;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
}

.news-content {
  padding: 16px;
}

.news-content h4 {
  margin: 0 0 10px;
  font-size: 16px;
  color: #333;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.news-summary {
  font-size: 14px;
  color: #666;
  margin: 0 0 12px;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.news-views {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner {
    height: 350px;
    margin-bottom: 30px;
  }
  
  .banner-content h2 {
    font-size: 32px;
  }
  
  .banner-content p {
    font-size: 16px;
  }
  
  .statistics-section {
    margin-top: 30px;
    margin-bottom: 30px;
  }
  
  .stat-card {
    padding: 20px;
    margin-bottom: 15px;
  }
  
  .stat-value {
    font-size: 24px;
  }
  
  .section-header h3 {
    font-size: 22px;
  }
  
  .script-card-horizontal {
    width: 240px;
  }
  
  .quick-entry {
    margin-bottom: 40px;
  }
  
  .entry-card {
    padding: 25px 15px;
    margin-bottom: 15px;
  }
}
</style>
