<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api'
import { applicationStatusMap, applicationTypeMap, formatTime } from '@/utils/format'
import EmptyState from '@/components/EmptyState.vue'

const status = ref('PENDING')
const loading = ref(true)
const pageData = reactive({ list: [], total: 0, page: 1, size: 10 })

const load = async () => {
  loading.value = true
  try {
    const data = await adminApi.applications({
      page: pageData.page,
      size: pageData.size,
      status: status.value || undefined
    })
    pageData.list = data.list
    pageData.total = data.total
  } finally {
    loading.value = false
  }
}

onMounted(load)

const switchStatus = (value) => {
  status.value = value
  pageData.page = 1
  load()
}

const handle = async (item, action) => {
  const isApprove = action === 'approve'
  let remark = ''
  try {
    const result = await ElMessageBox.prompt(
      isApprove
        ? `通过后，${item.applicantNickname || item.applicantUsername} 将获得${applicationTypeMap[item.type]}对应的角色。`
        : `请填写拒绝原因（可选）。`,
      isApprove ? '通过申请' : '拒绝申请',
      {
        confirmButtonText: isApprove ? '通过' : '拒绝',
        cancelButtonText: '取消',
        inputPlaceholder: isApprove ? '审批备注（可选）' : '拒绝原因（可选）',
        inputValue: ''
      }
    )
    remark = result.value || ''
  } catch {
    return
  }
  try {
    if (isApprove) {
      await adminApi.approveApplication(item.id, remark)
      ElMessage.success('申请已通过，用户角色已更新')
    } else {
      await adminApi.rejectApplication(item.id, remark)
      ElMessage.success('申请已拒绝')
    }
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
        <h2>申请审批</h2>
        <p>审批用户提交的计分员 / 管理员申请，通过后角色立即生效。</p>
      </div>
      <el-radio-group :model-value="status" @change="switchStatus">
        <el-radio-button value="PENDING">待处理</el-radio-button>
        <el-radio-button value="APPROVED">已通过</el-radio-button>
        <el-radio-button value="REJECTED">已拒绝</el-radio-button>
        <el-radio-button value="">全部</el-radio-button>
      </el-radio-group>
    </div>

    <div class="admin-panel" v-loading="loading">
      <table v-if="pageData.list.length" class="admin-table">
        <thead>
          <tr>
            <th>申请人</th>
            <th>申请类型</th>
            <th>理由</th>
            <th>状态</th>
            <th>提交时间</th>
            <th style="text-align: right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in pageData.list" :key="item.id">
            <td>
              <strong>{{ item.applicantNickname || item.applicantUsername }}</strong>
              <div class="muted">@{{ item.applicantUsername }}</div>
            </td>
            <td>{{ applicationTypeMap[item.type] || item.type }}</td>
            <td class="muted" style="max-width: 320px">{{ item.reason || '未填写' }}</td>
            <td>
              <el-tag
                :type="(applicationStatusMap[item.status] || {}).type || 'info'"
                size="small"
                effect="light"
                round
              >
                {{ (applicationStatusMap[item.status] || {}).label || item.status }}
              </el-tag>
              <div v-if="item.handleRemark" class="muted" style="margin-top: 4px">
                {{ item.handleRemark }}
              </div>
            </td>
            <td class="muted">{{ formatTime(item.createdAt) }}</td>
            <td style="text-align: right">
              <template v-if="item.status === 'PENDING'">
                <el-button size="small" type="primary" @click="handle(item, 'approve')">通过</el-button>
                <el-button size="small" type="danger" text @click="handle(item, 'reject')">拒绝</el-button>
              </template>
              <span v-else class="muted">{{ item.handlerName || '已处理' }}</span>
            </td>
          </tr>
        </tbody>
      </table>

      <EmptyState
        v-else-if="!loading"
        icon="📨"
        title="没有符合条件的申请"
        description="新的角色申请会出现在这里。"
      />

      <div style="display: flex; justify-content: flex-end; margin-top: 16px">
        <el-pagination
          v-model:current-page="pageData.page"
          v-model:page-size="pageData.size"
          :total="pageData.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @current-change="load"
          @size-change="() => { pageData.page = 1; load() }"
        />
      </div>
    </div>
  </div>
</template>
