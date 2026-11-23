<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { adminApi } from '@/api'
import { formatTime } from '@/utils/format'

const info = ref(null)
const logs = reactive({ list: [], total: 0, page: 1, size: 15 })
const keyword = ref('')
const loading = ref(true)

const bar = (value) => `${Math.min(Math.max(Number(value) || 0, 0), 100)}%`
const cpuText = computed(() => (info.value?.cpuLoad < 0 ? '不可用' : `${info.value?.cpuLoad ?? 0}%`))

const loadSystem = async () => {
  info.value = await adminApi.system()
}

const loadLogs = async () => {
  loading.value = true
  try {
    const data = await adminApi.loginLogs({
      page: logs.page,
      size: logs.size,
      keyword: keyword.value || undefined
    })
    logs.list = data.list
    logs.total = data.total
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await Promise.all([loadSystem(), loadLogs()])
})

const search = () => {
  logs.page = 1
  loadLogs()
}
</script>

<template>
  <div>
    <div class="admin-page-head">
      <div>
        <h2>系统检测</h2>
        <p>系统资源占用、运行环境与用户登录情况。</p>
      </div>
      <el-button @click="loadSystem">刷新系统状态</el-button>
    </div>

    <div v-if="info" class="usage-grid">
      <div class="usage-card">
        <div class="usage-head">
          <strong>JVM 堆内存</strong>
          <span>{{ info.heapUsedMb }} / {{ info.heapMaxMb }} MB</span>
        </div>
        <div class="usage-bar"><i :style="{ width: bar(info.heapUsagePercent) }" /></div>
        <div class="usage-meta">
          <span>使用率 {{ info.heapUsagePercent }}%</span>
          <span>Java {{ info.jvmVersion }}</span>
        </div>
      </div>
      <div class="usage-card">
        <div class="usage-head">
          <strong>系统内存</strong>
          <span>{{ info.systemFreeMb }} MB 可用</span>
        </div>
        <div class="usage-bar"><i :style="{ width: bar(info.systemUsagePercent) }" /></div>
        <div class="usage-meta">
          <span>使用率 {{ info.systemUsagePercent }}%</span>
          <span>总量 {{ info.systemTotalMb }} MB</span>
        </div>
      </div>
      <div class="usage-card">
        <div class="usage-head">
          <strong>磁盘空间</strong>
          <span>{{ info.diskFreeGb }} GB 可用</span>
        </div>
        <div class="usage-bar"><i :style="{ width: bar(info.diskUsagePercent) }" /></div>
        <div class="usage-meta">
          <span>使用率 {{ info.diskUsagePercent }}%</span>
          <span>总量 {{ info.diskTotalGb }} GB</span>
        </div>
      </div>
      <div class="usage-card">
        <div class="usage-head">
          <strong>CPU 负载</strong>
          <span>{{ info.cpuCores }} 核心</span>
        </div>
        <div class="usage-bar">
          <i :style="{ width: bar(info.cpuLoad < 0 ? 0 : info.cpuLoad) }" />
        </div>
        <div class="usage-meta">
          <span>{{ cpuText }}</span>
          <span>{{ info.osName }} / {{ info.osArch }}</span>
        </div>
      </div>
    </div>

    <div class="admin-panel" style="margin-top: 18px" v-loading="loading">
      <div class="admin-panel-title">
        <h3>用户登录日志</h3>
        <div style="display: flex; gap: 8px">
          <el-input
            v-model="keyword"
            placeholder="用户名 / IP"
            clearable
            style="width: 200px"
            @keyup.enter="search"
            @clear="search"
          />
          <el-button type="primary" @click="search">搜索</el-button>
        </div>
      </div>

      <table class="admin-table">
        <thead>
          <tr>
            <th>用户</th>
            <th>IP</th>
            <th>设备</th>
            <th>结果</th>
            <th>说明</th>
            <th>时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="log in logs.list" :key="log.id">
            <td><strong>{{ log.username }}</strong></td>
            <td class="muted">{{ log.ip }}</td>
            <td class="muted" style="max-width: 260px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis">
              {{ log.userAgent || '—' }}
            </td>
            <td>
              <el-tag :type="log.success ? 'success' : 'danger'" size="small" effect="light" round>
                {{ log.success ? '成功' : '失败' }}
              </el-tag>
            </td>
            <td class="muted">{{ log.message }}</td>
            <td class="muted">{{ formatTime(log.loginTime) }}</td>
          </tr>
          <tr v-if="!logs.list.length && !loading">
            <td colspan="6" class="muted">暂无登录记录。</td>
          </tr>
        </tbody>
      </table>

      <div style="display: flex; justify-content: flex-end; margin-top: 16px">
        <el-pagination
          v-model:current-page="logs.page"
          v-model:page-size="logs.size"
          :total="logs.total"
          :page-sizes="[15, 30, 50]"
          layout="total, sizes, prev, pager, next"
          @current-change="loadLogs"
          @size-change="() => { logs.page = 1; loadLogs() }"
        />
      </div>
    </div>
  </div>
</template>
