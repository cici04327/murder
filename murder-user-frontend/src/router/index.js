import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

// 导入布局组件
import Layout from '@/layout/index.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'script',
        name: 'ScriptList',
        component: () => import('@/views/script/list.vue'),
        meta: { title: '剧本大厅' }
      },
      {
        path: 'script/:id',
        name: 'ScriptDetail',
        component: () => import('@/views/script/detail-enhanced.vue'),
        meta: { title: '剧本详情' }
      },
      {
        path: 'script/:id/enhanced',
        name: 'ScriptDetailEnhanced',
        component: () => import('@/views/script/detail-enhanced.vue'),
        meta: { title: '剧本详情（增强版）' }
      },
      {
        path: 'store',
        name: 'StoreList',
        component: () => import('@/views/store/list.vue'),
        meta: { title: '门店列表' }
      },
      {
        path: 'store/:id',
        name: 'StoreDetail',
        component: () => import('@/views/store/detail.vue'),
        meta: { title: '门店详情' }
      },
      {
        path: 'reservation/create',
        name: 'CreateReservation',
        component: () => import('@/views/reservation/create.vue'),
        meta: { title: '创建预约', requireAuth: true }
      },
      {
        path: 'reservation/confirm/:id',
        name: 'ConfirmReservation',
        component: () => import('@/views/reservation/confirm.vue'),
        meta: { title: '确认预约', requireAuth: true }
      },
      {
        path: 'reservation/detail/:id',
        name: 'ReservationDetail',
        component: () => import('@/views/reservation/detail.vue'),
        meta: { title: '预约详情', requireAuth: true }
      },
      {
        path: 'payment/:id',
        name: 'Payment',
        component: () => import('@/views/payment/index.vue'),
        meta: { title: '支付', requireAuth: true }
      },
      {
        path: 'payment/result',
        name: 'PaymentResult',
        component: () => import('@/views/payment/result.vue'),
        meta: { title: '支付结果', requireAuth: true }
      },
      {
        path: 'user',
        name: 'UserCenter',
        redirect: '/user/profile',
        meta: { requireAuth: true },
        children: [
          {
            path: 'profile',
            name: 'UserProfile',
            component: () => import('@/views/user/profile-enhanced.vue'),
            meta: { title: '个人中心', requireAuth: true }
          },
          {
            path: 'reservations',
            name: 'UserReservations',
            component: () => import('@/views/user/reservations.vue'),
            meta: { title: '我的预约', requireAuth: true }
          },
          {
            path: 'coupons',
            name: 'UserCoupons',
            component: () => import('@/views/user/coupons.vue'),
            meta: { title: '我的优惠券', requireAuth: true }
          },
          {
            path: 'addresses',
            name: 'UserAddresses',
            component: () => import('@/views/user/addresses.vue'),
            meta: { title: '我的地址', requireAuth: true }
          },
          {
            path: 'points',
            name: 'UserPoints',
            component: () => import('@/views/user/points.vue'),
            meta: { title: '我的积分', requireAuth: true }
          },
          {
            path: 'notifications',
            name: 'UserNotifications',
            component: () => import('@/views/user/notifications.vue'),
            meta: { title: '消息通知', requireAuth: true }
          },
          {
            path: 'favorites',
            name: 'UserFavorites',
            component: () => import('@/views/user/favorites-enhanced.vue'),
            meta: { title: '我的收藏', requireAuth: true }
          },
          {
            path: 'favorites-enhanced',
            name: 'UserFavoritesEnhanced',
            component: () => import('@/views/user/favorites-enhanced.vue'),
            meta: { title: '我的收藏（增强版）', requireAuth: true }
          },
          {
            path: 'settings',
            name: 'UserSettings',
            component: () => import('@/views/user/settings.vue'),
            meta: { title: '账号设置', requireAuth: true }
          }
        ]
      },
      {
        path: 'about',
        name: 'About',
        component: () => import('@/views/info/about.vue'),
        meta: { title: '关于我们' }
      },
      {
        path: 'contact',
        name: 'Contact',
        component: () => import('@/views/info/contact.vue'),
        meta: { title: '联系我们' }
      },
      {
        path: 'help',
        name: 'Help',
        component: () => import('@/views/info/help.vue'),
        meta: { title: '帮助中心' }
      },
      {
        path: 'agreement',
        name: 'Agreement',
        component: () => import('@/views/info/agreement.vue'),
        meta: { title: '用户协议' }
      },
      {
        path: 'search',
        name: 'Search',
        component: () => import('@/views/search/index.vue'),
        meta: { title: '搜索结果' }
      },
      {
        path: 'article',
        name: 'ArticleList',
        component: () => import('@/views/article/list-enhanced.vue'),
        meta: { title: '资讯攻略' }
      },
      {
        path: 'article/:id',
        name: 'ArticleDetail',
        component: () => import('@/views/article/detail-enhanced.vue'),
        meta: { title: '文章详情' }
      },
      {
        path: 'recommend',
        name: 'Recommend',
        component: () => import('@/views/recommend/index.vue'),
        meta: { title: '为你推荐' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  // 滚动行为配置
  scrollBehavior(to, from, savedPosition) {
    // 如果有保存的位置（例如使用浏览器后退按钮）
    if (savedPosition) {
      return savedPosition
    }
    // 如果路由有hash，滚动到hash位置
    if (to.hash) {
      return {
        el: to.hash,
        behavior: 'smooth'
      }
    }
    // 默认滚动到顶部
    return {
      top: 0,
      behavior: 'smooth'
    }
  }
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 剧本杀在线预约平台` : '剧本杀在线预约平台'
  
  // 需要登录的页面
  if (to.meta.requireAuth) {
    if (!userStore.isLoggedIn) {
      ElMessage.warning('请先登录')
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
  }
  
  next()
})

// 路由切换后的处理
router.afterEach((to, from) => {
  // 确保页面滚动到顶部（兼容处理）
  setTimeout(() => {
    window.scrollTo({
      top: 0,
      left: 0,
      behavior: 'instant' // 立即滚动，不使用动画
    })
  }, 0)
})

export default router
