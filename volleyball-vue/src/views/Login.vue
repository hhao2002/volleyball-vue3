<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'

const router = useRouter()
const username = ref('admin')
const password = ref('')
const loading = ref(false)
const error = ref('')

onMounted(async () => {
  // 已登录过就直接跳走，省一次输入
  try {
    const res = await api.currentUser()
    if (res && res.success) router.replace('/matchs')
  } catch {
    /* 忽略 */
  }
})

async function submit() {
  error.value = ''
  if (!username.value.trim() || !password.value) {
    error.value = '请输入用户名和密码'
    return
  }
  loading.value = true
  try {
    const res = await api.login(username.value.trim(), password.value)
    if (res && res.success) {
      router.push('/matchs')
    } else {
      error.value = (res && res.msg) || '登录失败'
    }
  } catch (e) {
    error.value = e.message || '网络异常，请确认后端已启动'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-wrap">
    <form class="login-card" @submit.prevent="submit">
      <div class="login-head">
        <div class="ball"></div>
        <h1>排球计分系统</h1>
        <p class="muted">Volleyball Scoring System</p>
      </div>

      <div v-if="error" class="alert error">{{ error }}</div>

      <div class="field">
        <label for="u">用户名</label>
        <input id="u" v-model="username" autocomplete="username" placeholder="请输入用户名" />
      </div>
      <div class="field">
        <label for="p">密码</label>
        <input
          id="p"
          v-model="password"
          type="password"
          autocomplete="current-password"
          placeholder="请输入密码"
          @keyup.enter="submit"
        />
      </div>

      <button class="btn primary login-btn" type="submit" :disabled="loading">
        {{ loading ? '登录中…' : '登 录' }}
      </button>

      <p class="hint center">默认管理员账号 admin / admin66</p>
    </form>
  </div>
</template>

<style scoped>
.login-wrap {
  min-height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.login-card {
  width: 100%;
  max-width: 380px;
  background: #fbfaea;
  border: 1px solid #d9cf9a;
  border-radius: 16px;
  box-shadow: 0 10px 34px rgba(109, 76, 61, 0.16);
  padding: 30px 26px 22px;
}

.login-head {
  text-align: center;
  margin-bottom: 22px;
}

.login-head h1 {
  font-size: 21px;
  color: #6d4c3d;
  letter-spacing: 2px;
  margin-top: 10px;
}

.login-head p {
  margin: 6px 0 0;
  letter-spacing: 1px;
}

.ball {
  width: 46px;
  height: 46px;
  margin: 0 auto;
  border-radius: 50%;
  background: radial-gradient(circle at 32% 28%, #fff, #f4d03f 42%, #c99a1e 100%);
  box-shadow: inset -4px -6px 10px rgba(150, 105, 10, 0.28);
}

.login-btn {
  width: 100%;
  padding: 11px;
  font-size: 15px;
  letter-spacing: 3px;
  margin-top: 4px;
}

.center {
  text-align: center;
  margin-top: 14px;
}
</style>