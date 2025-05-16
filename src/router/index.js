// router.js
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name:'index',
    component: () => import('../components/index.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../components/Register.vue')
  },
  {
    path: '/login',
    name:'login',
    component: () => import('../components/Login.vue')
  },
  {
    path: '/chat',
    name:'chat',
    component: () => import('../components/ChatRoom.vue')
  }

]

const router = createRouter({
  // 使用HTML5历史模式
  history: createWebHistory(),
  routes
})

export default router