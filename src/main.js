// main.js
import { createApp } from 'vue'
import App from './App.vue'
import router from './router/index.js'

// 创建Vue 3应用实例
const app = createApp(App)

// 使用路由插件
app.use(router)

// 挂载应用到DOM
app.mount('#app')