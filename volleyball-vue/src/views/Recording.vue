<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '../api'
import TopBar from '../components/TopBar.vue'

const route = useRoute()
const router = useRouter()
const matchId = computed(() => Number(route.params.id))

const loading = ref(true)
const error = ref('')
const saving = ref(false)

const name = ref('')
const rules = ref('5-25')
const sponsor = ref('')
const state = ref('进行中')
const teams = ref([]) // [{id,name,coach,members}]
const scores = ref({}) // teamId -> {dScore, xScore}
const leftTeamId = ref(null)
const inning = ref(1)
const history = ref([])
const logs = ref([])
const speechOn = ref(true)
const mvpText = ref('')

/* ---------- 计算属性 ---------- */
// 剩余可加分：分数上限由规则第二段决定
const pointLimit = computed(() => {
  const parts = String(rules.value || '').split('-')
  const n = parseInt(parts[1], 10)
  return Number.isFinite(n) && n > 0 ? n : 25
})
// 总局数上限
const setLimit = computed(() => {
  const parts = String(rules.value || '').split('-')
  const n = parseInt(parts[0], 10)
  return Number.isFinite(n) && n > 0 ? n : 5
})
// 胜局需求：3局制赢2局，5局制赢3局
const setsToWin = computed(() => Math.floor(setLimit.value / 2) + 1)

const leftTeam = computed(() => teams.value.find((t) => t.id === leftTeamId.value) || teams.value[0] || null)
const rightTeam = computed(() => {
  if (!teams.value.length) return null
  return teams.value.find((t) => t.id !== leftTeam.value?.id) || null
})

const finished = computed(() => state.value === '结束')
const canAdd = computed(() => !finished.value && !saving.value)

function scoreText(team) {
  if (!team) return 0
  const s = scores.value[team.id]
  return s ? s.xScore || 0 : 0
}
function setText(team) {
  if (!team) return 0
  const s = scores.value[team.id]
  return s ? s.dScore || 0 : 0
}

/* ---------- 数据加载 ---------- */
function applyDetail(d) {
  name.value = d.name || '未命名比赛'
  rules.value = d.rules || '5-25'
  sponsor.value = d.sponsor || ''
  state.value = d.state || '进行中'
  teams.value = d.teams || []
  scores.value = {}
  ;(d.scores || []).forEach((s) => {
    scores.value[s.teamId] = { dScore: s.dScore || 0, xScore: s.xScore || 0 }
  })
  if (!leftTeamId.value || !teams.value.some((t) => t.id === leftTeamId.value)) {
    leftTeamId.value = teams.value[0] ? teams.value[0].id : null
  }
  inning.value = Math.min(Math.max(1, (scores.value[teams.value[0]?.id]?.dScore || 0) + (scores.value[teams.value[1]?.id]?.dScore || 0) + 1), setLimit.value)
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const d = await api.match(matchId.value)
    if (!d || d.success === false || !d.match) {
      error.value = (d && d.msg) || '未找到该比赛'
      return
    }
    applyDetail(d.match)
    logs.value = []
    addLog('已载入比赛数据')
  } catch (e) {
    error.value = e.message || '加载比赛失败'
  } finally {
    loading.value = false
  }
}

