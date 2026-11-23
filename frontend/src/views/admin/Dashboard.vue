<script setup>
import { computed, onMounted, ref } from 'vue'
import { adminApi } from '@/api'
import { formatTime } from '@/utils/format'
import EmptyState from '@/components/EmptyState.vue'

const info = ref(null)
const announcements = ref([])
const logs = ref([])
const loading = ref(true)

const bar = (value) => `${Math.min(Math.max(Number(value) || 0, 0), 100)}%`
const cpuText = computed(() => (info.value?.cpuLoad < 0 ? '不可用' : `${info.value?.cpuLoad ?? 0}%`))

const load = async () => {
  loading.value = true
  try {
    const [system, ann, logPage] = await Promise.all([
      adminApi.system(),
      adminApi.announcements(),
      adminApi.loginLogs({ page: 1, size: 6 })
    ])
    info.value = system
    announcements.value = ann.slice(0, 4)
    logs.value = logPage.list
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div v-loading="loading">
    <template v-if="info">
      <div class="stat-grid">
        <div class="stat-card accent">
          <div class="label">注册用户</div>
          <div class="value">{{ info.userCount }}</div>
          <div class="hint">活跃 {{ info.activeUserCount }} · 管理员 {{ info.adminCount }} · 计分员 {{ info.scorerCount }}</div>
        </div>
        <div class="stat-card">
          <div class="label">比赛总数</div>
          <div class="value">{{ info.matchCount }}</div>
          <div class="hint">进行中 {{ info.ongoingMatchCount }} · 已结束 {{ info.finishedMatchCount }}</div>
        </div>
        <div class="stat-card">
          <div class="label">待处理申请</div>
          <div class="value">{{ info.pendingApplicationCount }}</div>
          <div class="hint">
            <router-link to="/admin/applications" class="text-link">前往审批 →</router-link>
          </div>
        </div>
        <div class="stat-card">
          <div class="label">今日登录</div>
          <div class="value">{{ info.todayLoginCount }}</div>
          <div class="hint">累计登录 {{ info.totalLoginCount }} 次</div>
        </div>
      </div>

      <div class="admin-grid-2" style="margin-top: 18px">
        <div class="admin-panel">
          <div class="admin-panel-title">
            <h3>系统占用</h3>
            <span class="tag gray">实时采样</span>
          </div>
          <div class="usage-grid">
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
                <strong>CPU</strong>
                <span>{{ info.cpuCores }} 核心</span>
              </div>
              <div class="usage-bar">
                <i :style="{ width: bar(info.cpuLoad < 0 ? 0 : info.cpuLoad) }" />
              </div>
              <div class="usage-meta">
                <span>负载 {{ cpuText }}</span>
                <span>{{ info.osName }} / {{ info.osArch }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="admin-panel">
          <div class="admin-panel-title">
            <h3>运行信息</h3>
            <span class="tag">JVM</span>
          </div>
          <div class="kv"><span>操作系统</span><b>{{ info.osName }} {{ info.osArch }}</b></div>
          <div class="kv"><span>Java 版本</span><b>{{ info.jvmVersion }}</b></div>
          <div class="kv"><span>CPU 核心</span><b>{{ info.cpuCores }}</b></div>
          <div class="kv"><span>连续运行</span><b>{{ info.uptimeMinutes }} 分钟</b></div>
          <div class="kv"><span>队伍 / 队员</span><b>{{ info.teamCount }} / {{ info.memberCount }}</b></div>
          <div class="kv"><span>公告数量</span><b>{{ info.announcementCount }}</b></div>
          <div style="display: flex; gap: 10px; margin-top: 16px; flex-wrap: wrap">
            <router-link to="/admin/matches" class="btn-primary" style="height: 38px; padding: 0 16px">创建比赛</router-link>
            <router-link to="/admin/teams" class="btn-ghost" style="height: 38px; padding: 0 16px">管理队伍</router-link>
          </div>
        </div>
      </div>

      <div class="admin-grid-2" style="margin-top: 18px">
        <div class="admin-panel">
          <div class="admin-panel-title">
            <h3>最近公告</h3>
            <router-link to="/admin/announcements" class="text-link">管理公告 →</router-link>
          </div>
          <div v-if="announcements.length" class="timeline">
            <div v-for="item in announcements" :key="item.id" class="timeline-item">
              <div class="tl-head">
                <span>{{ item.title }}</span>
                <span style="color: var(--muted); font-size: 11px">{{ formatTime(item.createdAt) }}</span>
              </div>
              <p>{{ item.content }}</p>
            </div>
          </div>
          <EmptyState v-else icon="📣" title="暂无公告" description="发布第一条公告吧。" />
        </div>

        <div class="admin-panel">
          <div class="admin-panel-title">
            <h3>最近登录</h3>
            <router-link to="/admin/logs" class="text-link">更多记录 →</router-link>
          </div>
          <table class="admin-table">
            <thead>
              <tr><th>用户</th><th>IP</th><th>状态</th><th>时间</th></tr>
            </thead>
            <tbody>
              <tr v-for="log in logs" :key="log.id">
                <td>{{ log.username }}</td>
                <td class="muted">{{ log.ip }}</td>
                <td>
                  <el-tag :type="log.success ? 'success' : 'danger'" size="small" effect="light">
                    {{ log.success ? '成功' : '失败' }}
                  </el-tag>
                </td>
                <td class="muted">{{ formatTime(log.loginTime, 'MM-DD HH:mm') }}</td>
              </tr>
              <tr v-if="!logs.length">
                <td colspan="4" class="muted">暂无登录记录</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>
  </div>
</template>
