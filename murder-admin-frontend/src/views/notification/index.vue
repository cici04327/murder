<template>
  <div class="notification-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">
            <el-icon><Bell /></el-icon>
            通知中心
          </span>
          <div class="header-actions">
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="badge">
              <el-tag>未读通知</el-tag>
            </el-badge>
            <el-button 
              type="primary" 
              size="small" 
              @click="handleMarkAllRead"
              :disabled="unreadCount === 0"
            >
              全部标记为已读
            </el-button>
          </div>
        </div>
      </template>

      <!-- 筛选和搜索 -->
      <div class="filter-bar">
        <div class="filter-left">
          <el-radio-group v-model="filterType" @change="handleFilterChange">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="unread">未读</el-radio-button>
            <el-radio-button label="read">已读</el-radio-button>
          </el-radio-group>
        </div>
        <div class="filter-right">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索通知标题或内容"
            style="width: 300px; margin-right: 10px;"
            clearable
            @clear="handleSearchClear"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleSearchClear">重置</el-button>
        </div>
      </div>
      
      <!-- 批量操作和统计 -->
      <div class="batch-actions" v-if="selectedRows.length > 0 || showStatistics">
        <div class="batch-left">
          <el-tag v-if="selectedRows.length > 0" type="info">
            已选择 {{ selectedRows.length }} 项
          </el-tag>
          <el-button
            v-if="selectedRows.length > 0"
            type="primary"
            size="small"
            @click="handleBatchMarkRead"
          >
            批量标记已读
          </el-button>
          <el-button
            v-if="selectedRows.length > 0"
            type="danger"
            size="small"
            @click="handleBatchDelete"
          >
            批量删除
          </el-button>
          <el-button
            v-if="selectedRows.length > 0"
            size="small"
            @click="handleClearSelection"
          >
            取消选择
          </el-button>
        </div>
        <div class="batch-right">
          <el-button
            type="warning"
            size="small"
            @click="handleClearRead"
          >
            清空已读通知
          </el-button>
          <el-button
            size="small"
            @click="toggleStatistics"
          >
            <el-icon><DataAnalysis /></el-icon>
            {{ showStatistics ? '隐藏统计' : '显示统计' }}
          </el-button>
        </div>
      </div>
      
      <!-- 统计信息卡片 -->
      <div v-if="showStatistics" class="statistics-panel">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-icon" style="background: #409EFF;">
                  <el-icon :size="24"><Bell /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ statistics.totalCount || 0 }}</div>
                  <div class="stat-label">总通知数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-icon" style="background: #F56C6C;">
                  <el-icon :size="24"><Warning /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ statistics.unreadCount || 0 }}</div>
                  <div class="stat-label">未读通知</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-icon" style="background: #67C23A;">
                  <el-icon :size="24"><CircleCheck /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ statistics.readCount || 0 }}</div>
                  <div class="stat-label">已读通知</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-icon" style="background: #E6A23C;">
                  <el-icon :size="24"><Clock /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ statistics.todayCount || 0 }}</div>
                  <div class="stat-label">今日新增</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 类型统计 -->
        <el-card shadow="hover" style="margin-top: 15px;" v-if="statistics.typeCount">
          <template #header>
            <span>通知类型分布</span>
          </template>
          <div class="type-statistics">
            <div 
              v-for="(count, type) in statistics.typeCount" 
              :key="type"
              class="type-item"
            >
              <el-tag>{{ type }}</el-tag>
              <span class="type-count">{{ count }} 条</span>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 通知列表 -->
      <el-table 
        :data="notificationList" 
        style="width: 100%" 
        v-loading="loading"
        @row-click="handleRowClick"
        @selection-change="handleSelectionChange"
        :row-class-name="tableRowClassName"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-badge is-dot :hidden="row.isRead === 1">
              <el-icon :size="20" :color="row.isRead === 0 ? '#409EFF' : '#909399'">
                <Bell />
              </el-icon>
            </el-badge>
          </template>
        </el-table-column>

        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">{{ row.typeName }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="标题" prop="title" width="200" />

        <el-table-column label="内容" prop="content" show-overflow-tooltip />

        <el-table-column label="时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button 
              v-if="row.isRead === 0"
              type="primary" 
              size="small" 
              @click.stop="handleMarkRead(row)"
              link
            >
              标记已读
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click.stop="handleDelete(row)"
              link
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Bell, 
  Search, 
  DataAnalysis, 
  Warning, 
  CircleCheck, 
  Clock 
} from '@element-plus/icons-vue'
import { 
  getNotificationPage, 
  getUnreadCount, 
  markAsRead, 
  markAllAsRead, 
  deleteNotification,
  batchDeleteNotifications,
  batchMarkAsRead,
  searchNotifications,
  getNotificationStatistics,
  clearReadNotifications
} from '@/api/notification'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const notificationList = ref([])
const total = ref(0)
const unreadCount = ref(0)
const filterType = ref('all')
const searchKeyword = ref('')
const isSearchMode = ref(false)
const selectedRows = ref([])
const showStatistics = ref(false)
const statistics = ref({})
const queryParams = ref({
  page: 1,
  pageSize: 10,
  onlyUnread: null
})

// 获取通知列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getNotificationPage(queryParams.value)
    if (res.code === 1) {
      notificationList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取通知列表失败')
  } finally {
    loading.value = false
  }
}

// 获取未读数量
const getUnread = async () => {
  try {
    const res = await getUnreadCount()
    if (res.code === 1) {
      unreadCount.value = res.data
    }
  } catch (error) {
    console.error('获取未读数量失败', error)
  }
}

