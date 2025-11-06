import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      }
    ]
  },
  {
    path: '/store',
    component: Layout,
    redirect: '/store/list',
    meta: { title: '门店管理', icon: 'Shop' },
    children: [
      {
        path: 'list',
        name: 'StoreList',
        component: () => import('@/views/store/list.vue'),
        meta: { title: '门店列表', icon: 'List' }
      },
      {
        path: 'add',
        name: 'StoreAdd',
        component: () => import('@/views/store/add.vue'),
        meta: { title: '新增门店', activeMenu: '/store/list' },
        hidden: true
      },
      {
        path: 'statistics',
        name: 'StoreStatistics',
        component: () => import('@/views/store/statistics.vue'),
        meta: { title: '门店统计', icon: 'DataAnalysis' }
      },
      {
        path: 'room',
        name: 'StoreRoom',
        component: () => import('@/views/store/room.vue'),
        meta: { title: '房间管理', icon: 'Operation' }
      },
      {
        path: 'review',
        name: 'StoreReview',
        component: () => import('@/views/store/review.vue'),
        meta: { title: '评价管理', icon: 'Comment' }
      }
    ]
  },
  {
    path: '/script',
    component: Layout,
    redirect: '/script/list',
    meta: { title: '剧本管理', icon: 'Reading' },
    children: [
      {
        path: 'list',
        name: 'ScriptList',
        component: () => import('@/views/script/list.vue'),
        meta: { title: '剧本列表', icon: 'List' }
      },
      {
        path: 'add',
        name: 'ScriptAdd',
        component: () => import('@/views/script/add.vue'),
        meta: { title: '新增剧本', activeMenu: '/script/list' },
        hidden: true
      },
      {
        path: 'category',
        name: 'ScriptCategory',
        component: () => import('@/views/script/category.vue'),
        meta: { title: '剧本分类', icon: 'Folder' }
      },
      {
        path: 'review',
        name: 'ScriptReview',
        component: () => import('@/views/script/review.vue'),
        meta: { title: '剧本评价', icon: 'Comment' }
      }
    ]
  },
  {
    path: '/reservation',
    component: Layout,
    redirect: '/reservation/list',
    meta: { title: '预约管理', icon: 'Calendar' },
    children: [
      {
        path: 'list',
        name: 'ReservationList',
        component: () => import('@/views/reservation/list.vue'),
        meta: { title: '预约列表', icon: 'List' }
      },
      {
        path: 'add',
        name: 'ReservationAdd',
        component: () => import('@/views/reservation/add.vue'),
        meta: { title: '新建预约', activeMenu: '/reservation/list' },
        hidden: true
      },
      {
        path: 'detail/:id',
        name: 'ReservationDetail',
        component: () => import('@/views/reservation/detail.vue'),
        meta: { title: '预约详情', activeMenu: '/reservation/list' },
        hidden: true
      },
      {
        path: 'edit/:id',
        name: 'ReservationEdit',
        component: () => import('@/views/reservation/edit.vue'),
        meta: { title: '编辑预约', activeMenu: '/reservation/list' },
        hidden: true
      },
      {
        path: 'refund',
        name: 'ReservationRefund',
        component: () => import('@/views/reservation/refund.vue'),
        meta: { title: '退款管理', icon: 'RefreshLeft' }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    redirect: '/user/list',
    meta: { title: '用户管理', icon: 'User' },
    children: [
      {
        path: 'list',
        name: 'UserList',
        component: () => import('@/views/user/list.vue'),
        meta: { title: '用户列表', icon: 'List' }
      },
      {
        path: 'detail/:id',
        name: 'UserDetail',
        component: () => import('@/views/user/detail.vue'),
        meta: { title: '用户详情' },
        hidden: true
      },
      {
        path: 'add',
        name: 'UserAdd',
        component: () => import('@/views/user/edit.vue'),
        meta: { title: '新增用户' },
        hidden: true
      },
      {
        path: 'edit/:id',
        name: 'UserEdit',
        component: () => import('@/views/user/edit.vue'),
        meta: { title: '编辑用户' },
        hidden: true
      },
      {
        path: 'points/:id',
        name: 'UserPoints',
        component: () => import('@/views/user/points.vue'),
        meta: { title: '积分管理' },
        hidden: true
      },
      {
        path: 'address/:id',
        name: 'UserAddress',
        component: () => import('@/views/user/address.vue'),
        meta: { title: '地址管理' },
        hidden: true
      }
    ]
  },
  {
    path: '/vip',
    component: Layout,
    redirect: '/vip/packages',
    meta: { title: 'VIP管理', icon: 'Medal' },
    children: [
      {
        path: 'packages',
        name: 'VipPackages',
        component: () => import('@/views/vip/packages.vue'),
        meta: { title: 'VIP套餐', icon: 'ShoppingBag' }
      }
    ]
  },
  {
    path: '/coupon',
    component: Layout,
    redirect: '/coupon/list',
    meta: { title: '优惠券管理', icon: 'Ticket' },
    children: [
      {
        path: 'list',
        name: 'CouponList',
        component: () => import('@/views/coupon/list.vue'),
        meta: { title: '优惠券列表', icon: 'List' }
      },
      {
        path: 'user-coupon',
        name: 'UserCoupon',
        component: () => import('@/views/coupon/user-coupon.vue'),
        meta: { title: '用户优惠券', icon: 'Tickets' }
      }
    ]
  },
  {
    path: '/notification',
    component: Layout,
    redirect: '/notification/index',
    meta: { title: '通知中心', icon: 'Bell' },
    children: [
      {
        path: 'index',
        name: 'Notification',
        component: () => import('@/views/notification/index.vue'),
        meta: { title: '通知中心', icon: 'Bell' }
      }
    ]
  },
  {
    path: '/finance',
    component: Layout,
    redirect: '/finance/revenue',
    meta: { title: '财务管理', icon: 'Coin' },
    children: [
      {
        path: 'revenue',
        name: 'FinanceRevenue',
        component: () => import('@/views/finance/revenue.vue'),
        meta: { title: '营收统计', icon: 'TrendCharts' }
      },
      {
        path: 'refund',
        name: 'FinanceRefund',
        component: () => import('@/views/finance/refund.vue'),
        meta: { title: '退款管理', icon: 'RefreshLeft' }
      },
      {
        path: 'report',
        name: 'FinanceReport',
        component: () => import('@/views/finance/report.vue'),
        meta: { title: '财务报表', icon: 'Document' }
      },
      {
        path: 'reconciliation',
        name: 'FinanceReconciliation',
        component: () => import('@/views/finance/reconciliation.vue'),
        meta: { title: '对账管理', icon: 'ScaleToOriginal' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('admin-token')
  
  if (to.path === '/login') {
    next()
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
