<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="sidebar">
      <div class="logo">
        <h2>剧本杀管理后台</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <el-sub-menu v-if="route.children && route.children.length > 1" :index="route.path">
            <template #title>
              <el-icon v-if="route.meta?.icon || route.children?.[0]?.meta?.icon">
                <component :is="route.meta?.icon || route.children?.[0]?.meta?.icon" />
              </el-icon>
              <span>{{ route.meta?.title || route.children?.[0]?.meta?.title }}</span>
            </template>
            <template v-for="child in route.children.filter(c => !c.meta?.parent)" :key="child.path">
              <el-sub-menu v-if="route.children.some(sc => sc.meta?.parent === child.path)" :index="route.path + '/' + child.path">
                <template #title>
                  <el-icon><component :is="child.meta.icon" /></el-icon>
                  <span>{{ child.meta.title }}</span>
                </template>
                <!-- 列表本身作为第一个子项 -->
                <el-menu-item :index="route.path + '/' + child.path">
                  <el-icon><component :is="child.meta.icon" /></el-icon>
                  <span>{{ child.meta.title }}</span>
                </el-menu-item>
                <!-- 其余折叠子项，如新增 -->
                <el-menu-item v-for="sub in route.children.filter(sc => sc.meta?.parent === child.path)" :key="sub.path" :index="route.path + '/' + sub.path">
                  <el-icon><component :is="sub.meta.icon || 'Plus'" /></el-icon>
                  <span>{{ sub.meta.title }}</span>
                </el-menu-item>
              </el-sub-menu>
              <el-menu-item v-else :index="route.path + '/' + child.path">
                <el-icon><component :is="child.meta.icon" /></el-icon>
                <span>{{ child.meta.title }}</span>
              </el-menu-item>
            </template>
          </el-sub-menu>
          <el-menu-item v-else :index="route.children && route.children.length === 1 ? (route.path === '/' ? '/' + route.children[0].path : route.path + '/' + route.children[0].path) : route.path">
            <el-icon><component :is="route.meta?.icon || route.children?.[0]?.meta?.icon" /></el-icon>
            <span>{{ route.meta?.title || route.children?.[0]?.meta?.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>管理后台</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <!-- 通知铃铛 -->
          <NotificationBell />
          
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              <el-icon><User /></el-icon>
              <span>管理员</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import NotificationBell from '@/components/NotificationBell.vue'

const router = useRouter()
const route = useRoute()

const menuRoutes = computed(() => {
  return router.options.routes.filter(r => r.path !== '/login').map(route => {
    // 过滤掉标记为 hidden 的子路由
    if (route.children && route.children.length > 0) {
      return {
        ...route,
        children: route.children.filter(child => !child.hidden)
      }
    }
    return route
  })
})

const activeMenu = computed(() => (route.meta && route.meta.activeMenu) ? route.meta.activeMenu : route.path)

const currentTitle = computed(() => route.meta.title || '')

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('admin-token')
    ElMessage.success('退出成功')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4a;
}

.logo h2 {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.sidebar-menu {
  border: none;
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 10px;
}

.user-dropdown span {
  margin: 0 5px;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
