<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validateConfirm = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]{4,20}$/, message: '4-20 位字母、数字或下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度为 6-32 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
}

const submit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await auth.register({
      username: form.username,
      password: form.password,
      nickname: form.nickname,
      email: form.email
    })
    ElMessage.success('注册成功，欢迎加入排球社区')
    router.push('/')
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
        <h1>加入我们，<br />让赛场更热烈。</h1>
        <p>普通用户可以实时观看比赛；想参与计分？注册后可在个人中心申请成为计分员或管理员。</p>
        <ul>
          <li>免费注册，立即可用</li>
          <li>实时比分与赛程提醒</li>
          <li>申请角色，参与赛事运营</li>
        </ul>
      </div>
    </section>

    <section class="auth-panel">
      <div class="auth-card">
        <h2>注册</h2>
        <p class="auth-sub">创建账号，开始你的排球旅程。</p>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="submit">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="4-20 位字母、数字或下划线" size="large" />
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="form.nickname" placeholder="选填，展示在页面右上角" size="large" maxlength="20" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="选填，用于联系" size="large" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="6-32 位密码" size="large" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="再次输入密码"
              size="large"
              show-password
              @keyup.enter="submit"
            />
          </el-form-item>
          <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="submit">
            创建账号
          </el-button>
        </el-form>
        <div class="auth-switch">
          已有账号？<router-link to="/login">直接登录</router-link>
          <span style="margin: 0 8px; color: var(--line)">|</span>
          <router-link to="/">返回首页</router-link>
        </div>
      </div>
    </section>
  </div>
</template>
