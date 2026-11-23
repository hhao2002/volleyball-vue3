import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/PublicLayout.vue'),
    children: [
      { path: '', name: 'home', component: () => import('@/views/Home.vue'), meta: { title: '首页' } },
      {
        path: 'results',
        name: 'results',
        component: () => import('@/views/Results.vue'),
        meta: { title: '比赛结果' }
      },
      {
        path: 'results/:id',
        name: 'result-detail',
        component: () => import('@/views/ResultDetail.vue'),
        meta: { title: '赛果详情' }
      },
      {
        path: 'live',
        name: 'live-list',
        component: () => import('@/views/LiveList.vue'),
        meta: { title: '实时比赛', auth: true, roles: ['USER', 'SCORER', 'ADMIN'] }
      },
      {
        path: 'live/:id',
        name: 'live-watch',
        component: () => import('@/views/LiveWatch.vue'),
        meta: { title: '实时观赛', auth: true, roles: ['USER', 'SCORER', 'ADMIN'] }
      },
      {
        path: 'profile',
        name: 'profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心', auth: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', guestOnly: true }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', guestOnly: true }
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { auth: true, roles: ['ADMIN', 'SCORER'] },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      {
        path: 'dashboard',
        name: 'admin-dashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '管理看板', roles: ['ADMIN'] }
      },
      {
        path: 'matches',
        name: 'admin-matches',
        component: () => import('@/views/admin/AdminMatches.vue'),
        meta: { title: '比赛管理' }
      },
      {
        path: 'matches/:id/score',
        name: 'score-console',
        component: () => import('@/views/admin/ScoreConsole.vue'),
        meta: { title: '计分台' }
      },
      {
        path: 'teams',
        name: 'admin-teams',
        component: () => import('@/views/admin/AdminTeams.vue'),
        meta: { title: '队伍管理' }
      },
      {
        path: 'announcements',
        name: 'admin-announcements',
        component: () => import('@/views/admin/AdminAnnouncements.vue'),
        meta: { title: '公告管理', roles: ['ADMIN'] }
      },
      {
        path: 'users',
        name: 'admin-users',
        component: () => import('@/views/admin/AdminUsers.vue'),
        meta: { title: '用户管理', roles: ['ADMIN'] }
      },
      {
        path: 'applications',
        name: 'admin-applications',
        component: () => import('@/views/admin/AdminApplications.vue'),
        meta: { title: '申请审批', roles: ['ADMIN'] }
      },
      {
        path: 'logs',
        name: 'admin-logs',
        component: () => import('@/views/admin/AdminLogs.vue'),
        meta: { title: '系统检测', roles: ['ADMIN'] }
      }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.auth && !auth.isLoggedIn) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.meta.guestOnly && auth.isLoggedIn) {
    return auth.isScorer ? '/admin' : '/'
  }
  const roles = to.meta.roles
  if (roles && !roles.includes(auth.role)) {
    if (auth.isScorer) return { path: '/admin/matches' }
    return { path: '/' }
  }
  document.title = `${to.meta.title || '排球'} · 排球比赛系统 3.0`
})

export default router
