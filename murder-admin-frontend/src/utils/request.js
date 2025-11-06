import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000,
  // 添加重试配置
  retryDelay: 1000,
  retry: 3
})

// 用户服务专用实例（用于通知等用户相关功能）
export const userService = axios.create({
  baseURL: '/user-api',
  timeout: 10000,
  // 添加重试配置
  retryDelay: 1000,
  retry: 3
})

// 预约服务专用实例
export const reservationService = axios.create({
  baseURL: '/reservation-api',
  timeout: 10000,
  // 添加重试配置
  retryDelay: 1000,
  retry: 3
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
  async error => {
    console.error('响应错误:', error)
    
    const config = error.config
    
    // 如果是网络错误且配置了重试
    if (error.code === 'ERR_NETWORK' && config && config.retry) {
      config.__retryCount = config.__retryCount || 0
      
      if (config.__retryCount < config.retry) {
        config.__retryCount++
        console.log(`请求失败，正在重试 (${config.__retryCount}/${config.retry})...`)
        
        // 延迟后重试
        await new Promise(resolve => setTimeout(resolve, config.retryDelay || 1000))
        return service.request(config)
      }
    }
    
    // 只在所有重试都失败后才显示错误消息
    if (error.response?.status === 429) {
      ElMessage.error('请求过于频繁，请稍后再试')
    } else if (error.code === 'ERR_NETWORK') {
      ElMessage.error('网络连接失败，请检查网络或稍后重试')
    } else if (!error.config?.__retryCount) {
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
  async error => {
    console.error('响应错误:', error)
    
    const config = error.config
    
    // 如果是网络错误且配置了重试
    if (error.code === 'ERR_NETWORK' && config && config.retry) {
      config.__retryCount = config.__retryCount || 0
      
      if (config.__retryCount < config.retry) {
        config.__retryCount++
        console.log(`用户服务请求失败，正在重试 (${config.__retryCount}/${config.retry})...`)
        
        // 延迟后重试
        await new Promise(resolve => setTimeout(resolve, config.retryDelay || 1000))
        return userService.request(config)
      }
    }
    
    // 只在所有重试都失败后才显示错误消息
    if (error.code === 'ERR_NETWORK') {
      ElMessage.error('网络连接失败，请检查网络或稍后重试')
    } else if (!error.config?.__retryCount) {
      ElMessage.error(error.message || '网络错误')
    }
    
    return Promise.reject(error)
  }
)

// 预约服务请求拦截器
reservationService.interceptors.request.use(
  config => {
    const token = localStorage.getItem('admin-token')
    if (token) {
      config.headers['admin-token'] = token
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 预约服务响应拦截器
reservationService.interceptors.response.use(
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
  async error => {
    console.error('响应错误:', error)
    
    const config = error.config
    
    // 如果是网络错误且配置了重试
    if (error.code === 'ERR_NETWORK' && config && config.retry) {
      config.__retryCount = config.__retryCount || 0
      
      if (config.__retryCount < config.retry) {
        config.__retryCount++
        console.log(`预约服务请求失败，正在重试 (${config.__retryCount}/${config.retry})...`)
        
        // 延迟后重试
        await new Promise(resolve => setTimeout(resolve, config.retryDelay || 1000))
        return reservationService.request(config)
      }
    }
    
    // 只在所有重试都失败后才显示错误消息
    if (error.code === 'ERR_NETWORK') {
      ElMessage.error('网络连接失败，请检查网络或稍后重试')
    } else if (!error.config?.__retryCount) {
      ElMessage.error(error.message || '网络错误')
    }
    
    return Promise.reject(error)
  }
)

export default service
