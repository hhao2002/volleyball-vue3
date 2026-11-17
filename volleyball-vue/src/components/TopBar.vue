<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'

const router = useRouter()
const user = ref(null)

onMounted(async () => {
  try {
    const res = await api.currentUser()
    if (res && res.success) user.value = res.user
    else router.replace('/login')
  } catch {
    router.replace('/login')
  }
})

async function logout() {
  try {
    await api.logout()
  } finally {
    router.replace('/login')
  }
}
</script>

<template>
  <header class="topbar">
    <div class="brand">
      <span class="dot"></span>
      <span>排球计分系统</span>
    </div>
    <nav>
      <RouterLink to="/matchs">比赛列表</RouterLink>
      <span v-if="user" class="user">{{ user.username }}（{{ user.role }}）</span>
      <button class="btn ghost logout" type="button" @click="logout">退出</button>
    </nav>
  </header>
</template>

<style scoped>
.logout {
  padding: 4px 12px;
  font-size: 13px;
  color: #f0e6cf;
  border-color: rgba(240, 230, 207, 0.5);
}

.logout:hover {
  background: #f0e6cf;
  color: #6d4c3d;
}
</style>