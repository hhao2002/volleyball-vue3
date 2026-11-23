<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { liveApi } from '@/api'
import { useAuthStore } from '@/stores/auth'
import { connectLive } from '@/utils/liveSocket'
import MatchArena from '@/components/MatchArena.vue'
import EmptyState from '@/components/EmptyState.vue'

const route = useRoute()
const auth = useAuthStore()
const detail = ref(null)
const loading = ref(true)
const error = ref('')
const connected = ref(false)
let socket = null
let pollTimer = null

const matchId = computed(() => Number(route.params.id))
const isCreator = computed(() => detail.value?.match?.creatorId === auth.user?.id)
const isOngoing = computed(() => detail.value?.match?.state === 'ONGOING')
const resultLink = computed(() => `/results/${matchId.value}`)

const load = async () => {
  try {
    detail.value = await liveApi.detail(matchId.value)
    error.value = ''
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await load()
  if (!detail.value) return
  socket = connectLive(matchId.value, auth.token, {
    onOpen: () => {
      connected.value = true
    },
    onClose: () => {
      connected.value = false
    },
    onMessage: (payload) => {
      if (payload.type === 'MATCH_UPDATE' && payload.data) {
        detail.value = payload.data
      }
    }
  })
  pollTimer = window.setInterval(() => {
    if (!connected.value) load()
  }, 20000)
})

onUnmounted(() => {
  socket?.close()
  window.clearInterval(pollTimer)
})
</script>

<template>
  <div class="container page">
    <div v-if="loading" v-loading="true" style="min-height: 320px" />

    <EmptyState v-else-if="error" icon="📡" title="无法进入这场比赛" :description="error">
      <router-link to="/live" class="btn-primary">返回实时比赛</router-link>
    </EmptyState>

    <template v-else-if="detail">
      <div class="page-head">
        <div>
          <span class="eyebrow">Live Match</span>
          <h1 class="page-title">实时观赛</h1>
          <p class="page-sub">
            只有创建该比赛的计分员可以修改比分，其他用户为只读观战。
            <span v-if="isCreator && isOngoing" style="color: var(--orange-deep); font-weight: 700">
              你是本场计分员。
            </span>
          </p>
        </div>
        <div style="display: flex; gap: 10px; flex-wrap: wrap">
          <router-link v-if="isCreator && isOngoing" :to="`/admin/matches/${matchId}/score`" class="btn-primary">
            进入计分台
          </router-link>
          <router-link v-if="detail.match.state === 'FINISHED'" :to="resultLink" class="btn-ghost">
            查看赛果详情
          </router-link>
          <router-link to="/live" class="btn-ghost">返回列表</router-link>
        </div>
      </div>

      <el-alert
        v-if="!connected && detail.match.state === 'ONGOING'"
        title="实时连接重连中，比分将定时刷新"
        type="warning"
        :closable="false"
        show-icon
        style="margin-bottom: 16px; border-radius: 12px"
      />

      <MatchArena :detail="detail" :connected="connected" />

      <div v-if="detail.match.state === 'FINISHED'" class="panel" style="margin-top: 18px">
        <div class="panel-title">
          <h3>比赛已结束</h3>
          <router-link :to="resultLink" class="text-link">查看完整赛果 →</router-link>
        </div>
        <p style="color: var(--muted); font-size: 13px; line-height: 1.9">
          MVP：{{ detail.mvpName || '暂无' }} · SVP：{{ detail.svpName || '暂无' }}。
          比赛结果已自动同步到公告栏。
        </p>
      </div>
    </template>
  </div>
</template>
