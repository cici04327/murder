<template>
  <div class="reservation-create-container">
    <el-card>
      <template #header>
        <h2>创建预约</h2>
      </template>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="reservation-form"
      >
        <el-form-item label="选择剧本" prop="scriptId">
          <el-select
            v-model="form.scriptId"
            placeholder="请选择剧本"
            filterable
            @change="handleScriptChange"
          >
            <el-option
              v-for="script in scripts"
              :key="script.id"
              :label="script.name"
              :value="script.id"
            >
              <span>{{ script.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                ¥{{ script.price }}/人
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="选择门店" prop="storeId">
          <el-select
            v-model="form.storeId"
            placeholder="请选择门店"
            filterable
            @change="handleStoreChange"
          >
            <el-option
              v-for="store in stores"
              :key="store.id"
              :label="store.name"
              :value="store.id"
            >
              <div>
                <div>{{ store.name }}</div>
                <div style="font-size: 12px; color: #999">{{ store.address }}</div>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="选择房间" prop="roomId" v-if="rooms.length > 0">
          <el-select v-model="form.roomId" placeholder="请选择房间">
            <el-option
              v-for="room in availableRooms"
              :key="room.id"
              :label="`${room.name} (可容纳${room.capacity}人)`"
              :value="room.id"
            >
              <span>{{ room.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                容纳{{ room.capacity }}人
              </span>
            </el-option>
          </el-select>
          <div v-if="route.query.roomId && form.roomId" class="hint" style="color: #67c23a; margin-top: 5px;">
            <el-icon><CircleCheck /></el-icon>
            已为您预选房间：{{ rooms.find(r => r.id === form.roomId)?.name }}
          </div>
        </el-form-item>
        
        <el-form-item label="预约日期" prop="reservationDate">
          <el-date-picker
            v-model="form.reservationDate"
            type="date"
            placeholder="选择日期"
            :disabled-date="disabledDate"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item label="预约时间" prop="reservationTime">
          <el-time-select
            v-model="form.reservationTime"
            start="09:00"
            step="00:30"
            end="21:00"
            placeholder="选择时间"
          />
        </el-form-item>
        
        <el-form-item label="参与人数" prop="playerCount">
          <el-input-number
            v-model="form.playerCount"
            :min="selectedScript?.playerCount || 1"
            :max="selectedScript?.playerCount || 10"
          />
          <span class="hint" v-if="selectedScript">
            （该剧本需要{{ selectedScript.playerCount }}人）
          </span>
        </el-form-item>
        
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="form.contactName" placeholder="请输入联系人姓名" />
        </el-form-item>
        
        <el-form-item label="优惠券" v-if="availableCoupons.length > 0">
          <el-select v-model="form.userCouponId" placeholder="选择优惠券（可不选）" clearable @change="handleCouponChange">
            <el-option
              v-for="coupon in availableCoupons"
              :key="coupon.id"
              :label="`${coupon.couponName} - ${coupon.type === 1 ? '减¥' + coupon.discountValue : coupon.type === 2 ? (coupon.discountValue * 10) + '折' : '抵用¥' + coupon.discountValue}`"
              :value="coupon.id"
            >
              <span>{{ coupon.couponName }}</span>
              <span style="float: right; color: #f56c6c; font-size: 12px">
                {{ coupon.type === 1 ? `减¥${coupon.discountValue}` : coupon.type === 2 ? `${coupon.discountValue * 10}折` : `抵用¥${coupon.discountValue}` }}
              </span>
            </el-option>
          </el-select>
          <div style="margin-top: 5px; color: #999; font-size: 12px" v-if="selectedCoupon && selectedCoupon.minAmount">
            满¥{{ selectedCoupon.minAmount }}可用
          </div>
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="有什么特殊需求可以在这里说明"
          />
        </el-form-item>
        
        <el-divider />
        
        <div class="price-info">
          <div class="price-item">
            <span>剧本单价：</span>
            <span class="price">¥{{ selectedScript?.price || 0 }}</span>
          </div>
          <div class="price-item">
            <span>参与人数：</span>
            <span>{{ form.playerCount }}人</span>
          </div>
          <div class="price-item">
            <span>小计：</span>
            <span>¥{{ totalPrice }}</span>
          </div>
          <div class="price-item" v-if="discountAmount > 0">
            <span>优惠金额：</span>
            <span style="color: #67c23a">-¥{{ discountAmount }}</span>
          </div>
          <div class="price-item total">
            <span>应付金额：</span>
            <span class="price">¥{{ finalPrice }}</span>
          </div>
        </div>
        
        <el-form-item>
          <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">
            提交预约
          </el-button>
          <el-button size="large" @click="router.back()">
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCheck } from '@element-plus/icons-vue'
import { getScriptList } from '@/api/script'
import { getStoreList, getStoreRooms } from '@/api/store'
import { createReservation, checkRoomAvailability } from '@/api/reservation'
import { getMyCoupons } from '@/api/coupon'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const submitting = ref(false)
const checkingAvailability = ref(false)
const scripts = ref([])
const stores = ref([])
const rooms = ref([])
const availableCoupons = ref([])
const selectedCoupon = ref(null)

const form = reactive({
  scriptId: null,
  storeId: null,
  roomId: null,
  reservationDate: '',
  reservationTime: '',
  playerCount: 1,
  contactPhone: '',
  contactName: '',
  remark: '',
  userCouponId: null
})

const rules = {
  scriptId: [{ required: true, message: '请选择剧本', trigger: 'change' }],
  storeId: [{ required: true, message: '请选择门店', trigger: 'change' }],
  roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  reservationDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  reservationTime: [{ required: true, message: '请选择时间', trigger: 'change' }],
  playerCount: [{ required: true, message: '请输入人数', trigger: 'blur' }],
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 监听时间变化，自动检查可用性
watch([() => form.roomId, () => form.reservationDate, () => form.reservationTime], () => {
  if (form.roomId && form.reservationDate && form.reservationTime) {
    checkAvailability()
  }
})

const selectedScript = computed(() => {
  return scripts.value.find(s => s.id === form.scriptId)
})

const availableRooms = computed(() => {
  return rooms.value.filter(r => r.status === 1)
})

const totalPrice = computed(() => {
  if (!selectedScript.value) return 0
  return (selectedScript.value.price * form.playerCount).toFixed(2)
})

const discountAmount = computed(() => {
  if (!selectedCoupon.value) return 0
  const total = parseFloat(totalPrice.value)
  if (selectedCoupon.value.type === 1) {
    // 满减券
    if (total >= parseFloat(selectedCoupon.value.minAmount || 0)) {
      return parseFloat(selectedCoupon.value.discountValue)
    }
  } else if (selectedCoupon.value.type === 2) {
    // 折扣券：discountValue是小数（如0.8表示8折）
    return (total * (1 - parseFloat(selectedCoupon.value.discountValue))).toFixed(2)
  } else if (selectedCoupon.value.type === 3) {
    // 代金券
    return parseFloat(selectedCoupon.value.discountValue)
  }
  return 0
})

const finalPrice = computed(() => {
  const total = parseFloat(totalPrice.value)
  const discount = parseFloat(discountAmount.value)
  return Math.max(total - discount, 0).toFixed(2)
})

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 86400000
}

const loadScripts = async () => {
  try {
    const res = await getScriptList({ page: 1, pageSize: 100 })
    if (res.data) {
      scripts.value = res.data.records || res.data.list || res.data
    }
  } catch (error) {
    console.error('加载剧本失败:', error)
    // 模拟数据
    scripts.value = [
      { id: 1, name: '迷雾庄园', price: 88, playerCount: 6 },
      { id: 2, name: '时光旅人', price: 68, playerCount: 5 }
    ]
  }
}

const loadStores = async () => {
  try {
    const res = await getStoreList({ page: 1, pageSize: 100 })
    if (res.data) {
      stores.value = res.data.records || res.data.list || res.data
    }
  } catch (error) {
    console.error('加载门店失败:', error)
    // 模拟数据
    stores.value = [
      { id: 1, name: '探案密室', address: '北京市朝阳区xxx' },
      { id: 2, name: '时空剧本馆', address: '北京市海淀区xxx' }
    ]
  }
}

const handleScriptChange = (scriptId) => {
  const script = scripts.value.find(s => s.id === scriptId)
  if (script) {
    form.playerCount = script.playerCount
  }
  // 加载可用优惠券
  loadAvailableCoupons()
}

const handleCouponChange = (couponId) => {
  selectedCoupon.value = availableCoupons.value.find(c => c.id === couponId)
}

const handleStoreChange = async (storeId) => {
  form.roomId = null
  rooms.value = []
  
  try {
    const res = await getStoreRooms(storeId)
    if (res.data) {
      rooms.value = res.data
    }
  } catch (error) {
    console.error('加载房间失败:', error)
    // 模拟数据
    rooms.value = [
      { id: 1, name: '推理房A', capacity: 6, status: 1 },
      { id: 2, name: '推理房B', capacity: 8, status: 1 }
    ]
  }
}

const loadAvailableCoupons = async () => {
  if (!userStore.userInfo?.id) return
  
  try {
    // 获取用户的未使用优惠券
    const res = await getMyCoupons({ status: 1, page: 1, pageSize: 100 })
    if (res.code === 1 || res.code === 200) {
      availableCoupons.value = res.data.records || []
    }
  } catch (error) {
    console.error('加载优惠券失败:', error)
  }
}

const checkAvailability = async () => {
  if (!form.roomId || !form.reservationDate || !form.reservationTime) {
    return true
  }
  
  checkingAvailability.value = true
  try {
    const res = await checkRoomAvailability({
      roomId: form.roomId,
      reservationTime: `${form.reservationDate} ${form.reservationTime}`,
      duration: selectedScript.value?.duration || 3
    })
    
    if (res.data === false || res.code === 0) {
      ElMessage.warning('该时段房间已被预约，请选择其他时间')
      return false
    }
    return true
  } catch (error) {
    console.error('检查房间可用性失败:', error)
    return true // 检查失败时允许继续，由后端再次验证
  } finally {
    checkingAvailability.value = false
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      // 检查房间可用性
      const isAvailable = await checkAvailability()
      if (!isAvailable) {
        return
      }
      
      submitting.value = true
      try {
        // 计算总价格和实际价格
        const totalAmount = parseFloat(totalPrice.value)
        const actualAmount = parseFloat(finalPrice.value)
        
        // 构建预约数据（只发送后端需要的字段）
        const reservationData = {
          storeId: form.storeId,
          roomId: form.roomId,
          scriptId: form.scriptId,
          playerCount: form.playerCount,
          reservationTime: `${form.reservationDate} ${form.reservationTime}`,
          duration: selectedScript.value?.duration || 3.0,
          totalPrice: totalAmount,
          contactName: form.contactName,
          contactPhone: form.contactPhone,
          remark: form.remark,
          userId: userStore.userInfo?.id,
          userCouponId: form.userCouponId || null
        }
        
        const res = await createReservation(reservationData)
        if (res.code === 1 || res.code === 200) {
          ElMessage.success('预约成功')
          router.push(`/reservation/confirm/${res.data.id || res.data}`)
        }
      } catch (error) {
        console.error('创建预约失败:', error)
        ElMessage.error(error.response?.data?.msg || '创建预约失败，请重试')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 监听房间列表变化，自动选择预约的房间
watch(rooms, (newRooms) => {
  // 如果URL中有roomId参数，自动选中该房间
  if (route.query.roomId && newRooms.length > 0) {
    const roomId = parseInt(route.query.roomId)
    const targetRoom = newRooms.find(r => r.id === roomId)
    
    if (targetRoom) {
      form.roomId = roomId
      
      // 如果房间不可用，给出提示
      if (targetRoom.status !== 1) {
        ElMessage.warning(`房间"${targetRoom.name}"当前不可预约，请选择其他房间`)
      } else {
        const roomName = route.query.roomName || targetRoom.name
        ElMessage.success(`已自动选择房间：${roomName}`)
      }
    }
  }
})

onMounted(async () => {
  // 先加载剧本和门店列表
  await Promise.all([loadScripts(), loadStores()])
  
  // 从URL参数中获取预选值
  if (route.query.scriptId) {
    form.scriptId = parseInt(route.query.scriptId)
    handleScriptChange(form.scriptId)
  }
  
  // 如果有门店参数，先加载门店的房间列表
  if (route.query.storeId) {
    form.storeId = parseInt(route.query.storeId)
    await handleStoreChange(form.storeId)
    // 房间列表加载后，watch会自动处理roomId的选择
  }
  
  // 预填联系信息
  if (userStore.userInfo) {
    form.contactName = userStore.userInfo.name || userStore.userInfo.username
    form.contactPhone = userStore.userInfo.phone || ''
  }
  
  // 如果同时有门店和房间信息，显示提示
  if (route.query.storeId && route.query.roomId) {
    const storeName = stores.value.find(s => s.id === parseInt(route.query.storeId))?.name
    if (storeName) {
      console.log(`从门店"${storeName}"跳转，正在加载房间信息...`)
    }
  }
})
</script>

<style scoped>
.reservation-create-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
}

.reservation-form {
  margin-top: 20px;
}

.hint {
  margin-left: 10px;
  color: #999;
  font-size: 14px;
}

.price-info {
  background: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.price-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 16px;
}

.price-item.total {
  font-size: 20px;
  font-weight: bold;
  padding-top: 10px;
  border-top: 1px dashed #ddd;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}
</style>
