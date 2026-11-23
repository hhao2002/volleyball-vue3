<script setup>
import { computed } from 'vue'
import { formatTime, stateMap } from '@/utils/format'

const props = defineProps({
  detail: { type: Object, required: true },
  interactive: { type: Boolean, default: false },
  busy: { type: Boolean, default: false },
  connected: { type: Boolean, default: false }
})

const emit = defineEmits(['point', 'out', 'undo'])

const match = computed(() => props.detail?.match || {})
const leftTeam = computed(() => props.detail?.leftTeam || { members: [] })
const rightTeam = computed(() => props.detail?.rightTeam || { members: [] })
const sets = computed(() => props.detail?.sets || [])
const allEvents = computed(() => [...(props.detail?.events || [])].sort((a, b) => b.seq - a.seq))
const recentEvents = computed(() => allEvents.value.slice(0, 12))
const state = computed(() => stateMap[match.value.state] || { label: match.value.state, type: 'info' })
const target = computed(() =>
  match.value.currentSet === match.value.bestOf ? match.value.finalSetPoints : match.value.pointsPerSet
)
const canOperate = computed(
  () => props.interactive && match.value.state === 'ONGOING' && !props.busy
)
const canUndo = computed(() => canOperate.value && allEvents.value.length > 0)

const teamColor = (teamId) => {
  if (teamId === leftTeam.value.id) return leftTeam.value.color || '#ff6b2c'
  if (teamId === rightTeam.value.id) return rightTeam.value.color || '#2f6bff'
  return '#8b7266'
}

const memberPoints = computed(() => {
  const map = {}
  allEvents.value.forEach((event) => {
    if (event.memberId) {
      map[event.memberId] = (map[event.memberId] || 0) + 1
    }
  })
  return map
})

const winner = computed(() => {
  if (match.value.state !== 'FINISHED') return null
  return match.value.winnerTeamId === leftTeam.value.id ? leftTeam.value : rightTeam.value
})

const vpMap = computed(() => {
  const map = {}
  ;(props.detail?.vps || []).forEach((vp) => {
    map[vp.type] = vp
  })
  return map
})

const handlePoint = (team, member) => {
  if (!canOperate.value) return
  emit('point', { teamId: team.id, memberId: member.id, type: 'ATTACK' })
}

const handleOut = (team) => {
  if (!canOperate.value) return
  emit('out', { teamId: team.id, type: 'OUT' })
}

const feedWho = (event) => {
  if (event.type === 'OUT') return `${event.teamName || '队伍'} · 对方出界`
  return `${event.memberName || '球员'} · ${event.teamName || ''}`
}
</script>

