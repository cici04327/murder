<template>
  <div class="employee-management">
    <el-card>
      <!-- 查询表单 -->
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="门店">
          <el-select v-model="queryForm.storeId" placeholder="请选择门店" clearable filterable>
            <el-option
              v-for="store in storeList"
              :key="store.id"
              :label="store.name"
              :value="store.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">新增员工</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="storeName" label="所属门店" width="180" />
        <el-table-column prop="name" label="员工姓名" width="120" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="position" label="职位" width="120">
          <template #default="{ row }">
            <el-tag :type="getPositionType(row.position)">
              {{ getPositionText(row.position) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinDate" label="入职日期" width="120" />
        <el-table-column prop="salary" label="月薪(元)" width="120">
          <template #default="{ row }">
            {{ row.salary ? row.salary.toFixed(2) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button 
              :type="row.status === 1 ? 'warning' : 'success'" 
              size="small" 
              @click="handleChangeStatus(row)"
            >
              {{ row.status === 1 ? '离职' : '复职' }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
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
        <el-form-item label="门店" prop="storeId">
          <el-select v-model="formData.storeId" placeholder="请选择门店" filterable>
            <el-option
              v-for="store in storeList"
              :key="store.id"
              :label="store.name"
              :value="store.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="员工姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入员工姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="职位" prop="position">
          <el-select v-model="formData.position" placeholder="请选择职位">
            <el-option label="店长" :value="1" />
            <el-option label="副店长" :value="2" />
            <el-option label="主持人" :value="3" />
            <el-option label="服务员" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="入职日期" prop="joinDate">
          <el-date-picker
            v-model="formData.joinDate"
            type="date"
            placeholder="选择入职日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="月薪(元)" prop="salary">
          <el-input-number
            v-model="formData.salary"
            :min="0"
            :max="100000"
            :precision="2"
            :step="100"
            placeholder="请输入月薪"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">在职</el-radio>
            <el-radio :label="0">离职</el-radio>
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
const dialogTitle = ref('新增员工')
const formRef = ref(null)
const storeList = ref([])

const queryForm = reactive({
  storeId: null,
  page: 1,
  pageSize: 10
})

const formData = reactive({
  id: null,
  storeId: null,
  name: '',
  phone: '',
  position: null,
  joinDate: '',
  salary: null,
  status: 1
})

const formRules = {
  storeId: [{ required: true, message: '请选择门店', trigger: 'change' }],
  name: [{ required: true, message: '请输入员工姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  position: [{ required: true, message: '请选择职位', trigger: 'change' }]
}

const getPositionText = (position) => {
  const map = { 1: '店长', 2: '副店长', 3: '主持人', 4: '服务员' }
  return map[position] || '未知'
}

const getPositionType = (position) => {
  const map = { 1: 'danger', 2: 'warning', 3: 'success', 4: '' }
  return map[position] || ''
}

const fetchStoreList = async () => {
  try {
    const res = await request.get('/store/list')
    storeList.value = res.data
  } catch (error) {
    console.error('获取门店列表失败:', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: queryForm.page,
      pageSize: queryForm.pageSize
    }
    if (queryForm.storeId) {
      params.storeId = queryForm.storeId
    }
    const res = await request.get('/store/employee/page', { params })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryForm.page = 1
  fetchData()
}

const handleReset = () => {
  queryForm.storeId = null
  queryForm.page = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增员工'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑员工'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleChangeStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 1 ? '复职' : '离职'
  
  try {
    await ElMessageBox.confirm(`确定要将该员工设置为${actionText}状态吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.put(`/store/employee/status/${row.id}?status=${newStatus}`)
    ElMessage.success(`${actionText}成功`)
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该员工吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.delete(`/store/employee/${row.id}`)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    if (formData.id) {
      await request.put('/store/employee', formData)
      ElMessage.success('更新成功')
    } else {
      await request.post('/store/employee', formData)
      ElMessage.success('新增成功')
    }
    
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('提交失败:', error)
    if (error !== false) {
      ElMessage.error('提交失败')
    }
  }
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(formData, {
    id: null,
    storeId: null,
    name: '',
    phone: '',
    position: null,
    joinDate: '',
    salary: null,
    status: 1
  })
}

onMounted(() => {
  fetchStoreList()
  fetchData()
})
</script>

<style scoped>
.employee-management {
  width: 100%;
}

.query-form {
  margin-bottom: 20px;
}
</style>
