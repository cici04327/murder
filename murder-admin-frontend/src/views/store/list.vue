<template>
  <div class="store-list">
    <el-card>
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="门店名称">
          <el-input v-model="queryForm.name" placeholder="请输入门店名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">新增门店</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="门店名称" width="180" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column label="营业时间" width="180">
          <template #default="{ row }">
            <span v-if="row.openTime && row.closeTime">
              {{ row.openTime }} - {{ row.closeTime }}
            </span>
            <span v-else style="color: #ccc;">未设置</span>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="130">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled show-score text-color="#ff9900" :max="5" v-if="row.rating" />
            <span v-else style="color: #ccc;">暂无评分</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="getBusinessStatusType(row)" size="small">
              {{ getBusinessStatusText(row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="门店名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入门店名称" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="门店地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入门店地址" />
        </el-form-item>
        <el-form-item label="开始时间" prop="openTime">
          <el-time-picker
            v-model="formData.openTime"
            format="HH:mm:ss"
            value-format="HH:mm:ss"
            placeholder="选择营业开始时间"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="closeTime">
          <el-time-picker
            v-model="formData.closeTime"
            format="HH:mm:ss"
            value-format="HH:mm:ss"
            placeholder="选择营业结束时间"
          />
        </el-form-item>
        <el-form-item label="门店描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入门店描述"
          />
        </el-form-item>
        <el-form-item label="门店评分">
          <el-rate v-model="formData.rating" :max="5" show-score />
          <span style="margin-left: 10px; color: #999; font-size: 12px;">用于展示门店服务质量</span>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">营业中</el-radio>
            <el-radio :label="0">已停业</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('新增门店')
const formRef = ref(null)

const queryForm = reactive({
  name: '',
  page: 1,
  pageSize: 10
})

const formData = reactive({
  id: null,
  name: '',
  phone: '',
  address: '',
  openTime: '',
  closeTime: '',
  description: '',
  rating: 0,
  status: 1
})

// 判断营业状态
const getBusinessStatusType = (row) => {
  if (row.status === 0) return 'danger' // 已停业
  
  if (!row.openTime || !row.closeTime) return 'warning' // 未设置营业时间
  
  // 判断当前时间是否在营业时间内
  const now = new Date()
  const currentTime = now.getHours() * 60 + now.getMinutes()
  
  const [openHour, openMin] = row.openTime.split(':').map(Number)
  const [closeHour, closeMin] = row.closeTime.split(':').map(Number)
  
  const openMinutes = openHour * 60 + openMin
  const closeMinutes = closeHour * 60 + closeMin
  
  if (currentTime >= openMinutes && currentTime <= closeMinutes) {
    return 'success' // 营业中
  }
  return 'info' // 休息中
}

const getBusinessStatusText = (row) => {
  if (row.status === 0) return '已停业'
  
  if (!row.openTime || !row.closeTime) return '未设置'
  
  const now = new Date()
  const currentTime = now.getHours() * 60 + now.getMinutes()
  
  const [openHour, openMin] = row.openTime.split(':').map(Number)
  const [closeHour, closeMin] = row.closeTime.split(':').map(Number)
  
  const openMinutes = openHour * 60 + openMin
  const closeMinutes = closeHour * 60 + closeMin
  
  if (currentTime >= openMinutes && currentTime <= closeMinutes) {
    return '营业中'
  }
  return '休息中'
}

const formRules = {
  name: [{ required: true, message: '请输入门店名称', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  address: [{ required: true, message: '请输入门店地址', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    console.log('加载门店列表，参数:', queryForm)
    const res = await request.get('/store/page', { params: queryForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (newPage) => {
  console.log('门店列表页码变化:', newPage)
  queryForm.page = newPage
  fetchData()
}

const handleSizeChange = (newSize) => {
  console.log('门店列表每页大小变化:', newSize)
  queryForm.pageSize = newSize
  queryForm.page = 1
  fetchData()
}

const handleQuery = () => {
  queryForm.page = 1
  fetchData()
}

const handleReset = () => {
  queryForm.name = ''
  queryForm.page = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增门店'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑门店'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该门店吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.delete(`/store/${row.id}`)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    if (formData.id) {
      await request.put('/store', formData)
      ElMessage.success('更新成功')
    } else {
      await request.post('/store', formData)
      ElMessage.success('新增成功')
    }
    
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(formData, {
    id: null,
    name: '',
    phone: '',
    address: '',
    openTime: '',
    closeTime: '',
    description: '',
    rating: 0,
    status: 1
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.store-list {
  width: 100%;
}

.query-form {
  margin-bottom: 20px;
}
</style>
