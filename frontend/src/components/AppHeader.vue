<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const scrolled = ref(false)

const onScroll = () => {
  scrolled.value = window.scrollY > 8
}

onMounted(() => {
  window.addEventListener('scroll', onScroll, { passive: true })
  onScroll()
})

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
})

const handleCommand = (command) => {
  if (command === 'logout') {
    auth.logout()
    ElMessage.success('已退出登录，欢迎再来')
    router.push('/')
    return
  }
  router.push(command)
}
</script>

<template>
  <header class="app-header" :class="{ scrolled }">
    <div class="container header-inner">
      <router-link to="/" class="brand">
        <span class="brand-ball">🏐</span>
        <span class="brand-text">
          <strong>VOLLEYBALL 3.0</strong>
          <small>排球比赛系统</small>
        </span>
      </router-link>

      <nav class="header-nav">
        <router-link to="/" class="nav-link">首页</router-link>
        <router-link to="/results" class="nav-link">比赛结果</router-link>
        <router-link to="/live" class="nav-link">实时比赛</router-link>
        <router-link v-if="auth.isScorer" to="/admin" class="nav-link">管理后台</router-link>
      </nav>

      <div class="header-actions">
        <template v-if="auth.isLoggedIn">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-trigger">
              <span class="avatar">{{ auth.displayName.slice(0, 1) }}</span>
              <span class="user-meta">
                <strong>{{ auth.displayName }}</strong>
                <span>{{ auth.roleLabel }}</span>
              </span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="/profile">个人中心</el-dropdown-item>
                <el-dropdown-item v-if="auth.isScorer" command="/admin">管理后台</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login" class="btn-ghost">登录</router-link>
          <router-link to="/register" class="btn-primary">加入我们</router-link>
        </template>
      </div>
    </div>
  </header>
</template>
