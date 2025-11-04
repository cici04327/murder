import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    
    // 添加token - 后端拦截器期望的请求头名称是 'token'
    if (userStore.token) {
      config.headers['token'] = userStore.token
    }
    
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码为200，说明接口请求成功，可以正常拿到数据
    // 否则的话抛出错误
    if (res.code === 1 || res.code === 200) {
      return res
    } else {
      ElMessage.error(res.msg || res.message || '请求失败')
      return Promise.reject(new Error(res.msg || res.message || '请求失败'))
    }
  },
  error => {
    console.error('响应错误:', error)
    
    // 静默处理的错误（不显示错误消息）
    const silentErrors = [
      '/script/review/page',        // 评价列表可能为空
      '/script/favorite/',           // 收藏状态检查
      '/article/comments'            // 评论列表可能为空
    ]
    
    const url = error.config?.url || ''
    const isSilent = silentErrors.some(path => url.includes(path))
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          if (!isSilent) {
            ElMessage.error('登录已过期，请重新登录')
          }
          const userStore = useUserStore()
          userStore.logout()
          router.push('/login')
          break
        case 403:
          if (!isSilent) {
            ElMessage.error('没有权限访问')
          }
          break
        case 404:
          // 404 错误静默处理，只在控制台显示
          console.warn('资源不存在:', url)
          break
        case 500:
          if (!isSilent) {
            ElMessage.error('服务器错误，请稍后重试')
          }
          break
        default:
          if (!isSilent) {
            ElMessage.error(data.msg || data.message || '请求失败')
          }
      }
    } else if (error.message.includes('timeout')) {
      if (!isSilent) {
        ElMessage.error('请求超时，请检查网络')
      }
    } else if (error.message.includes('Network Error')) {
      if (!isSilent) {
        ElMessage.error('网络错误，请检查网络连接')
      }
    } else {
      if (!isSilent) {
        ElMessage.error('请求失败，请稍后重试')
      }
    }
    
    return Promise.reject(error)
  }
)

export default request
