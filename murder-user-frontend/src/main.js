import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
import { registerServiceWorker, showInstallPrompt } from './utils/pwa'
import { registerLazyDirective } from './directives/lazy'
import './styles/animations.css'
import './styles/background-themes.css'

// 创建应用实例
const app = createApp(App)

// 先注册 pinia（状态管理）
const pinia = createPinia()
app.use(pinia)

// 然后注册路由（路由守卫中需要使用 pinia）
app.use(router)

// 注册 Element Plus
app.use(ElementPlus)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册图片懒加载指令
registerLazyDirective(app)

// 挂载应用
app.mount('#app')

// 注册PWA（应用挂载后）
registerServiceWorker()
showInstallPrompt()
