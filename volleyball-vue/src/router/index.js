import { createRouter, createWebHashHistory } from 'vue-router'
import Login from '../views/Login.vue'
import MatchList from '../views/MatchList.vue'
import Recording from '../views/Recording.vue'
import { api } from '../api'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'login', component: Login, meta: { guestOnly: true } },
  { path: '/matchs', name: 'matchs', component: MatchList, meta: { auth: true } },
  { path: '/recording/:id', name: 'recording', component: Recording, meta: { auth: true } },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

// 会话校验：刷新页面后仍能凭 JSESSIONID 保持登录态
router.beforeEach(async (to) => {
  if (!to.meta.auth) return true
  try {
    const res = await api.currentUser()
    if (res && res.success) return true
  } catch {
    /* 忽略，按未登录处理 */
  }
  return { name: 'login', query: { redirect: to.fullPath } }
})

export default router