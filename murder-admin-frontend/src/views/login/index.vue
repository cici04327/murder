<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">剧本杀管理后台</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <!-- 滑块验证码 -->
        <el-form-item>
          <SliderCaptcha 
            ref="captchaRef"
            @success="handleCaptchaSuccess"
            @fail="handleCaptchaFail"
            @refresh="handleCaptchaRefresh"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="loading"
            :disabled="!captchaVerified"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userService } from '@/utils/request'
import SliderCaptcha from '@/components/SliderCaptcha.vue'

const router = useRouter()
const loginFormRef = ref(null)
const captchaRef = ref(null)
const loading = ref(false)
const captchaVerified = ref(false)

const loginForm = reactive({
  username: 'test_user',
  password: '123456'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 验证码成功回调
const handleCaptchaSuccess = () => {
  captchaVerified.value = true
  ElMessage.success('验证成功')
}

// 验证码失败回调
const handleCaptchaFail = () => {
  captchaVerified.value = false
  ElMessage.error('验证失败，请重试')
}

// 验证码刷新回调
const handleCaptchaRefresh = () => {
  captchaVerified.value = false
}

const handleLogin = async () => {
  // 检查验证码
  if (!captchaVerified.value) {
    ElMessage.warning('请先完成安全验证')
    return
  }
  
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    // 使用管理员登录接口
    const res = await userService.post('/user/admin/login', loginForm)
    
    if (res.code === 1 || res.code === 200) {
      localStorage.setItem('admin-token', res.data.token)
      localStorage.setItem('admin-user', JSON.stringify(res.data))
      ElMessage.success('登录成功')
      
      // 延迟跳转，确保 token 已保存
      setTimeout(() => {
        router.push('/').catch(err => {
          console.error('路由跳转失败:', err)
          // 如果路由跳转失败，使用强制刷新
          window.location.href = '/'
        })
      }, 100)
    } else {
      ElMessage.error(res.msg || '登录失败')
      // 登录失败，重置验证码
      captchaVerified.value = false
      if (captchaRef.value) {
        captchaRef.value.reset()
      }
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error(error.message || '登录异常')
    // 登录失败，重置验证码
    captchaVerified.value = false
    if (captchaRef.value) {
      captchaRef.value.reset()
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
}

.login-form {
  margin-top: 20px;
}
</style>
