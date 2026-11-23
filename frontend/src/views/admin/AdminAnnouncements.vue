<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api'
import { announcementTypeMap, formatTime } from '@/utils/format'
import EmptyState from '@/components/EmptyState.vue'

const list = ref([])
const loading = ref(true)
const dialogVisible = ref(false)
const saving = ref(false)
const editingId = ref(null)
const form = reactive({ title: '', content: '', pinned: false })

const load = async () => {
  loading.value = true
  try {
    list.value = await adminApi.announcements()
  } finally {
    loading.value = false
  }
}

onMounted(load)

const openCreate = () => {
  editingId.value = null
  form.title = ''
  form.content = ''
  form.pinned = false
  dialogVisible.value = true
}

const openEdit = (item) => {
  editingId.value = item.id
  form.title = item.title
  form.content = item.content
  form.pinned = Boolean(item.pinned)
  dialogVisible.value = true
}

const save = async () => {
  if (!form.title.trim()) {
    ElMessage.warning('请输入公告标题')
    return
  }
  saving.value = true
  try {
    if (editingId.value) {
      await adminApi.updateAnnouncement(editingId.value, { ...form })
      ElMessage.success('公告已更新')
    } else {
      await adminApi.createAnnouncement({ ...form })
      ElMessage.success('公告已发布')
    }
    dialogVisible.value = false
    await load()
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    saving.value = false
  }
}

const remove = async (item) => {
  try {
    await ElMessageBox.confirm(`确定删除公告「${item.title}」吗？`, '删除公告', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  try {
    await adminApi.deleteAnnouncement(item.id)
    ElMessage.success('公告已删除')
    await load()
  } catch (e) {
    ElMessage.error(e.message)
  }
}
</script>

<template>
  <div>
    <div class="admin-page-head">
      <div>
        <h2>公告管理</h2>
        <p>手动发布公告；比赛开始与结束会自动生成公告并展示在首页。</p>
      </div>
      <el-button type="primary" @click="openCreate">＋ 发布公告</el-button>
    </div>

    <div class="admin-panel" v-loading="loading">
      <table v-if="list.length" class="admin-table">
        <thead>
          <tr>
            <th>标题</th>
            <th>类型</th>
            <th>置顶</th>
            <th>发布时间</th>
            <th style="text-align: right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.id">
            <td>
              <strong>{{ item.title }}</strong>
              <div class="muted" style="max-width: 460px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis">
                {{ item.content }}
              </div>
            </td>
            <td>
              <el-tag
                :type="(announcementTypeMap[item.type] || {}).type || 'info'"
                size="small"
                effect="light"
                round
              >
                {{ (announcementTypeMap[item.type] || {}).label || item.type }}
              </el-tag>
            </td>
            <td>
              <el-tag v-if="item.pinned" type="danger" size="small" effect="light" round>置顶</el-tag>
              <span v-else class="muted">—</span>
            </td>
            <td class="muted">{{ formatTime(item.createdAt) }}</td>
            <td style="text-align: right">
              <el-button size="small" text @click="openEdit(item)">编辑</el-button>
              <el-button size="small" type="danger" text @click="remove(item)">删除</el-button>
            </td>
          </tr>
        </tbody>
      </table>

      <EmptyState
        v-else-if="!loading"
        icon="📣"
        title="还没有公告"
        description="发布第一条公告，向球友们传递消息。"
      >
        <el-button type="primary" @click="openCreate">发布公告</el-button>
      </EmptyState>
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑公告' : '发布公告'" width="560px">
      <el-form label-position="top">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" maxlength="100" placeholder="公告标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="6" maxlength="1000" show-word-limit placeholder="公告内容" />
        </el-form-item>
        <el-form-item>
          <el-switch v-model="form.pinned" active-text="置顶展示" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
