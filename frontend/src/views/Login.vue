<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const fillDemo = (username, password) => {
  form.username = username
  form.password = password
}

const submit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await auth.login(form)
    ElMessage.success(`欢迎回来，${auth.displayName}`)
    const redirect = route.query.redirect
    if (redirect) {
      router.push(String(redirect))
    } else {
      router.push(auth.isScorer ? '/admin' : '/')
    }
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-shell">
    <section class="auth-stage">
      <div class="auth-stage-copy">
        <div class="ball-big" />
        <h1>回到赛场，<br />每一分都算数。</h1>
        <p>登录后可实时观看比赛、申请成为计分员。计分员可以创建比赛、管理队伍，并拥有全部用户权限。</p>
        <ul>
          <li>游客可浏览公告与比赛结果</li>
          <li>普通用户可实时观看进行中的比赛</li>
          <li>计分员与管理员拥有专属控制台</li>
        </ul>
      </div>
    </section>

    <section class="auth-panel">
      <div class="auth-card">
        <h2>登录</h2>
        <p class="auth-sub">欢迎回来，继续关注排球赛场。</p>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="submit">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" size="large" autocomplete="username">
              <template #prefix><el-icon><User /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
              autocomplete="current-password"
              @keyup.enter="submit"
            >
              <template #prefix><el-icon><Lock /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="submit">
            登录
          </el-button>
        </el-form>

        <div class="demo-accounts">
          <strong>演示账号</strong>（点击自动填充）<br />
          <a href="#" @click.prevent="fillDemo('admin', 'admin123')">管理员 admin / admin123</a> ·
          <a href="#" @click.prevent="fillDemo('scorer', 'scorer123')">计分员 scorer / scorer123</a> ·
          <a href="#" @click.prevent="fillDemo('user', 'user123')">用户 user / user123</a>
        </div>

        <div class="auth-switch">
          还没有账号？<router-link to="/register">立即注册</router-link>
          <span style="margin: 0 8px; color: var(--line)">|</span>
          <router-link to="/">返回首页</router-link>
        </div>
      </div>
    </section>
  </div>
</template>
