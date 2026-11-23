<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api'
import { useAuthStore } from '@/stores/auth'
import { formatTime, roleMap } from '@/utils/format'

const auth = useAuthStore()
const loading = ref(true)
const keyword = ref('')
const role = ref('')
const pageData = reactive({ list: [], total: 0, page: 1, size: 10 })

const load = async () => {
  loading.value = true
  try {
    const data = await adminApi.users({
      page: pageData.page,
      size: pageData.size,
      keyword: keyword.value || undefined,
      role: role.value || undefined
    })
    pageData.list = data.list
    pageData.total = data.total
  } finally {
    loading.value = false
  }
}

onMounted(load)

const search = () => {
  pageData.page = 1
  load()
}

const isSelf = (row) => row.id === auth.user?.id

const changeRole = async (row, nextRole) => {
  if (nextRole === row.role) return
  try {
    await ElMessageBox.confirm(
      `确定将「${row.nickname || row.username}」的角色改为${(roleMap[nextRole] || {}).label || nextRole}吗？`,
      '修改角色',
      { confirmButtonText: '修改', cancelButtonText: '取消', type: 'warning' }
    )
  } catch {
    await load()
    return
  }
  try {
    await adminApi.updateUserRole(row.id, nextRole)
    row.role = nextRole
    ElMessage.success('角色已更新')
  } catch (e) {
    ElMessage.error(e.message)
    await load()
  }
}

const changeStatus = async (row, active) => {
  const nextStatus = active ? 'ACTIVE' : 'DISABLED'
  try {
    if (!active) {
      await ElMessageBox.confirm(
        `禁用后「${row.nickname || row.username}」将无法登录，确定继续吗？`,
        '禁用账号',
        { confirmButtonText: '禁用', cancelButtonText: '取消', type: 'warning' }
      )
    }
  } catch {
    return
  }
  try {
    await adminApi.updateUserStatus(row.id, nextStatus)
    row.status = nextStatus
    ElMessage.success(active ? '账号已启用' : '账号已禁用')
  } catch (e) {
    ElMessage.error(e.message)
    await load()
  }
}
</script>

<template>
  <div>
    <div class="admin-page-head">
      <div>
        <h2>用户管理</h2>
        <p>查看全部用户、调整角色、启用或禁用账号。管理员不能修改自己的角色与状态。</p>
      </div>
      <div style="display: flex; gap: 10px; flex-wrap: wrap">
        <el-input
          v-model="keyword"
          placeholder="搜索用户名 / 昵称 / 邮箱"
          clearable
          style="width: 220px"
          @keyup.enter="search"
          @clear="search"
        />
        <el-select v-model="role" placeholder="全部角色" clearable style="width: 130px" @change="search">
          <el-option label="管理员" value="ADMIN" />
          <el-option label="计分员" value="SCORER" />
          <el-option label="普通用户" value="USER" />
        </el-select>
        <el-button type="primary" @click="search">搜索</el-button>
      </div>
    </div>

    <div class="admin-panel" v-loading="loading">
      <table class="admin-table">
        <thead>
          <tr>
            <th>用户</th>
            <th>邮箱</th>
            <th>角色</th>
            <th>状态</th>
            <th>最近登录</th>
            <th>注册时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in pageData.list" :key="row.id">
            <td>
              <strong>{{ row.nickname || row.username }}</strong>
              <div class="muted">@{{ row.username }}</div>
            </td>
            <td class="muted">{{ row.email || '—' }}</td>
            <td>
              <el-select
                :model-value="row.role"
                size="small"
                style="width: 110px"
                :disabled="isSelf(row)"
                @change="(value) => changeRole(row, value)"
              >
                <el-option label="管理员" value="ADMIN" />
                <el-option label="计分员" value="SCORER" />
                <el-option label="普通用户" value="USER" />
              </el-select>
            </td>
            <td>
              <el-switch
                :model-value="row.status === 'ACTIVE'"
                :disabled="isSelf(row)"
                inline-prompt
                active-text="启用"
                inactive-text="禁用"
                @change="(value) => changeStatus(row, value)"
              />
            </td>
            <td class="muted">{{ formatTime(row.lastLoginAt) }}</td>
            <td class="muted">{{ formatTime(row.createdAt) }}</td>
          </tr>
          <tr v-if="!pageData.list.length && !loading">
            <td colspan="6" class="muted">没有匹配的用户。</td>
          </tr>
        </tbody>
      </table>

      <div style="display: flex; justify-content: flex-end; margin-top: 16px">
        <el-pagination
          v-model:current-page="pageData.page"
          v-model:page-size="pageData.size"
          :total="pageData.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @current-change="load"
          @size-change="search"
        />
      </div>
    </div>
  </div>
</template>
