<template>
  <div class="user-notifications">
    <el-card class="page-header">
      <div class="header-content">
        <div>
          <h2>消息通知</h2>
          <p class="subtitle">查看您的所有消息通知</p>
        </div>
        <div class="header-actions">
          <el-button @click="handleMarkAllRead" :disabled="unreadCount === 0">
            <el-icon><CircleCheck /></el-icon>
            全部已读
          </el-button>
          <el-button @click="handleClearAll" type="danger" plain>
            <el-icon><Delete /></el-icon>
            清空消息
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 未读消息统计 -->
    <el-card class="unread-stats" v-if="unreadCount > 0">
      <el-alert
        :title="`您有 ${unreadCount} 条未读消息`"
        type="warning"
        :closable="false"
        show-icon
      />
    </el-card>

    <!-- 消息列表 -->
    <el-card class="notification-list-card">
      <template #header>
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="全部消息" name="all">
            <template #label>
              <span>全部消息</span>
              <el-badge v-if="unreadCount > 0" :value="unreadCount" class="tab-badge" />
            </template>
          </el-tab-pane>
          <el-tab-pane label="系统通知" name="system"></el-tab-pane>
          <el-tab-pane label="预约消息" name="reservation"></el-tab-pane>
          <el-tab-pane label="优惠活动" name="promotion"></el-tab-pane>
        </el-tabs>
      </template>

      <div v-loading="loading" class="notification-list">
        <div v-if="notifications.length === 0" class="empty-state">
          <el-empty description="暂无消息"></el-empty>
        </div>

        <div v-else class="notification-items">
          <div
            v-for="notification in notifications"
            :key="notification.id"
            class="notification-item"
            :class="{ 'unread': notification.isRead === 0 }"
            @click="handleNotificationClick(notification)"
          >
            <div class="notification-icon">
              <el-icon :size="24" :color="getNotificationColor(notification.type)">
                <component :is="getNotificationIcon(notification.type)" />
              </el-icon>
            </div>
            <div class="notification-content">
              <div class="notification-header">
                <h4 class="notification-title">{{ notification.title }}</h4>
                <el-tag v-if="notification.isRead === 0" type="danger" size="small">未读</el-tag>
              </div>
              <p class="notification-message">{{ notification.content }}</p>
              <div class="notification-footer">
                <span class="notification-time">{{ formatDateTime(notification.createTime) }}</span>
                <el-tag :type="getTypeTag(notification.type)" size="small">
                  {{ getTypeLabel(notification.type) }}
                </el-tag>
              </div>
            </div>
            <div class="notification-actions">
              <el-button
                v-if="notification.isRead === 0"
                type="primary"
                size="small"
                link
                @click.stop="handleMarkRead(notification.id)"
              >
                标记已读
              </el-button>
              <el-button
                type="danger"
                size="small"
                link
                @click.stop="handleDelete(notification.id)"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <el-pagination
          v-if="total > 0"
          class="pagination"
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 消息详情对话框 -->
    <el-dialog
      v-model="showDetail"
      :title="currentNotification?.title"
      width="600px"
    >
      <div class="notification-detail">
        <div class="detail-header">
          <el-tag :type="getTypeTag(currentNotification?.type)" size="small">
            {{ getTypeLabel(currentNotification?.type) }}
          </el-tag>
          <span class="detail-time">{{ formatDateTime(currentNotification?.createTime) }}</span>
        </div>
        <div class="detail-content">
          {{ currentNotification?.content }}
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetail = false">关闭</el-button>
        <el-button
          v-if="currentNotification?.isRead === 0"
          type="primary"
          @click="handleMarkRead(currentNotification.id, true)"
        >
          标记已读
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  CircleCheck,
  Delete,
  Bell,
  ChatDotSquare,
  ShoppingCart,
  Tickets,
  Warning,
  InfoFilled
} from '@element-plus/icons-vue'
import {
  getUserNotifications,
  markNotificationRead,
  markAllNotificationsRead,
  deleteNotification,
  clearAllNotifications
} from '@/api/user'

// 状态
const loading = ref(false)
const activeTab = ref('all')
const notifications = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const unreadCount = ref(0)

const showDetail = ref(false)
const currentNotification = ref(null)

// 分页处理函数
const handlePageChange = (newPage) => {
  console.log('通知列表页码变化:', newPage)
  currentPage.value = newPage
  loadNotifications()
}

const handleSizeChange = (newSize) => {
  console.log('通知列表每页大小变化:', newSize)
  pageSize.value = newSize
  currentPage.value = 1
  loadNotifications()
}

