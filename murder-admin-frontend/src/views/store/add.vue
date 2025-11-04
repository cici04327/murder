<template>
  <div class="store-add">
    <el-card>
      <template #header>新增门店</template>
      <el-form label-width="100px" :model="form" :rules="rules" ref="formRef" @submit.prevent>
        <el-form-item label="门店名称">
          <el-input v-model="form.name" placeholder="请输入门店名称" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="form.phone" placeholder="请输入联系方式" />
        </el-form-item>
        <el-form-item label="营业时间">
          <el-time-picker v-model="form.openTime" placeholder="开门时间" format="HH:mm:ss" value-format="HH:mm:ss" />
          <span style="margin: 0 8px">-</span>
          <el-time-picker v-model="form.closeTime" placeholder="关门时间" format="HH:mm:ss" value-format="HH:mm:ss" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({
  name: '',
  address: '',
  phone: '',
  description: '',
  openTime: '09:00:00',
  closeTime: '22:00:00',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入门店名称', trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    { pattern: /^(1[3-9]\d{9}|0\d{2,3}-?\d{7,8})$/, message: '请输入合法的手机号或座机号', trigger: 'blur' }
  ],
  openTime: [{ required: true, message: '请选择开门时间', trigger: 'change' }],
  closeTime: [{ required: true, message: '请选择关门时间', trigger: 'change' }]
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (form.openTime && form.closeTime && form.openTime >= form.closeTime) {
      ElMessage.error('开门时间必须早于关门时间')
      return
    }
    submitting.value = true
    await request.post('/store', form)
    ElMessage.success('门店新增成功')
    router.push('/store/list')
  } catch (e) {
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.store-add { width: 100%; }
</style>
