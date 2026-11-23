<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { publicApi } from '@/api'
import { formatTime } from '@/utils/format'
import MatchArena from '@/components/MatchArena.vue'
import EmptyState from '@/components/EmptyState.vue'

const route = useRoute()
const detail = ref(null)
const loading = ref(true)
const error = ref('')

const pointsByMember = computed(() => {
  const map = {}
  ;(detail.value?.events || []).forEach((event) => {
    if (event.memberId) map[event.memberId] = (map[event.memberId] || 0) + 1
  })
  return map
})

onMounted(async () => {
  try {
    detail.value = await publicApi.resultDetail(route.params.id)
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="container page">
    <div v-if="loading" v-loading="true" style="min-height: 320px" />

    <EmptyState v-else-if="error" icon="🔍" title="没有找到这场比赛" :description="error">
      <router-link to="/results" class="btn-primary">返回赛果列表</router-link>
    </EmptyState>

    <template v-else-if="detail">
      <div class="page-head">
        <div>
          <span class="eyebrow">Match Result</span>
          <h1 class="page-title">{{ detail.match.name }}</h1>
          <p class="page-sub">{{ detail.match.rules }} · {{ formatTime(detail.match.endTime) }} 结束</p>
        </div>
        <router-link to="/results" class="btn-ghost">返回赛果</router-link>
      </div>

      <MatchArena :detail="detail" />

      <div class="detail-grid">
        <div>
          <div class="info-card">
            <h3>局分详情</h3>
            <table class="sets-table">
              <thead>
                <tr>
                  <th>局</th>
                  <th>{{ detail.leftTeam?.name }}</th>
                  <th>{{ detail.rightTeam?.name }}</th>
                  <th>获胜方</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="set in detail.sets" :key="set.setNo">
                  <td>第 {{ set.setNo }} 局</td>
                  <td :style="{ fontWeight: set.winnerTeamId === detail.leftTeam?.id ? 800 : 400 }">
                    {{ set.leftPoints }}
                  </td>
                  <td :style="{ fontWeight: set.winnerTeamId === detail.rightTeam?.id ? 800 : 400 }">
                    {{ set.rightPoints }}
                  </td>
                  <td>{{ set.winnerTeamId === detail.leftTeam?.id ? detail.leftTeam?.name : detail.rightTeam?.name }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="info-card" style="margin-top: 18px">
            <h3>比赛信息</h3>
            <div class="kv"><span>比赛名称</span><b>{{ detail.match.name }}</b></div>
            <div class="kv"><span>主办方</span><b>{{ detail.match.sponsor || '—' }}</b></div>
            <div class="kv"><span>赛制</span><b>{{ detail.match.rules }}</b></div>
            <div class="kv"><span>开赛时间</span><b>{{ formatTime(detail.match.startTime) }}</b></div>
            <div class="kv"><span>结束时间</span><b>{{ formatTime(detail.match.endTime) }}</b></div>
            <div class="kv"><span>计分员</span><b>{{ detail.creatorName }}</b></div>
          </div>
        </div>

        <div>
          <div class="info-card">
            <h3>{{ detail.leftTeam?.name }} · 球员得分</h3>
            <div class="team-roster">
              <div v-for="member in detail.leftTeam?.members || []" :key="member.id" class="roster-row">
                <span class="pos-badge">{{ member.courtPosition || '·' }}</span>
                <span>{{ member.name }}</span>
                <span style="color: var(--muted)">{{ member.role }}</span>
                <span class="pts">{{ pointsByMember[member.id] || 0 }} 分</span>
              </div>
            </div>
          </div>

          <div class="info-card" style="margin-top: 18px">
            <h3>{{ detail.rightTeam?.name }} · 球员得分</h3>
            <div class="team-roster">
              <div v-for="member in detail.rightTeam?.members || []" :key="member.id" class="roster-row">
                <span class="pos-badge">{{ member.courtPosition || '·' }}</span>
                <span>{{ member.name }}</span>
                <span style="color: var(--muted)">{{ member.role }}</span>
                <span class="pts">{{ pointsByMember[member.id] || 0 }} 分</span>
              </div>
            </div>
          </div>

          <div v-if="detail.vps?.length" class="info-card" style="margin-top: 18px">
            <h3>最有价值球员</h3>
            <div class="vp-badges">
              <span v-for="vp in detail.vps" :key="vp.id" class="vp-badge" :class="vp.type.toLowerCase()">
                {{ vp.type }} · {{ vp.memberName }}（{{ vp.teamName }}）<br />
                <small style="font-weight: 500">{{ vp.evaluate }}</small>
              </span>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>
