<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>欢迎登录</h2>
        <p>剧本杀在线预约平台</p>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名/手机号"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
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
            :loading="loading"
            :disabled="!captchaVerified"
            @click="handleLogin"
            style="width: 100%"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <span>还没有账号？</span>
        <el-link type="primary" @click="router.push('/register')">立即注册</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import SliderCaptcha from '@/components/SliderCaptcha.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref(null)
const captchaRef = ref(null)
const loading = ref(false)
const captchaVerified = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
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
  if (!loginFormRef.value) return
  
  // 检查验证码
  if (!captchaVerified.value) {
    ElMessage.warning('请先完成安全验证')
    return
  }
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const success = await userStore.login(loginForm)
        if (success) {
          ElMessage.success('登录成功')
          // 登录成功，跳转到之前的页面或首页
          const redirect = route.query.redirect || '/home'
          router.push(redirect)
        } else {
          // 登录失败，重置验证码
          captchaVerified.value = false
          if (captchaRef.value) {
            captchaRef.value.reset()
          }
        }
      } catch (error) {
        // 登录失败，重置验证码
        captchaVerified.value = false
        if (captchaRef.value) {
          captchaRef.value.reset()
        }
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 450px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  font-size: 28px;
  color: #333;
  margin: 0 0 10px;
}

.login-header p {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.login-form {
  margin-top: 30px;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.login-footer span {
  margin-right: 5px;
}
</style>
