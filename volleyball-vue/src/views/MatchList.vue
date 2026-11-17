<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'
import TopBar from '../components/TopBar.vue'

const router = useRouter()

const matches = ref([])
const freeTeams = ref([])
const loading = ref(true)
const error = ref('')
const notice = ref('')

const showCreate = ref(false)
const creating = ref(false)
const createError = ref('')
const form = ref({ name: '', rules: '5-25', sponsor: '', teamAId: '', teamBId: '' })

const ruleOptions = [
  { value: '1-5', label: '1 局 5 分' },
  { value: '3-15', label: '3 局 15 分' },
  { value: '5-25', label: '5 局 25 分' },
]

// 占位比赛不出现在列表里
const visibleMatches = computed(() => matches.value.filter((m) => m.id !== 1))

function stateClass(state) {
  if (state === '结束') return 'tag done'
  if (state === '进行中') return 'tag live'
  return 'tag'
}

function teamsOf(m) {
  const t = m.teams || []
  return [t[0] || null, t[1] || null]
}

function scoreOf(m, team) {
  const s = (m.scores || []).find((x) => team && x.teamId === team.id)
  return s ? { d: s.dScore || 0, x: s.xScore || 0 } : { d: 0, x: 0 }
}

function fmtTime(v) {
  if (!v) return ''
  return String(v).replace('.0', '')
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const list = await api.matches()
    matches.value = Array.isArray(list) ? list : []
  } catch (e) {
    error.value = e.message || '加载比赛列表失败'
  } finally {
    loading.value = false
  }
}

async function openCreate() {
  createError.value = ''
  form.value = { name: '', rules: '5-25', sponsor: '', teamAId: '', teamBId: '' }
  showCreate.value = true
  try {
    const list = await api.freeTeams()
    freeTeams.value = Array.isArray(list) ? list : []
    if (freeTeams.value.length >= 2) {
      form.value.teamAId = String(freeTeams.value[0].id)
      form.value.teamBId = String(freeTeams.value[1].id)
    }
  } catch (e) {
    createError.value = e.message || '加载可用队伍失败'
  }
}

async function submitCreate() {
  createError.value = ''
  const f = form.value
  if (!f.name.trim()) {
    createError.value = '请填写比赛名称'
    return
  }
  if (!f.teamAId || !f.teamBId) {
    createError.value = '请选择两支队伍'
    return
  }
  if (f.teamAId === f.teamBId) {
    createError.value = '两支队伍不能相同'
    return
  }
  creating.value = true
  try {
    const res = await api.addMatch({
      name: f.name.trim(),
      rules: f.rules,
      sponsor: f.sponsor.trim(),
      teamAId: f.teamAId,
      teamBId: f.teamBId,
    })
    if (res && res.success) {
      showCreate.value = false
      notice.value = '比赛创建成功'
      await load()
      if (res.matchId) router.push(`/recording/${res.matchId}`)
    } else {
      createError.value = (res && res.msg) || '创建失败'
      if (res && res.matchId) {
        // 后端已建出比赛但后续步骤失败，提示可删除
        await load()
      }
    }
  } catch (e) {
    createError.value = e.message || '创建失败'
  } finally {
    creating.value = false
  }
}

async function remove(m) {
  if (!window.confirm(`确定删除比赛「${m.name}」？该操作不可恢复。`)) return
  try {
    const res = await api.deleteMatch(m.id)
    if (res && res.success) {
      notice.value = '已删除'
      await load()
    } else {
      error.value = (res && res.msg) || '删除失败'
    }
  } catch (e) {
    error.value = e.message || '删除失败'
  }
}

onMounted(load)
</script>

