import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 用户服务专用实例（用于通知等用户相关功能）
export const userService = axios.create({
  baseURL: '/user-api',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('admin-token')
    if (token) {
      // 管理员登录时使用admin密钥生成token，使用admin-token请求头
      config.headers['admin-token'] = token
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 用户服务请求拦截器
userService.interceptors.request.use(
  config => {
    const token = localStorage.getItem('admin-token')
    if (token) {
      // 管理员登录时使用admin密钥生成token，使用admin-token请求头
      config.headers['admin-token'] = token
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 成功的状态码：1 或 200
    if (res.code !== 1 && res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      
      // 401: 未授权，跳转登录页
      if (res.code === 401) {
        localStorage.removeItem('admin-token')
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    
    return res
  },
  error => {
    console.error('响应错误:', error)
    
    if (error.response?.status === 429) {
      ElMessage.error('请求过于频繁，请稍后再试')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    
    return Promise.reject(error)
  }
)

// 用户服务响应拦截器
userService.interceptors.response.use(
  response => {
    const res = response.data
    
    if (res.code !== 1 && res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      
      if (res.code === 401) {
        localStorage.removeItem('admin-token')
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    
    return res
  },
  error => {
    console.error('响应错误:', error)
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default service
