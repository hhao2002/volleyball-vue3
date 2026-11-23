<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { scorerApi } from '@/api'
import { useAuthStore } from '@/stores/auth'
import { connectLive } from '@/utils/liveSocket'
import { stateMap } from '@/utils/format'
import MatchArena from '@/components/MatchArena.vue'
import EmptyState from '@/components/EmptyState.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const detail = ref(null)
const loading = ref(true)
const error = ref('')
const busy = ref(false)
const connected = ref(false)
let socket = null

const matchId = computed(() => Number(route.params.id))
const match = computed(() => detail.value?.match || {})
const isCreator = computed(() => match.value.creatorId === auth.user?.id)
const canOperate = computed(() => isCreator.value && match.value.state === 'ONGOING')
const stateInfo = computed(() => stateMap[match.value.state] || { label: match.value.state, type: 'info' })

const load = async () => {
  try {
    detail.value = await scorerApi.matchDetail(matchId.value)
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
})

onUnmounted(() => socket?.close())

const startMatch = async () => {
  try {
    await ElMessageBox.confirm(
      '开始后双方阵容将被锁定，只有你可以为本场比赛计分。',
      '开始比赛',
      { confirmButtonText: '开始', cancelButtonText: '取消', type: 'warning' }
    )
  } catch {
    return
  }
  busy.value = true
  try {
    detail.value = await scorerApi.startMatch(matchId.value)
    ElMessage.success('比赛已开始，比分将实时同步')
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    busy.value = false
  }
}

const addPoint = async (payload) => {
  if (!canOperate.value || busy.value) return
  busy.value = true
  try {
    detail.value = await scorerApi.addPoint(matchId.value, payload)
    if (detail.value.match.state === 'FINISHED') {
      ElMessage.success('比赛结束，MVP / SVP 已自动评选')
    }
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    busy.value = false
  }
}

const undo = async () => {
  if (!canOperate.value || busy.value) return
  busy.value = true
  try {
    detail.value = await scorerApi.undo(matchId.value)
    ElMessage.success('已撤销上一分')
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    busy.value = false
  }
}
</script>

<template>
  <div>
    <div v-if="loading" v-loading="true" style="min-height: 320px" />

    <EmptyState v-else-if="error" icon="🔍" title="无法打开计分台" :description="error">
      <el-button type="primary" @click="router.push('/admin/matches')">返回比赛管理</el-button>
    </EmptyState>

    <template v-else-if="detail">
      <div class="console-head">
        <div>
          <el-button text @click="router.push('/admin/matches')">← 返回比赛管理</el-button>
          <h2 style="font-size: 26px; letter-spacing: -0.8px; margin-top: 4px">{{ match.name }}</h2>
          <div class="console-note" :class="{ readonly: !canOperate }">
            <span class="dot" />
            <template v-if="canOperate">你正在计分，所有操作实时同步给观众</template>
            <template v-else-if="!isCreator">只读模式 · 只有创建该比赛的计分员可以修改比分</template>
            <template v-else-if="match.state === 'READY'">比赛尚未开始</template>
            <template v-else>比赛已结束，仅可查看</template>
          </div>
        </div>
        <div style="display: flex; gap: 10px; flex-wrap: wrap">
          <el-tag :type="stateInfo.type" effect="light" round>{{ stateInfo.label }}</el-tag>
          <el-button @click="router.push(`/live/${matchId}`)">观众视角</el-button>
          <el-button v-if="match.state === 'FINISHED'" type="primary" @click="router.push(`/results/${matchId}`)">
            查看赛果
          </el-button>
        </div>
      </div>

      <div v-if="match.state === 'READY'" class="admin-panel">
        <div class="admin-panel-title">
          <h3>赛前确认</h3>
          <span class="tag">准备中</span>
        </div>
        <div class="scoreboard" style="border-radius: 16px">
          <div class="score-side">
            <span class="team-name">{{ detail.leftTeam?.name }}</span>
            <span class="coach">{{ detail.leftTeam?.members?.length }} 名队员</span>
          </div>
          <div class="score-center">
            <div class="set-caption">{{ match.rules }}</div>
            <div class="points-row"><span class="points" style="font-size: 40px">VS</span></div>
          </div>
          <div class="score-side right">
            <span class="team-name">{{ detail.rightTeam?.name }}</span>
            <span class="coach">{{ detail.rightTeam?.members?.length }} 名队员</span>
          </div>
        </div>
        <div style="display: flex; align-items: center; justify-content: space-between; gap: 16px; margin-top: 18px; flex-wrap: wrap">
          <p style="color: var(--muted); font-size: 13px">
            开始后，队伍阵容将被锁定；比赛结束后自动生成公告与 MVP / SVP。
          </p>
          <el-button v-if="isCreator" type="primary" size="large" :loading="busy" @click="startMatch">
            开始比赛
          </el-button>
          <el-tag v-else type="info">只有创建者可以开始比赛</el-tag>
        </div>
        <div style="margin-top: 20px">
          <div class="admin-panel-title"><h3>双方阵容</h3></div>
          <div class="team-panels" style="padding: 0">
            <div
              v-for="team in [detail.leftTeam, detail.rightTeam]"
              :key="team.id"
              class="team-panel"
              :style="{ '--team-color': team.color || '#ff6b2c' }"
            >
              <div class="panel-head"><strong>{{ team.name }}</strong><span>{{ team.coach }}</span></div>
              <div class="team-roster-chips">
                <span v-for="member in team.members || []" :key="member.id" class="member-chip">
                  <b>{{ member.courtPosition }}</b> {{ member.name }} · {{ member.role }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <MatchArena
        v-else
        :detail="detail"
        :interactive="canOperate"
        :busy="busy"
        :connected="connected"
        @point="addPoint"
        @out="addPoint"
        @undo="undo"
      >
        <template #actions>
          <span v-if="!isCreator" class="tag gray">只读观战</span>
        </template>
        <template #tools>
          <span class="tag" :class="{ gray: !connected }">{{ connected ? '实时同步中' : '正在重连' }}</span>
        </template>
      </MatchArena>
    </template>
  </div>
</template>