// 加载通知列表
const loadNotifications = async () => {
  loading.value = true
  try {
    console.log('加载通知列表，参数:', { page: currentPage.value, pageSize: pageSize.value, type: activeTab.value })
    const params = {
      type: activeTab.value === 'all' ? undefined : activeTab.value,
      page: currentPage.value,
      pageSize: pageSize.value
    }
    const res = await getUserNotifications(params)
    if (res.code === 1) {
      notifications.value = res.data.records || []
      total.value = res.data.total || 0
      unreadCount.value = res.data.unreadCount || 0
    }
  } catch (error) {
    console.error('加载通知失败:', error)
    ElMessage.error('加载通知失败')
  } finally {
    loading.value = false
  }
}

// 切换标签
const handleTabChange = () => {
  currentPage.value = 1
  loadNotifications()
}

// 点击通知
const handleNotificationClick = (notification) => {
  currentNotification.value = notification
  showDetail.value = true
  
  // 如果是未读消息，自动标记为已读
  if (notification.isRead === 0) {
    handleMarkRead(notification.id, false)
  }
}

// 标记已读
const handleMarkRead = async (id, closeDialog = false) => {
  try {
    const res = await markNotificationRead(id)
    if (res.code === 1) {
      ElMessage.success('已标记为已读')
      loadNotifications()
      if (closeDialog) {
        showDetail.value = false
      }
    }
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

// 全部已读
const handleMarkAllRead = async () => {
  try {
    await ElMessageBox.confirm('确定要将所有消息标记为已读吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    const res = await markAllNotificationsRead()
    if (res.code === 1) {
      ElMessage.success('已全部标记为已读')
      loadNotifications()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('全部已读失败:', error)
    }
  }
}

// 删除通知
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteNotification(id)
    if (res.code === 1) {
      ElMessage.success('删除成功')
      loadNotifications()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 清空所有消息
const handleClearAll = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有消息吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await clearAllNotifications()
    if (res.code === 1) {
      ElMessage.success('已清空所有消息')
      loadNotifications()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空失败:', error)
    }
  }
}

// 获取通知图标
const getNotificationIcon = (type) => {
  const iconMap = {
    'system': InfoFilled,
    'reservation': ShoppingCart,
    'promotion': Tickets,
    'warning': Warning,
    'message': ChatDotSquare
  }
  return iconMap[type] || Bell
}

// 获取通知颜色
const getNotificationColor = (type) => {
  const colorMap = {
    'system': '#409EFF',
    'reservation': '#67C23A',
    'promotion': '#E6A23C',
    'warning': '#F56C6C',
    'message': '#909399'
  }
  return colorMap[type] || '#909399'
}

// 获取类型标签样式
const getTypeTag = (type) => {
  const tagMap = {
    'system': 'info',
    'reservation': 'success',
    'promotion': 'warning',
    'warning': 'danger',
    'message': ''
  }
  return tagMap[type] || ''
}

// 获取类型标签文本
const getTypeLabel = (type) => {
  const labelMap = {
    'system': '系统通知',
    'reservation': '预约消息',
    'promotion': '优惠活动',
    'warning': '重要提醒',
    'message': '消息'
  }
  return labelMap[type] || '通知'
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  
  // 小于1分钟
  if (diff < 60000) {
    return '刚刚'
  }
  // 小于1小时
  if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`
  }
  // 小于1天
  if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  }
  // 小于7天
  if (diff < 604800000) {
    return `${Math.floor(diff / 86400000)}天前`
  }
  
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 初始化
onMounted(() => {
  loadNotifications()
})
</script>

<style scoped>
.user-notifications {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #303133;
}

.subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

/* 未读统计 */
.unread-stats {
  margin-bottom: 20px;
}

/* 消息列表 */
.notification-list-card :deep(.el-card__header) {
  padding: 20px 20px 0;
}

.tab-badge {
  margin-left: 5px;
}

.notification-list {
  min-height: 400px;
}

.notification-items {
  margin-top: 20px;
}

.notification-item {
  display: flex;
  padding: 20px;
  border-bottom: 1px solid #EBEEF5;
  cursor: pointer;
  transition: all 0.3s;
}

.notification-item:hover {
  background-color: #F5F7FA;
}

.notification-item.unread {
  background-color: #ECF5FF;
}

.notification-item.unread:hover {
  background-color: #D9ECFF;
}

.notification-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #F5F7FA;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.notification-title {
  margin: 0;
  font-size: 16px;
  color: #303133;
  font-weight: bold;
}

.notification-message {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.notification-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

.notification-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-left: 15px;
}

/* 消息详情 */
.notification-detail {
  padding: 20px 0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #EBEEF5;
}

.detail-time {
  font-size: 13px;
  color: #909399;
}

.detail-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
}

/* 空状态 */
.empty-state {
  padding: 60px 0;
  text-align: center;
}

/* 分页 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
