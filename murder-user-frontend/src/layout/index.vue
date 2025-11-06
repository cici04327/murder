<template>
  <div class="layout-container">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo" @click="router.push('/')">
          <el-icon><Location /></el-icon>
          <span>剧本杀在线预约</span>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          :ellipsis="false"
          class="header-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/script">剧本大厅</el-menu-item>
          <el-menu-item index="/store">门店列表</el-menu-item>
        </el-menu>
        
        <!-- 全局搜索 -->
        <GlobalSearch />
        
        <div class="header-right">
          <!-- 主题切换 -->
          <ThemeSwitcher />
          <template v-if="userStore.isLoggedIn">
            <!-- 消息通知 -->
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
              <el-button circle @click="router.push('/user/notifications')">
                <el-icon><Bell /></el-icon>
              </el-button>
            </el-badge>
            
            <!-- 用户菜单 -->
            <el-dropdown @command="handleUserCommand">
              <div class="user-info">
                <el-avatar :src="userStore.avatar" :size="36" />
                <span class="username">{{ userStore.nickname }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="vip">
                    <el-icon><Medal /></el-icon>
                    VIP会员
                  </el-dropdown-item>
                  <el-dropdown-item command="reservations">
                    <el-icon><Calendar /></el-icon>
                    我的预约
                  </el-dropdown-item>
                  <el-dropdown-item command="coupons">
                    <el-icon><Ticket /></el-icon>
                    我的优惠券
                  </el-dropdown-item>
                  <el-dropdown-item command="favorites">
                    <el-icon><Star /></el-icon>
                    我的收藏
                  </el-dropdown-item>
                  <el-dropdown-item command="points">
                    <el-icon><Star /></el-icon>
                    我的积分
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          
          <template v-else>
            <el-button type="primary" @click="router.push('/login')">登录</el-button>
            <el-button @click="router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>
    
    <!-- 主体内容 -->
    <el-main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>
    
    <!-- 底部 -->
    <el-footer class="footer">
      <div class="footer-content">
        <div class="footer-links">
          <a @click="router.push('/about')">关于我们</a>
          <span class="divider">|</span>
          <a @click="router.push('/contact')">联系我们</a>
          <span class="divider">|</span>
          <a @click="router.push('/help')">帮助中心</a>
          <span class="divider">|</span>
          <a @click="router.push('/agreement')">用户协议</a>
        </div>
        <div class="copyright">
          © 2024 剧本杀在线预约平台. All Rights Reserved.
        </div>
      </div>
    </el-footer>
    
    <!-- AI客服 -->
    <AICustomerService />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessageBox } from 'element-plus'
import { Location, Bell, User, Calendar, Ticket, Star, SwitchButton, Medal } from '@element-plus/icons-vue'
import GlobalSearch from '@/components/GlobalSearch.vue'
import AICustomerService from '@/components/AICustomerService.vue'
import ThemeSwitcher from '@/components/ThemeSwitcher.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const unreadCount = ref(0)

const activeMenu = computed(() => {
  return route.path
})

const handleMenuSelect = (index) => {
  router.push(index)
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'vip':
      router.push('/vip')
      break
    case 'reservations':
      router.push('/user/reservations')
      break
    case 'coupons':
      router.push('/user/coupons')
      break
    case 'favorites':
      router.push('/user/favorites')
      break
    case 'points':
      router.push('/user/points')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userStore.logout()
        router.push('/home')
      }).catch(() => {})
      break
  }
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
}

.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 64px;
  position: sticky;
  top: 0;
  z-index: 1000;
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  gap: 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  cursor: pointer;
  user-select: none;
}

.logo:hover {
  opacity: 0.8;
}

.header-menu {
  flex: 1;
  margin: 0 40px;
  border: none;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 200px;
  flex-shrink: 0;
  justify-content: flex-end;
}

.notification-badge {
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 20px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.username {
  font-size: 14px;
  color: #333;
}

.main-content {
  flex: 1;
  padding: 20px;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.footer {
  background: rgba(44, 62, 80, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  color: #fff;
  padding: 30px 0;
  text-align: center;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 -2px 20px rgba(0, 0, 0, 0.1);
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
}

.footer-links {
  margin-bottom: 15px;
}

.footer-links a {
  color: #fff;
  text-decoration: none;
  margin: 0 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.footer-links a:hover {
  color: #409eff;
  text-decoration: underline;
}

.divider {
  margin: 0 5px;
  opacity: 0.5;
}

.copyright {
  font-size: 14px;
  opacity: 0.7;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
