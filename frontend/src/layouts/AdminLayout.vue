<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const navItems = computed(() => {
  const items = [
    { to: '/admin/dashboard', label: '管理看板', icon: 'DataLine', admin: true },
    { to: '/admin/matches', label: '比赛管理', icon: 'Trophy' },
    { to: '/admin/teams', label: '队伍管理', icon: 'UserFilled' },
    { to: '/admin/announcements', label: '公告管理', icon: 'Bell', admin: true },
    { to: '/admin/users', label: '用户管理', icon: 'User', admin: true },
    { to: '/admin/applications', label: '申请审批', icon: 'Tickets', admin: true },
    { to: '/admin/logs', label: '系统检测', icon: 'Monitor', admin: true }
  ]
  return items.filter((item) => !item.admin || auth.isAdmin)
})

const pageTitle = computed(() => route.meta.title || '管理后台')

const logout = () => {
  auth.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}
</script>

<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <router-link to="/admin" class="admin-brand">
        <span class="brand-ball">🏐</span>
        <span>
          <strong>VOLLEYBALL 3.0</strong>
          <span>CONTROL CENTER</span>
        </span>
      </router-link>

      <nav class="admin-nav">
        <router-link v-for="item in navItems" :key="item.to" :to="item.to">
          <el-icon><component :is="item.icon" /></el-icon>
          {{ item.label }}
        </router-link>
      </nav>

      <div class="admin-sidebar-foot">
        <div>{{ auth.roleLabel }} · {{ auth.displayName }}</div>
        <div>比赛进行中时，仅创建者可计分</div>
      </div>
    </aside>

    <div class="admin-main">
      <header class="admin-topbar">
        <div>
          <h1>{{ pageTitle }}</h1>
          <div class="crumb">管理后台 / {{ pageTitle }}</div>
        </div>
        <div class="admin-topbar-actions">
          <el-tag v-if="auth.isAdmin" type="danger" effect="light" round>管理员</el-tag>
          <el-tag v-else type="warning" effect="light" round>计分员</el-tag>
          <el-button text @click="router.push('/')">返回前台</el-button>
          <el-button text @click="logout">退出</el-button>
        </div>
      </header>

      <main class="admin-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-up" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>