<template>
  <div>
    <TopBar />

    <main class="page">
      <div class="panel">
        <div class="panel-title">
          <span>比赛列表</span>
          <div style="display: flex; gap: 8px">
            <button class="btn ghost" type="button" :disabled="loading" @click="load">
              {{ loading ? '刷新中…' : '刷新' }}
            </button>
            <button class="btn primary" type="button" @click="openCreate">+ 新建比赛</button>
          </div>
        </div>

        <div v-if="error" class="alert error">{{ error }}</div>
        <div v-else-if="notice" class="alert ok">{{ notice }}</div>

        <div v-if="loading" class="empty">正在加载比赛列表…</div>
        <div v-else-if="visibleMatches.length === 0" class="empty">
          还没有比赛，点击右上角「新建比赛」开始。
        </div>

        <div v-else class="match-grid">
          <article v-for="m in visibleMatches" :key="m.id" class="match-card">
            <div class="head">
              <div>
                <div class="name">{{ m.name }}</div>
                <div class="muted">规则 {{ m.rules || '-' }} · {{ m.sponsor || '无赞助商' }}</div>
              </div>
              <span :class="stateClass(m.state)">{{ m.state || '未知' }}</span>
            </div>

            <div class="score-line">
              <div>
                <div class="num">{{ scoreOf(m, teamsOf(m)[0]).x }}</div>
                <div class="sets">胜局 {{ scoreOf(m, teamsOf(m)[0]).d }}</div>
              </div>
              <div class="muted">:</div>
              <div>
                <div class="num">{{ scoreOf(m, teamsOf(m)[1]).x }}</div>
                <div class="sets">胜局 {{ scoreOf(m, teamsOf(m)[1]).d }}</div>
              </div>
            </div>

            <div class="vs">
              <div class="side">{{ teamsOf(m)[0] ? teamsOf(m)[0].name : '待定' }}</div>
              <div class="mid">VS</div>
              <div class="side">{{ teamsOf(m)[1] ? teamsOf(m)[1].name : '待定' }}</div>
            </div>

            <div class="muted">
              {{ fmtTime(m.startTime) || '未设置开始时间' }}
              <template v-if="m.endTime">→ {{ fmtTime(m.endTime) }}</template>
            </div>

            <div class="card-actions">
              <button class="btn primary" type="button" @click="router.push(`/recording/${m.id}`)">
                进入记分
              </button>
              <button class="btn danger" type="button" @click="remove(m)">删除</button>
            </div>
          </article>
        </div>
      </div>
    </main>

    <!-- 新建比赛弹窗 -->
    <div v-if="showCreate" class="modal-mask" @click.self="showCreate = false">
      <div class="modal">
        <h3>新建比赛</h3>

        <div v-if="createError" class="alert error">{{ createError }}</div>

        <div class="field">
          <label>比赛名称</label>
          <input v-model="form.name" placeholder="例如：2026 春季排球联赛" />
        </div>

        <div class="row-2">
          <div class="field">
            <label>赛制规则</label>
            <select v-model="form.rules">
              <option v-for="r in ruleOptions" :key="r.value" :value="r.value">{{ r.label }}</option>
            </select>
          </div>
          <div class="field">
            <label>赞助商（可选）</label>
            <input v-model="form.sponsor" placeholder="赞助单位" />
          </div>
        </div>

        <div class="row-2">
          <div class="field">
            <label>A 队</label>
            <select v-model="form.teamAId">
              <option value="">请选择</option>
              <option v-for="t in freeTeams" :key="t.id" :value="String(t.id)">{{ t.name }}</option>
            </select>
          </div>
          <div class="field">
            <label>B 队</label>
            <select v-model="form.teamBId">
              <option value="">请选择</option>
              <option v-for="t in freeTeams" :key="t.id" :value="String(t.id)">{{ t.name }}</option>
            </select>
          </div>
        </div>

        <p v-if="freeTeams.length < 2" class="hint">可用队伍不足两支，请先在队伍管理中释放队伍。</p>

        <div class="modal-actions">
          <button class="btn ghost" type="button" @click="showCreate = false">取消</button>
          <button class="btn primary" type="button" :disabled="creating" @click="submitCreate">
            {{ creating ? '创建中…' : '创建并记分' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>