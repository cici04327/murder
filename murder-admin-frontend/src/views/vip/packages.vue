<template>
  <div class="vip-packages-container">
    <!-- 顶部统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">VIP会员总数</div>
              <div class="stat-value">{{ stats.totalVipUsers || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon :size="32"><ShoppingBag /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">套餐总数</div>
              <div class="stat-value">{{ stats.totalPackages || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
              <el-icon :size="32"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">本月收入</div>
              <div class="stat-value">¥{{ stats.monthlyRevenue || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #30cfd0 0%, #330867 100%)">
              <el-icon :size="32"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">新增会员</div>
              <div class="stat-value">{{ stats.newVipUsers || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作栏 -->
    <el-card class="filter-card">
      <el-row :gutter="20" align="middle">
        <el-col :span="18">
          <el-space wrap>
            <el-select v-model="queryParams.status" placeholder="全部状态" style="width: 120px" @change="loadPackages">
              <el-option label="全部" :value="null" />
              <el-option label="上架中" :value="1" />
              <el-option label="已下架" :value="0" />
            </el-select>
            <el-select v-model="queryParams.level" placeholder="全部等级" style="width: 150px" @change="loadPackages">
              <el-option label="全部等级" :value="null" />
              <el-option label="普通会员" :value="1" />
              <el-option label="银卡会员" :value="2" />
              <el-option label="金卡会员" :value="3" />
              <el-option label="钻石会员" :value="4" />
            </el-select>
            <el-button type="default" @click="resetFilters">重置</el-button>
          </el-space>
        </el-col>
        <el-col :span="6" style="text-align: right">
          <el-button type="primary" @click="showPackageDialog(null)">
            <el-icon><Plus /></el-icon>
            新增套餐
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 套餐列表 -->
    <el-card class="table-card">
      <el-table 
        :data="packages" 
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="套餐信息" min-width="200">
          <template #default="{ row }">
            <div class="package-info">
              <div class="package-name">
                {{ row.name }}
                <el-tag v-if="row.tag" size="small" type="warning" style="margin-left: 8px">{{ row.tag }}</el-tag>
              </div>
              <div class="package-level">
                <el-tag :type="getLevelTagType(row.level)" size="small">
                  {{ getLevelName(row.level) }}
                </el-tag>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="时长" width="100">
          <template #default="{ row }">
            {{ row.durationDays }}天
          </template>
        </el-table-column>
        <el-table-column label="价格" width="150">
          <template #default="{ row }">
            <div class="price-info">
              <div class="current-price">¥{{ row.currentPrice }}</div>
              <div class="original-price" v-if="row.originalPrice > row.currentPrice">
                原价 ¥{{ row.originalPrice }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="积分倍率" width="100">
          <template #default="{ row }">
            <el-tag type="success">{{ row.pointMultiplier }}倍</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="月送券" width="100">
          <template #default="{ row }">
            {{ row.couponCount }}张
          </template>
        </el-table-column>
        <el-table-column label="权益" width="180">
          <template #default="{ row }">
            <el-space wrap :size="5">
              <el-tag v-if="row.priorityBooking" size="small" type="primary">预约优先</el-tag>
              <el-tag v-if="row.exclusiveService" size="small" type="success">专属客服</el-tag>
              <el-tag v-if="row.birthdayGift" size="small" type="warning">生日礼包</el-tag>
              <el-tag v-if="row.exclusiveBadge" size="small" type="danger">专属徽章</el-tag>
              <el-tag v-if="row.specialDiscount" size="small" type="info">{{ (row.specialDiscount * 100).toFixed(0) }}折</el-tag>
            </el-space>
          </template>
        </el-table-column>
        <el-table-column label="排序" width="80">
          <template #default="{ row }">
            {{ row.sortOrder }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showPackageDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentPackage ? '编辑套餐' : '新增套餐'"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form :model="packageForm" :rules="packageRules" ref="packageFormRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="套餐名称" prop="name">
              <el-input v-model="packageForm.name" placeholder="请输入套餐名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="会员等级" prop="level">
              <el-select v-model="packageForm.level" placeholder="请选择等级" style="width: 100%">
                <el-option label="普通会员" :value="1" />
                <el-option label="银卡会员" :value="2" />
                <el-option label="金卡会员" :value="3" />
                <el-option label="钻石会员" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="时长类型" prop="durationType">
              <el-select v-model="packageForm.durationType" placeholder="请选择" style="width: 100%">
                <el-option label="月度" :value="1" />
                <el-option label="季度" :value="2" />
                <el-option label="半年" :value="3" />
                <el-option label="年度" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="时长天数" prop="durationDays">
              <el-input-number v-model="packageForm.durationDays" :min="1" :max="3650" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="原价" prop="originalPrice">
              <el-input-number v-model="packageForm.originalPrice" :precision="2" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="现价" prop="currentPrice">
              <el-input-number v-model="packageForm.currentPrice" :precision="2" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="积分倍率" prop="pointMultiplier">
              <el-input-number v-model="packageForm.pointMultiplier" :precision="1" :min="1" :max="10" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="月送券数" prop="couponCount">
              <el-input-number v-model="packageForm.couponCount" :min="0" :max="10" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="专属折扣">
              <el-input-number v-model="packageForm.specialDiscount" :precision="2" :min="0.5" :max="1" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="packageForm.sortOrder" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="权益开关">
          <el-space wrap>
            <el-checkbox v-model="packageForm.priorityBooking">预约优先</el-checkbox>
            <el-checkbox v-model="packageForm.exclusiveService">专属客服</el-checkbox>
            <el-checkbox v-model="packageForm.birthdayGift">生日礼包</el-checkbox>
            <el-checkbox v-model="packageForm.exclusiveBadge">专属徽章</el-checkbox>
          </el-space>
        </el-form-item>

        <el-form-item label="标签">
          <el-input v-model="packageForm.tag" placeholder="如：推荐、最划算" maxlength="10" />
        </el-form-item>

        <el-form-item label="套餐描述">
          <el-input v-model="packageForm.description" type="textarea" :rows="3" placeholder="请输入套餐描述" />
        </el-form-item>

        <el-form-item label="权益列表">
          <div class="features-editor">
            <div v-for="(feature, index) in packageForm.features" :key="index" class="feature-item">
              <el-input v-model="packageForm.features[index]" placeholder="请输入权益内容">
                <template #append>
                  <el-button @click="removeFeature(index)">删除</el-button>
                </template>
              </el-input>
            </div>
            <el-button type="dashed" style="width: 100%; margin-top: 10px" @click="addFeature">
              <el-icon><Plus /></el-icon>
              添加权益
            </el-button>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, ShoppingBag, Money, TrendCharts, Plus } from '@element-plus/icons-vue'
import { 
  getAllPackages, 
  createPackage, 
  updatePackage, 
  deletePackage, 
  updatePackageStatus,
  getVipStatistics 
} from '@/api/vip'

// 数据
const loading = ref(false)
const packages = ref([])
const stats = ref({})
const queryParams = reactive({
  status: null,
  level: null
})

// 对话框
const dialogVisible = ref(false)
const currentPackage = ref(null)
const packageFormRef = ref(null)
const submitting = ref(false)

const packageForm = reactive({
  name: '',
  level: null,
  durationType: null,
  durationDays: 30,
  originalPrice: 0,
  currentPrice: 0,
  pointMultiplier: 1.0,
  couponCount: 0,
  priorityBooking: false,
  exclusiveService: false,
  birthdayGift: false,
  exclusiveBadge: false,
  specialDiscount: null,
  tag: '',
  description: '',
  features: [],
  sortOrder: 0
})

const packageRules = {
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  level: [{ required: true, message: '请选择会员等级', trigger: 'change' }],
  durationType: [{ required: true, message: '请选择时长类型', trigger: 'change' }],
  durationDays: [{ required: true, message: '请输入时长天数', trigger: 'blur' }],
  originalPrice: [{ required: true, message: '请输入原价', trigger: 'blur' }],
  currentPrice: [{ required: true, message: '请输入现价', trigger: 'blur' }],
  pointMultiplier: [{ required: true, message: '请输入积分倍率', trigger: 'blur' }]
}

// 加载套餐列表
const loadPackages = async () => {
  try {
    loading.value = true
    const res = await getAllPackages(queryParams)
    console.log('VIP套餐API响应:', res)
    if (res.code === 200 || res.code === 1) {
      // 后端返回的是PageResult，数据在records里
      if (res.data && res.data.records) {
        packages.value = res.data.records
        console.log('VIP套餐数据:', packages.value)
      } else {
        packages.value = []
        console.warn('VIP套餐数据格式异常:', res.data)
      }
    }
  } catch (error) {
    console.error('加载套餐失败:', error)
    ElMessage.error('加载套餐失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await getVipStatistics()
    if (res.code === 200 || res.code === 1) {
      stats.value = res.data || {}
    }
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

// 显示对话框
const showPackageDialog = (pkg) => {
  currentPackage.value = pkg
  if (pkg) {
    // 编辑模式
    Object.assign(packageForm, {
      ...pkg,
      features: pkg.features ? [...pkg.features] : []
    })
  } else {
    // 新增模式
    resetForm()
  }
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  Object.assign(packageForm, {
    name: '',
    level: null,
    durationType: null,
    durationDays: 30,
    originalPrice: 0,
    currentPrice: 0,
    pointMultiplier: 1.0,
    couponCount: 0,
    priorityBooking: false,
    exclusiveService: false,
    birthdayGift: false,
    exclusiveBadge: false,
    specialDiscount: null,
    tag: '',
    description: '',
    features: [],
    sortOrder: 0
  })
  packageFormRef.value?.clearValidate()
}

// 添加权益
const addFeature = () => {
  packageForm.features.push('')
}

// 删除权益
const removeFeature = (index) => {
  packageForm.features.splice(index, 1)
}

// 提交表单
const handleSubmit = async () => {
  try {
    await packageFormRef.value.validate()
    
    submitting.value = true
    const data = { ...packageForm }
    
    // 过滤空权益
    data.features = data.features.filter(f => f.trim())
    
    let res
    if (currentPackage.value) {
      // 编辑
      data.id = currentPackage.value.id
      res = await updatePackage(data)
    } else {
      // 新增
      res = await createPackage(data)
    }
    
    if (res.code === 200 || res.code === 1) {
      ElMessage.success(currentPackage.value ? '修改成功' : '创建成功')
      dialogVisible.value = false
      loadPackages()
      loadStatistics()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    if (error !== false) { // 不是验证错误
      ElMessage.error('提交失败，请重试')
    }
  } finally {
    submitting.value = false
  }
}

// 状态切换
const handleStatusChange = async (row) => {
  try {
    const res = await updatePackageStatus(row.id, row.status)
    if (res.code === 200 || res.code === 1) {
      ElMessage.success(row.status === 1 ? '上架成功' : '下架成功')
    } else {
      ElMessage.error(res.msg || '操作失败')
      row.status = row.status === 1 ? 0 : 1 // 回滚
    }
  } catch (error) {
    console.error('状态切换失败:', error)
    ElMessage.error('操作失败')
    row.status = row.status === 1 ? 0 : 1 // 回滚
  }
}

// 删除套餐
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该套餐吗？', '提示', {
      type: 'warning'
    })
    
    const res = await deletePackage(row.id)
    if (res.code === 200 || res.code === 1) {
      ElMessage.success('删除成功')
      loadPackages()
      loadStatistics()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 重置筛选
const resetFilters = () => {
  queryParams.status = null
  queryParams.level = null
  loadPackages()
}

// 获取等级名称
const getLevelName = (level) => {
  const names = { 1: '普通会员', 2: '银卡会员', 3: '金卡会员', 4: '钻石会员' }
  return names[level] || '未知'
}

// 获取等级标签类型
const getLevelTagType = (level) => {
  const types = { 1: 'info', 2: 'default', 3: 'warning', 4: 'danger' }
  return types[level] || 'info'
}

onMounted(() => {
  loadPackages()
  loadStatistics()
})
</script>

<style scoped lang="scss">
.vip-packages-container {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  .stat-content {
    display: flex;
    align-items: center;
    gap: 16px;

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      flex-shrink: 0;
    }

    .stat-info {
      flex: 1;

      .stat-label {
        font-size: 14px;
        color: #999;
        margin-bottom: 8px;
      }

      .stat-value {
        font-size: 24px;
        font-weight: bold;
        color: #333;
      }
    }
  }
}

.filter-card {
  margin-bottom: 20px;
}

.table-card {
  .package-info {
    .package-name {
      font-weight: bold;
      margin-bottom: 6px;
      display: flex;
      align-items: center;
    }

    .package-level {
      font-size: 12px;
    }
  }

  .price-info {
    .current-price {
      font-size: 16px;
      font-weight: bold;
      color: #f56c6c;
    }

    .original-price {
      font-size: 12px;
      color: #999;
      text-decoration: line-through;
      margin-top: 4px;
    }
  }
}

.features-editor {
  width: 100%;

  .feature-item {
    margin-bottom: 10px;
  }
}
</style>

