<template>
  <div class="favorites-container">
    <div class="page-header">
      <h2>我的收藏</h2>
      <p class="subtitle">共收藏了 {{ total }} 个剧本</p>
    </div>

    <el-divider />

    <div v-loading="loading">
      <el-row :gutter="20" v-if="scriptList.length > 0">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="script in scriptList" :key="script.id">
          <el-card class="script-card" shadow="hover">
            <div class="script-cover" @click="goToDetail(script.id)">
              <img :src="script.cover || '/default-script.jpg'" :alt="script.name" onerror="this.src='/default-script.jpg'" />
              <div class="script-overlay">
                <el-button type="primary" circle>
                  <el-icon><View /></el-icon>
                </el-button>
              </div>
            </div>
            
            <div class="script-info">
              <h3 class="script-name" @click="goToDetail(script.id)">{{ script.name }}</h3>
              
              <div class="script-tags">
                <el-tag size="small" type="success">{{ script.categoryName || '未分类' }}</el-tag>
                <el-tag size="small">{{ script.playerCount }}人</el-tag>
              </div>
              
              <div class="script-rating">
                <el-rate v-model="script.rating" disabled show-score text-color="#ff9900" size="small" />
              </div>
              
              <div class="script-price">
                <span class="price">¥{{ script.price }}</span>
                <span class="unit">/人</span>
              </div>
              
              <div class="script-actions">
                <el-button type="primary" size="small" @click="handleReserve(script.id)">
                  <el-icon><Calendar /></el-icon>
                  预约
                </el-button>
                <el-button size="small" type="danger" @click="handleUnfavorite(script.id)">
                  <el-icon><Delete /></el-icon>
                  取消收藏
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-else description="还没有收藏任何剧本" />

      <el-pagination
        v-if="total > 0"
        class="pagination"
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[8, 12, 16, 24]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFavoriteScripts, unfavoriteScript } from '@/api/script'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, View, Delete } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const scriptList = ref([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  pageSize: 12
})

// 加载收藏列表
const loadFavorites = async () => {
  loading.value = true
  try {
    const res = await getFavoriteScripts(queryParams)
    console.log('收藏列表返回数据:', res)
    if (res.code === 1 || res.code === 200) {
      // 后端返回的是 PageResult，结构是 {total, records}
      if (res.data) {
        scriptList.value = res.data.records || []
        // 如果 total 为 0 但有数据，使用数据长度
        total.value = res.data.total || (res.data.records || []).length
      } else {
        scriptList.value = []
        total.value = 0
      }
      console.log('收藏列表:', scriptList.value)
      console.log('收藏总数:', total.value)
    } else {
      ElMessage.error(res.msg || '加载失败')
    }
  } catch (error) {
    console.error('加载收藏列表失败:', error)
    ElMessage.error('加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 跳转到详情页
const goToDetail = (id) => {
  router.push(`/script/${id}`)
}

// 预约剧本
const handleReserve = (scriptId) => {
  router.push({
    path: '/reservation/create',
    query: { scriptId }
  })
}

// 取消收藏
const handleUnfavorite = async (scriptId) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏这个剧本吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await unfavoriteScript(scriptId)
    ElMessage.success('取消收藏成功')
    
    // 刷新列表
    loadFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 分页变化
const handleSizeChange = () => {
  queryParams.page = 1
  loadFavorites()
}

const handleCurrentChange = () => {
  loadFavorites()
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.favorites-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.subtitle {
  margin: 10px 0 0;
  color: #666;
  font-size: 14px;
}

.script-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
  cursor: pointer;
}

.script-card:hover {
  transform: translateY(-5px);
}

.script-cover {
  position: relative;
  width: 100%;
  height: 240px;
  overflow: hidden;
  border-radius: 4px;
  cursor: pointer;
}

.script-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.script-cover:hover img {
  transform: scale(1.1);
}

.script-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.script-cover:hover .script-overlay {
  opacity: 1;
}

.script-info {
  padding: 15px 0 0;
}

.script-name {
  font-size: 16px;
  margin: 0 0 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  color: #333;
}

.script-name:hover {
  color: #409eff;
}

.script-tags {
  margin-bottom: 10px;
}

.script-tags .el-tag {
  margin-right: 5px;
}

.script-rating {
  margin-bottom: 10px;
}

.script-price {
  margin-bottom: 15px;
  font-size: 14px;
}

.price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}

.unit {
  color: #999;
  font-size: 12px;
  margin-left: 2px;
}

.script-actions {
  display: flex;
  gap: 10px;
}

.script-actions .el-button {
  flex: 1;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .script-actions {
    flex-direction: column;
  }
  
  .script-actions .el-button {
    width: 100%;
  }
}
</style>
