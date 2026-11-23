<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { applicationApi, authApi } from '@/api'
import { useAuthStore } from '@/stores/auth'
import {
  applicationStatusMap,
  applicationTypeMap,
  formatTime,
  roleMap
} from '@/utils/format'

const auth = useAuthStore()
const applications = ref([])
const loading = ref(true)

const profileForm = reactive({ nickname: '', email: '' })
const profileSaving = ref(false)
const passwordRef = ref()
const passwordSaving = ref(false)
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const applyForm = reactive({ type: '', reason: '' })
const applying = ref(false)

watch(
  () => auth.user,
  (user) => {
    if (user) {
      profileForm.nickname = user.nickname || ''
      profileForm.email = user.email || ''
    }
  },
  { immediate: true }
)

const roleInfo = computed(() => roleMap[auth.role] || { label: '游客', type: 'info' })

const applyOptions = computed(() => {
  if (auth.role === 'USER') {
    return [
      { value: 'SCORER', label: '申请成为计分员' },
      { value: 'ADMIN', label: '申请成为管理员' }
    ]
  }
  if (auth.role === 'SCORER') {
    return [{ value: 'ADMIN', label: '申请成为管理员' }]
  }
  return []
})

const pendingTypes = computed(
  () =>
    new Set(
      applications.value.filter((item) => item.status === 'PENDING').map((item) => item.type)
    )
)

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度为 6-32 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) callback(new Error('两次输入的密码不一致'))
        else callback()
      },
      trigger: 'blur'
    }
  ]
}

const loadApplications = async () => {
  try {
    applications.value = await applicationApi.mine()
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

onMounted(loadApplications)

const saveProfile = async () => {
  profileSaving.value = true
  try {
    await auth.updateProfile({ ...profileForm })
    ElMessage.success('个人资料已更新')
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    profileSaving.value = false
  }
}

const changePassword = async () => {
  const valid = await passwordRef.value?.validate().catch(() => false)
  if (!valid) return
  passwordSaving.value = true
  try {
    await authApi.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请牢记新密码')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    passwordRef.value?.resetFields()
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    passwordSaving.value = false
  }
}

const submitApply = async () => {
  if (!applyForm.type) {
    ElMessage.warning('请选择申请类型')
    return
  }
  applying.value = true
  try {
    await applicationApi.apply({ type: applyForm.type, reason: applyForm.reason })
    ElMessage.success('申请已提交，请等待管理员审批')
    applyForm.reason = ''
    await loadApplications()
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    applying.value = false
  }
}
</script>

<template>
  <div class="container page">
    <div class="page-head">
      <div>
        <span class="eyebrow">My Space</span>
        <h1 class="page-title">个人中心</h1>
        <p class="page-sub">管理你的资料、密码与角色申请。</p>
      </div>
      <router-link to="/" class="btn-ghost">返回首页</router-link>
    </div>

    <div class="profile-grid">
      <div>
        <div class="profile-card" v-reveal>
          <div class="profile-avatar">{{ auth.displayName.slice(0, 1) }}</div>
          <h2>{{ auth.displayName }}</h2>
          <div class="username">@{{ auth.user?.username }}</div>
          <div class="profile-meta">
            <span>{{ roleInfo.label }}</span>
            <span>{{ auth.user?.email || '未填写邮箱' }}</span>
          </div>
          <div style="margin-top: 24px; font-size: 12px; color: rgba(255, 255, 255, 0.55); line-height: 1.9">
            <div>注册于 {{ formatTime(auth.user?.createdAt) }}</div>
            <div>最近登录 {{ formatTime(auth.user?.lastLoginAt) }}</div>
          </div>
        </div>

        <div class="panel" style="margin-top: 18px">
          <div class="panel-title">
            <h3>角色说明</h3>
          </div>
          <div style="font-size: 13px; color: var(--muted); line-height: 2">
            <p>· 普通用户：浏览公告、赛果，实时观看比赛。</p>
            <p>· 计分员：创建比赛、记录比分、管理队伍与队员。</p>
            <p>· 管理员：拥有全部权限，管理公告、用户与系统。</p>
          </div>
        </div>
      </div>

      <div>
        <div class="panel">
          <div class="panel-title">
            <h3>基本资料</h3>
            <span class="tag">{{ roleInfo.label }}</span>
          </div>
          <el-form label-position="top">
            <el-row :gutter="16">
              <el-col :xs="24" :sm="12">
                <el-form-item label="昵称">
                  <el-input v-model="profileForm.nickname" maxlength="20" placeholder="展示名称" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="邮箱">
                  <el-input v-model="profileForm.email" placeholder="联系邮箱" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-button type="primary" :loading="profileSaving" @click="saveProfile">保存资料</el-button>
          </el-form>
        </div>

        <div class="panel" style="margin-top: 18px">
          <div class="panel-title">
            <h3>修改密码</h3>
          </div>
          <el-form ref="passwordRef" :model="passwordForm" :rules="passwordRules" label-position="top">
            <el-row :gutter="16">
              <el-col :xs="24" :sm="8">
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input v-model="passwordForm.oldPassword" type="password" show-password />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="8">
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passwordForm.newPassword" type="password" show-password />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="8">
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
                </el-form-item>
              </el-col>
            </el-row>
            <el-button :loading="passwordSaving" @click="changePassword">更新密码</el-button>
          </el-form>
        </div>

        <div v-if="applyOptions.length" class="panel" style="margin-top: 18px">
          <div class="panel-title">
            <h3>角色申请</h3>
            <span class="tag">当前 {{ roleInfo.label }}</span>
          </div>
          <el-form label-position="top">
            <el-row :gutter="16">
              <el-col :xs="24" :sm="10">
                <el-form-item label="申请类型">
                  <el-select v-model="applyForm.type" placeholder="选择申请类型" style="width: 100%">
                    <el-option
                      v-for="option in applyOptions"
                      :key="option.value"
                      :label="option.label"
                      :value="option.value"
                      :disabled="pendingTypes.has(option.value)"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="14">
                <el-form-item label="申请理由">
                  <el-input v-model="applyForm.reason" maxlength="200" placeholder="简单说明你的经验与意愿" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-button type="primary" :loading="applying" @click="submitApply">提交申请</el-button>
          </el-form>
        </div>

        <div class="panel" style="margin-top: 18px">
          <div class="panel-title">
            <h3>我的申请</h3>
            <span class="tag gray">{{ applications.length }} 条</span>
          </div>
          <div v-if="applications.length" class="timeline">
            <div v-for="item in applications" :key="item.id" class="timeline-item">
              <div class="tl-head">
                <span>{{ applicationTypeMap[item.type] || item.type }}</span>
                <el-tag
                  :type="(applicationStatusMap[item.status] || {}).type || 'info'"
                  size="small"
                  effect="light"
                  round
                >
                  {{ (applicationStatusMap[item.status] || {}).label || item.status }}
                </el-tag>
              </div>
              <p>理由：{{ item.reason || '未填写' }}</p>
              <p>提交于 {{ formatTime(item.createdAt) }}</p>
              <p v-if="item.handleRemark">审批备注：{{ item.handleRemark }}</p>
            </div>
          </div>
          <p v-else-if="!loading" style="color: var(--muted); font-size: 13px">暂无申请记录。</p>
        </div>
      </div>
    </div>
  </div>
</template>