<template>
  <section class="arena">
    <header class="arena-head">
      <div>
        <h2>{{ match.name }}</h2>
        <div class="meta">
          <el-tag :type="state.type" effect="light" round>{{ state.label }}</el-tag>
          <span v-if="match.sponsor">主办：{{ match.sponsor }}</span>
          <span>{{ match.rules }}</span>
          <span v-if="connected" class="tag live"><i class="live-dot" /> 实时连接</span>
        </div>
      </div>
      <slot name="actions" />
    </header>

    <div class="scoreboard">
      <div class="score-side" :style="{ '--team-color': leftTeam.color || '#ff6b2c' }">
        <span class="team-name">{{ leftTeam.name || '主队' }}</span>
        <span class="coach">{{ leftTeam.coach ? '主教练：' + leftTeam.coach : '' }}</span>
        <span class="sets">{{ match.leftSets ?? 0 }}</span>
        <span class="coach">已胜局数</span>
      </div>

      <div class="score-center">
        <div class="set-caption">
          第 {{ match.currentSet || 1 }} 局 · {{ target || 25 }} 分制 · 领先 2 分获胜
        </div>
        <div class="points-row">
          <transition name="pop">
            <span :key="'l' + match.leftPoints" class="points">{{ match.leftPoints ?? 0 }}</span>
          </transition>
          <span class="colon">:</span>
          <transition name="pop">
            <span :key="'r' + match.rightPoints" class="points">{{ match.rightPoints ?? 0 }}</span>
          </transition>
        </div>
        <div class="set-dots">
          <span
            v-for="set in sets"
            :key="set.setNo"
            class="set-chip"
            :class="{ win: set.winnerTeamId }"
          >
            第{{ set.setNo }}局 {{ set.leftPoints }}:{{ set.rightPoints }}
          </span>
          <span v-if="!sets.length" class="set-chip">本局进行中</span>
        </div>
      </div>

      <div class="score-side right" :style="{ '--team-color': rightTeam.color || '#2f6bff' }">
        <span class="team-name">{{ rightTeam.name || '客队' }}</span>
        <span class="coach">{{ rightTeam.coach ? '主教练：' + rightTeam.coach : '' }}</span>
        <span class="sets">{{ match.rightSets ?? 0 }}</span>
        <span class="coach">已胜局数</span>
      </div>
    </div>

    <div v-if="winner" class="winner-banner">
      <div>
        <strong>🏆 {{ winner.name }} 获胜</strong>
        <div class="vp" style="margin-top: 6px">
          <span>{{ match.leftSets }} : {{ match.rightSets }}（局分）</span>
          <span>{{ match.endTime ? '结束于 ' + formatTime(match.endTime) : '' }}</span>
        </div>
      </div>
      <div class="vp-badges">
        <span v-if="vpMap.MVP" class="vp-badge mvp">
          MVP {{ vpMap.MVP.memberName }} · {{ vpMap.MVP.evaluate }}
        </span>
        <span v-if="vpMap.SVP" class="vp-badge svp">
          SVP {{ vpMap.SVP.memberName }} · {{ vpMap.SVP.evaluate }}
        </span>
      </div>
    </div>

    <div class="team-panels">
      <div
        v-for="team in [leftTeam, rightTeam]"
        :key="team.id"
        class="team-panel"
        :style="{ '--team-color': team.color || (team.id === leftTeam.id ? '#ff6b2c' : '#2f6bff') }"
      >
        <div class="panel-head">
          <strong>{{ team.name }}</strong>
          <span>{{ interactive ? '点击球员即可记一分' : '阵容' }}</span>
        </div>
        <div class="players">
          <button
            v-for="member in team.members || []"
            :key="member.id"
            class="player-btn"
            :disabled="!canOperate"
            @click="handlePoint(team, member)"
          >
            <span class="num">#{{ member.number ?? member.courtPosition ?? '-' }}</span>
            <span class="nm">{{ member.name }}</span>
            <span class="pos">{{ member.role }}</span>
            <span class="pts">{{ memberPoints[member.id] || 0 }} 分</span>
          </button>
        </div>
        <button v-if="interactive" class="out-btn" :disabled="!canOperate" @click="handleOut(team)">
          ＋ 出界得分 <small>不记录球员</small>
        </button>
      </div>
    </div>

    <div v-if="interactive" class="arena-tools">
      <el-button round :disabled="!canUndo" @click="emit('undo')">撤销上一分</el-button>
      <slot name="tools" />
    </div>

    <div class="event-feed">
      <div class="feed-head">
        <strong>实时得分记录</strong>
        <span class="tag gray">{{ allEvents.length }} 次得分</span>
      </div>
      <div class="feed-list">
        <div
          v-for="event in recentEvents"
          :key="event.id || event.seq"
          class="feed-item"
          :style="{ '--dot': teamColor(event.teamId) }"
        >
          <span class="seq">#{{ event.seq }}</span>
          <span class="dot" />
          <span class="who">
            第{{ event.setNo }}局 · {{ feedWho(event) }}
            <span v-if="event.type === 'OUT'" class="tag gray" style="margin-left: 6px">出界</span>
          </span>
          <span class="time">{{ formatTime(event.createdAt, 'HH:mm:ss') }}</span>
        </div>
        <div v-if="!recentEvents.length" class="feed-empty">还没有得分记录，第一分即将诞生。</div>
      </div>
    </div>
  </section>
</template>
