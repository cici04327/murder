import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    host: '0.0.0.0',
    hmr: {
      protocol: 'ws',
      host: 'localhost',
      port: 3000,
      clientPort: 3000,
      overlay: false  // 禁用错误遮罩层
    },
    proxy: {
      // 所有API请求统一通过网关转发
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true  // 支持WebSocket
      },
      // 用户服务代理（通知系统）- 也通过网关
      '/user-api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true  // 支持WebSocket
      },
      // 预约服务代理
      '/reservation-api': {
        target: 'http://localhost:8085',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/reservation-api/, '')
      }
    }
  }
})