/* ---------- 日志与语音 ---------- */
function nowTime() {
  const d = new Date()
  const p = (n) => String(n).padStart(2, '0')
  return `${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
}

function addLog(text) {
  logs.value.unshift({ id: Date.now() + Math.random(), time: nowTime(), text })
  if (logs.value.length > 40) logs.value.pop()
}

function speak(text) {
  if (!speechOn.value) return
  if (typeof window === 'undefined' || !('speechSynthesis' in window)) return
  try {
    const u = new window.SpeechSynthesisUtterance(text)
    u.lang = 'zh-CN'
    u.rate = 1.05
    window.speechSynthesis.cancel()
    window.speechSynthesis.speak(u)
  } catch {
    /* 某些浏览器不支持，忽略 */
  }
}

/* ---------- 核心操作 ---------- */
function snapshot() {
  history.value.push({
    scores: JSON.parse(JSON.stringify(scores.value)),
    leftTeamId: leftTeamId.value,
    inning: inning.value,
  })
  if (history.value.length > 30) history.value.shift()
}

async function pushScore() {
  const payload = {
    matchId: matchId.value,
    state: state.value,
    inning: inning.value,
    scoreA: scoreText(leftTeam.value),
    scoreB: scoreText(rightTeam.value),
    setsA: setText(leftTeam.value),
    setsB: setText(rightTeam.value),
    leftTeamId: leftTeam.value ? leftTeam.value.id : -1,
  }
  saving.value = true
  try {
    const res = await api.updateScores(payload)
    if (!res || res.success === false) {
      error.value = (res && res.msg) || '保存比分失败'
      return false
    }
    error.value = ''
    return true
  } catch (e) {
    error.value = e.message || '保存比分失败'
    return false
  } finally {
    saving.value = false
  }
}

function addPoint(team, delta) {
  if (!team || finished.value) return
  const cur = scores.value[team.id] || { dScore: 0, xScore: 0 }
  const next = { dScore: cur.dScore || 0, xScore: (cur.xScore || 0) + delta }
  if (next.xScore < 0) next.xScore = 0
  if (next.xScore > pointLimit.value) return
  if (next.xScore === (cur.xScore || 0)) return

  snapshot()
  scores.value = { ...scores.value, [team.id]: next }
  addLog(`${team.name} ${delta > 0 ? '+' + delta : delta} → ${next.xScore}`)
  speak(`${team.name}${next.xScore}分`)
  pushScore()
}

async function undo() {
  if (!history.value.length) return
  const prev = history.value.pop()
  scores.value = prev.scores
  leftTeamId.value = prev.leftTeamId
  inning.value = prev.inning
  addLog('撤销上一步操作')
  await pushScore()
}

// 赢下一局：当局胜者胜局+1，双方当前局分清零，进入下一局
async function winSet() {
  const a = leftTeam.value
  const b = rightTeam.value
  if (!a || !b) return
  const ca = scoreText(a)
  const cb = scoreText(b)
  const winner = ca === cb ? null : ca > cb ? a : b
  snapshot()

  const nextSetsA = setText(a) + (winner && winner.id === a.id ? 1 : 0)
  const nextSetsB = setText(b) + (winner && winner.id === b.id ? 1 : 0)
  const next = {}
  next[a.id] = { dScore: nextSetsA, xScore: 0 }
  next[b.id] = { dScore: nextSetsB, xScore: 0 }
  scores.value = next
  inning.value = Math.min(inning.value + 1, setLimit.value)

  if (nextSetsA >= setsToWin.value || nextSetsB >= setsToWin.value) {
    addLog(`第 ${inning.value} 局结束，胜局已达 ${setsToWin.value}，可以结束比赛`)
    speak('比赛可以结束了')
  } else {
    addLog(`第 ${inning.value} 局结束，${winner ? winner.name + '胜' : '平分'}，进入第 ${inning.value} 局`)
    speak(`第${inning.value}局`)
  }
  await pushScore()
}

function swapSides() {
  snapshot()
  const a = leftTeam.value
  const b = rightTeam.value
  if (!b) return
  leftTeamId.value = b.id
  addLog(`换边：${b.name} 转到左侧`)
  pushScore()
}

async function changeInning(delta) {
  const next = Math.min(setLimit.value, Math.max(1, inning.value + delta))
  if (next === inning.value) return
  snapshot()
  inning.value = next
  addLog(`切换到第 ${next} 局`)
  await pushScore()
}

async function finish() {
  if (finished.value) return
  if (!window.confirm('确定结束这场比赛？结束后不能再加分。')) return
  snapshot()
  state.value = '结束'
  const ok = await pushScore()
  if (!ok) return
  try {
    const res = await api.finishMatch(matchId.value)
    if (res && res.success) {
      mvpText.value = res.mvp || ''
      addLog(`比赛已结束${mvpText.value ? '，MVP：' + mvpText.value : ''}`)
      speak(mvpText.value ? `比赛结束，最有价值球员${mvpText.value}` : '比赛结束')
    } else {
      error.value = (res && res.msg) || '结束比赛失败'
    }
  } catch (e) {
    error.value = e.message || '结束比赛失败'
  }
}

onMounted(() => {
  load()
  // 语音依赖浏览器支持，不支持时自动关闭开关
  if (typeof window !== 'undefined' && !('speechSynthesis' in window)) speechOn.value = false
})

onBeforeUnmount(() => {
  if (typeof window !== 'undefined' && 'speechSynthesis' in window) window.speechSynthesis.cancel()
})
</script>

<template>
  <div>
    <TopBar />

    <main class="page">
      <div class="panel">
        <div class="panel-title">
          <div>
            <div>{{ name }}</div>
            <div class="muted">
              规则 {{ rules }} · 第 {{ inning }} / {{ setLimit }} 局 · 每局 {{ pointLimit }} 分 ·
              赢 {{ setsToWin }} 局获胜
              <span v-if="sponsor"> · 赞助 {{ sponsor }}</span>
            </div>
          </div>
          <div style="display: flex; gap: 8px">
            <button class="btn ghost" type="button" @click="router.push('/matchs')">返回列表</button>
            <span :class="state === '结束' ? 'tag done' : 'tag live'">
              {{ state }}
            </span>
          </div>
        </div>

        <div v-if="error" class="alert error">{{ error }}</div>

        <div v-if="loading" class="empty">正在加载比分…</div>
        <div v-else-if="!teams.length" class="empty">该比赛没有绑定队伍，无法记分。</div>

        <template v-else>
          <div class="scoreboard">
            <!-- 左队 -->
            <section class="team-panel" :class="{ serving: leftTeam && leftTeam.id === leftTeamId }">
              <div class="team-name">{{ leftTeam?.name || '待定' }}</div>
              <div class="team-coach">教练 {{ leftTeam?.coach || '未设置' }}</div>
              <div class="big-score">{{ scoreText(leftTeam) }}</div>
              <div class="sets-badge">胜局 {{ setText(leftTeam) }}</div>
              <button class="big-btn" type="button" :disabled="!canAdd" @click="addPoint(leftTeam, 1)">
                +1
              </button>
              <button class="btn ghost" type="button" :disabled="!canAdd" @click="addPoint(leftTeam, -1)">
                取消一分
              </button>
              <div v-if="leftTeam && leftTeam.id === leftTeamId" class="serve-in">← 发球方</div>
              <div v-if="leftTeam?.members?.length" class="roster">
                <span
                  v-for="mem in leftTeam.members"
                  :key="mem.id"
                  class="member"
                  :class="{ libero: String(mem.position || '').includes('自由') }"
                >
                  {{ mem.position || '' }} {{ mem.name }}
                </span>
              </div>
            </section>

            <!-- 中间控制 -->
            <div class="center-col">
              <div class="rules">{{ rules }}</div>
              <div class="inning">第 {{ inning }} 局</div>
              <button class="btn primary" type="button" :disabled="saving" @click="winSet">
                本局结束
              </button>
              <div style="display: flex; gap: 6px">
                <button class="btn ghost" type="button" :disabled="inning <= 1" @click="changeInning(-1)">上一局</button>
                <button class="btn ghost" type="button" :disabled="inning >= setLimit" @click="changeInning(1)">下一局</button>
              </div>
              <button class="btn ghost" type="button" :disabled="saving" @click="swapSides">换边</button>
              <button class="btn ghost" type="button" :disabled="!history.length || saving" @click="undo">
                撤销
              </button>
              <button class="btn danger" type="button" :disabled="finished || saving" @click="finish">
                {{ finished ? '已结束' : '结束比赛' }}
              </button>
            </div>

            <!-- 右队 -->
            <section class="team-panel">
              <div class="team-name">{{ rightTeam?.name || '待定' }}</div>
              <div class="team-coach">教练 {{ rightTeam?.coach || '未设置' }}</div>
              <div class="big-score">{{ scoreText(rightTeam) }}</div>
              <div class="sets-badge">胜局 {{ setText(rightTeam) }}</div>
              <button class="big-btn" type="button" :disabled="!canAdd" @click="addPoint(rightTeam, 1)">
                +1
              </button>
              <button class="btn ghost" type="button" :disabled="!canAdd" @click="addPoint(rightTeam, -1)">
                取消一分
              </button>
              <div v-if="rightTeam?.members?.length" class="roster">
                <span
                  v-for="mem in rightTeam.members"
                  :key="mem.id"
                  class="member"
                  :class="{ libero: String(mem.position || '').includes('自由') }"
                >
                  {{ mem.position || '' }} {{ mem.name }}
                </span>
              </div>
            </section>
          </div>

          <div class="toolbar">
            <label class="speech-toggle">
              <input v-model="speechOn" type="checkbox" />
              语音播报比分
            </label>
            <span class="muted">{{ saving ? '保存中…' : '比分已同步到数据库' }}</span>
            <span v-if="mvpText" class="muted">MVP：{{ mvpText }}</span>
          </div>

          <div class="log">
            <div v-for="item in logs" :key="item.id" class="log-item">
              <span>{{ item.text }}</span>
              <span class="time">{{ item.time }}</span>
            </div>
          </div>
        </template>
      </div>
    </main>
  </div>
</template>