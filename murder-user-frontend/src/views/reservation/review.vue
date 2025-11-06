<template>
  <div class="review-page">
    <el-card class="review-card">
      <template #header>
        <div class="card-header">
          <span>订单评价</span>
        </div>
      </template>

      <!-- 订单信息 -->
      <div class="order-info" v-if="reservation">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="预约编号">{{ reservation.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="预约时间">{{ formatTime(reservation.reservationTime) }}</el-descriptions-item>
          <el-descriptions-item label="剧本名称">{{ reservation.scriptName }}</el-descriptions-item>
          <el-descriptions-item label="门店名称">{{ reservation.storeName }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 评价表单 -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="review-form"
      >
        <!-- 综合评分 -->
        <el-form-item label="综合评分" prop="overallRating">
          <el-rate v-model="form.overallRating" :max="5" show-text />
        </el-form-item>

        <!-- 门店评分 -->
        <el-form-item label="门店评分" prop="storeRating">
          <el-rate v-model="form.storeRating" :max="5" />
          <span class="rating-desc">环境、服务、位置等</span>
        </el-form-item>

        <!-- 剧本评分 -->
        <el-form-item label="剧本评分" prop="scriptRating">
          <el-rate v-model="form.scriptRating" :max="5" />
          <span class="rating-desc">剧情、难度、体验等</span>
        </el-form-item>

        <!-- 服务评分 -->
        <el-form-item label="服务评分" prop="serviceRating">
          <el-rate v-model="form.serviceRating" :max="5" />
          <span class="rating-desc">DM服务、响应速度等</span>
        </el-form-item>

        <!-- 评价内容 -->
        <el-form-item label="评价内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            placeholder="分享你的游戏体验吧~（详细评价超过50字可获得额外积分）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <!-- 评价标签 -->
        <el-form-item label="评价标签">
          <el-checkbox-group v-model="selectedTags">
            <el-checkbox-button v-for="tag in availableTags" :key="tag" :label="tag">
              {{ tag }}
            </el-checkbox-button>
          </el-checkbox-group>
        </el-form-item>

        <!-- 上传图片 -->
        <el-form-item label="上传图片">
          <el-upload
            v-model:file-list="imageList"
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :limit="5"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">最多上传5张图片，上传图片可获得额外积分</div>
        </el-form-item>

        <!-- 是否匿名 -->
        <el-form-item label="匿名评价">
          <el-switch v-model="form.isAnonymous" :active-value="1" :inactive-value="0" />
          <span class="rating-desc">匿名后不显示用户名</span>
        </el-form-item>

        <!-- 积分提示 -->
        <el-alert
          title="积分奖励说明"
          type="info"
          :closable="false"
          class="points-tip"
        >
          <div>基础积分：50分</div>
          <div>上传图片：+10分</div>
          <div>详细评价（超过50字）：+10分</div>
          <div>完成所有评分项：+5分</div>
          <div>最高可获得：75积分</div>
        </el-alert>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting" size="large">
            提交评价
          </el-button>
          <el-button @click="handleCancel" size="large">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getReservationDetail } from '@/api/reservation'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

const formRef = ref(null)
const submitting = ref(false)
const reservation = ref(null)
const imageList = ref([])
const selectedTags = ref([])

const availableTags = [
  '剧情精彩',
  '烧脑推理',
  '沉浸感强',
  '服务周到',
  '环境优雅',
  '性价比高',
  '适合新手',
  '适合进阶',
  '氛围好',
  'DM专业'
]

const form = reactive({
  reservationId: null,
  storeId: null,
  scriptId: null,
  overallRating: 5,
  storeRating: 5,
  scriptRating: 5,
  serviceRating: 5,
  content: '',
  images: '',
  tags: '',
  isAnonymous: 0
})

const rules = {
  overallRating: [
    { required: true, message: '请选择综合评分', trigger: 'change' }
  ],
  storeRating: [
    { required: true, message: '请选择门店评分', trigger: 'change' }
  ],
  scriptRating: [
    { required: true, message: '请选择剧本评分', trigger: 'change' }
  ],
  serviceRating: [
    { required: true, message: '请选择服务评分', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请填写评价内容', trigger: 'blur' },
    { min: 10, message: '评价内容至少10个字', trigger: 'blur' }
  ]
}

// 加载预约详情
const loadReservation = async () => {
  try {
    const res = await getReservationDetail(route.params.id)
    if (res.code === 1 || res.code === 200) {
      reservation.value = res.data
      form.reservationId = res.data.id
      form.storeId = res.data.storeId
      form.scriptId = res.data.scriptId
      
      // 检查订单状态
      if (res.data.status !== 3) {
        ElMessage.warning('只有已完成的订单才能评价')
        router.back()
      }
    }
  } catch (error) {
    console.error('加载预约详情失败:', error)
    ElMessage.error('加载订单信息失败')
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 提交评价
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // 处理图片
        const images = imageList.value
          .filter(item => item.url || item.raw)
          .map(item => item.url || URL.createObjectURL(item.raw))
          .join(',')
        
        // 处理标签
        const tags = selectedTags.value.join(',')
        
        const reviewData = {
          ...form,
          images,
          tags
        }
        
        const res = await request.post('/reservation/review', reviewData)
        
        if (res.code === 1 || res.code === 200) {
          ElMessage.success({
            message: `评价成功！获得 ${calculatePoints()} 积分奖励`,
            duration: 3000
          })
          setTimeout(() => {
            router.push('/user/reservations')
          }, 1500)
        }
      } catch (error) {
        console.error('提交评价失败:', error)
        ElMessage.error(error.response?.data?.msg || '提交评价失败，请重试')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 计算积分
const calculatePoints = () => {
  let points = 50 // 基础积分
  
  if (imageList.value.length > 0) {
    points += 10 // 上传图片
  }
  
  if (form.content && form.content.length > 50) {
    points += 10 // 详细评价
  }
  
  if (form.storeRating && form.scriptRating && form.serviceRating) {
    points += 5 // 完成所有评分
  }
  
  return points
}

// 取消
const handleCancel = () => {
  router.back()
}

onMounted(() => {
  if (route.params.id) {
    loadReservation()
  } else {
    ElMessage.error('缺少订单ID')
    router.back()
  }
})
</script>

<style scoped>
.review-page {
  max-width: 900px;
  margin: 20px auto;
  padding: 0 20px;
}

.review-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.order-info {
  margin-bottom: 30px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.review-form {
  margin-top: 20px;
}

.rating-desc {
  margin-left: 10px;
  color: #909399;
  font-size: 14px;
}

.upload-tip {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
}

.points-tip {
  margin: 20px 0;
}

.points-tip div {
  line-height: 24px;
}

:deep(.el-rate) {
  height: 20px;
}

:deep(.el-checkbox-button) {
  margin-right: 10px;
  margin-bottom: 10px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
  line-height: 100px;
}
</style>

