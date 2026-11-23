import { defineStore } from 'pinia'
import { authApi } from '@/api'

const TOKEN_KEY = 'vb_token'
const USER_KEY = 'vb_user'

function readUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    user: readUser(),
    refreshing: false
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token),
    role: (state) => state.user?.role || '',
    isAdmin: (state) => state.user?.role === 'ADMIN',
    isScorer: (state) => ['ADMIN', 'SCORER'].includes(state.user?.role),
    isGuest: (state) => !state.token,
    displayName: (state) => state.user?.nickname || state.user?.username || '球友',
    roleLabel: (state) => {
      const map = { ADMIN: '管理员', SCORER: '计分员', USER: '普通用户' }
      return map[state.user?.role] || '游客'
    }
  },
  actions: {
    setSession({ token, user }) {
      this.token = token
      this.user = user
      localStorage.setItem(TOKEN_KEY, token)
      localStorage.setItem(USER_KEY, JSON.stringify(user))
    },
    async login(payload) {
      const data = await authApi.login(payload)
      this.setSession(data)
      return data
    },
    async register(payload) {
      const data = await authApi.register(payload)
      this.setSession(data)
      return data
    },
    async refresh() {
      if (!this.token || this.refreshing) return
      this.refreshing = true
      try {
        const user = await authApi.me()
        this.user = user
        localStorage.setItem(USER_KEY, JSON.stringify(user))
      } catch {
        // 令牌失效时由 http 拦截器统一跳转
      } finally {
        this.refreshing = false
      }
    },
    async updateProfile(payload) {
      const user = await authApi.updateProfile(payload)
      this.user = user
      localStorage.setItem(USER_KEY, JSON.stringify(user))
      return user
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    }
  }
})
