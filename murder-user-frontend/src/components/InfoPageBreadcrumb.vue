<template>
  <div class="breadcrumb-container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">
        <el-icon><HomeFilled /></el-icon>
        首页
      </el-breadcrumb-item>
      <el-breadcrumb-item>{{ currentPage }}</el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { HomeFilled } from '@element-plus/icons-vue'

const route = useRoute()

const currentPage = computed(() => {
  return route.meta.title || '信息页面'
})
</script>

<style scoped>
.breadcrumb-container {
  padding: 15px 20px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.el-breadcrumb {
  font-size: 14px;
}

.el-breadcrumb-item {
  display: flex;
  align-items: center;
  gap: 5px;
}
</style>