// 筛选类型改变
const handleFilterChange = (value) => {
  queryParams.value.page = 1
  if (value === 'unread') {
    queryParams.value.onlyUnread = true
  } else if (value === 'all') {
    queryParams.value.onlyUnread = null
  } else {
    queryParams.value.onlyUnread = false
  }
  getList()
}

// 标记单个通知为已读
const handleMarkRead = async (row) => {
  try {
    const res = await markAsRead(row.id)
    if (res.code === 1) {
      ElMessage.success('标记成功')
      row.isRead = 1
      getUnread()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 全部标记为已读
const handleMarkAllRead = async () => {
  try {
    await ElMessageBox.confirm('确定要将所有通知标记为已读吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await markAllAsRead()
    if (res.code === 1) {
      ElMessage.success('操作成功')
      getList()
      getUnread()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 删除通知
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteNotification(row.id)
    if (res.code === 1) {
      ElMessage.success('删除成功')
      getList()
      if (row.isRead === 0) {
        getUnread()
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 页码改变
const handlePageChange = (page) => {
  queryParams.value.page = page
  getList()
}

// 每页数量改变
const handleSizeChange = (size) => {
  queryParams.value.pageSize = size
  queryParams.value.page = 1
  getList()
}

// 点击行跳转到详情
const handleRowClick = (row) => {
  // 跳转到相关业务页面
  switch (row.bizType) {
    case 'reservation':
      router.push('/reservation/list')
      break
    case 'coupon':
      router.push('/coupon/list')
      break
    case 'review':
      router.push('/store/review')
      break
    default:
      // 如果没有业务类型，显示通知详情
      ElMessage.info(row.content)
  }
  
  // 如果未读，标记为已读
  if (row.isRead === 0) {
    handleMarkRead(row)
  }
}

// 设置表格行样式
const tableRowClassName = ({ row }) => {
  return row.isRead === 0 ? 'unread-row' : ''
}

// 获取类型标签
const getTypeTag = (type) => {
  const typeMap = {
    1: 'success',  // 预约成功
    2: 'warning',  // 预约提醒
    3: 'danger',   // 优惠券到期
    4: 'info'      // 系统公告
  }
  return typeMap[type] || 'info'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  // 1分钟内
  if (diff < 60000) {
    return '刚刚'
  }
  // 1小时内
  if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`
  }
  // 今天
  if (date.toDateString() === now.toDateString()) {
    return `今天 ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
  }
  // 昨天
  const yesterday = new Date(now)
  yesterday.setDate(yesterday.getDate() - 1)
  if (date.toDateString() === yesterday.toDateString()) {
    return `昨天 ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
  }
  // 其他
  return time.substring(0, 16)
}

// 选择改变
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 清空选择
const handleClearSelection = () => {
  selectedRows.value = []
}

// 搜索通知
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  
  loading.value = true
  isSearchMode.value = true
  try {
    const res = await searchNotifications({
      keyword: searchKeyword.value,
      page: queryParams.value.page,
      pageSize: queryParams.value.pageSize
    })
    if (res.code === 1) {
      notificationList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('搜索失败')
  } finally {
    loading.value = false
  }
}

// 清空搜索
const handleSearchClear = () => {
  searchKeyword.value = ''
  isSearchMode.value = false
  queryParams.value.page = 1
  getList()
}

// 批量标记已读
const handleBatchMarkRead = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要标记的通知')
    return
  }
  
  const ids = selectedRows.value.map(row => row.id)
  try {
    const res = await batchMarkAsRead(ids)
    if (res.code === 1) {
      ElMessage.success('批量标记成功')
      selectedRows.value = []
      getList()
      getUnread()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的通知')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 条通知吗？`, 
      '提示', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const ids = selectedRows.value.map(row => row.id)
    const res = await batchDeleteNotifications(ids)
    if (res.code === 1) {
      ElMessage.success('批量删除成功')
      selectedRows.value = []
      getList()
      getUnread()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 清空已读通知
const handleClearRead = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有已读通知吗？此操作不可恢复！', 
      '警告', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await clearReadNotifications()
    if (res.code === 1) {
      ElMessage.success('清空成功')
      getList()
      getUnread()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 切换统计显示
const toggleStatistics = async () => {
  showStatistics.value = !showStatistics.value
  if (showStatistics.value) {
    await loadStatistics()
  }
}

// 加载统计信息
const loadStatistics = async () => {
  try {
    const res = await getNotificationStatistics()
    if (res.code === 1) {
      statistics.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取统计信息失败')
  }
}

// 初始化
onMounted(() => {
  getList()
  getUnread()
  
  // 每30秒刷新一次未读数量
  setInterval(() => {
    getUnread()
  }, 30000)
})
</script>

<style scoped>
.notification-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.badge {
  margin-right: 8px;
}

.filter-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-left {
  flex: 1;
}

.filter-right {
  display: flex;
  align-items: center;
}

.batch-actions {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.batch-left,
.batch-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.statistics-panel {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  overflow: hidden;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.type-statistics {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 15px;
}

.type-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.type-count {
  font-weight: bold;
  color: #409EFF;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 未读行样式 */
:deep(.unread-row) {
  background-color: #ecf5ff !important;
  cursor: pointer;
}

:deep(.unread-row:hover) {
  background-color: #d9ecff !important;
}

/* 已读行鼠标悬停样式 */
:deep(.el-table__row) {
  cursor: pointer;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa !important;
}
</style>
