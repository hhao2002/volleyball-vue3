<script setup>
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { scorerApi } from '@/api'
import { useAuthStore } from '@/stores/auth'
import { formatTime, stateMap } from '@/utils/format'
import EmptyState from '@/components/EmptyState.vue'

const auth = useAuthStore()
const router = useRouter()
const matches = ref([])
const teams = ref([])
const loading = ref(true)
const dialogVisible = ref(false)
const saving = ref(false)
const filterState = ref('')
let timer = null

const form = reactive({
  name: '',
  sponsor: '',
  leftTeamId: null,
  rightTeamId: null,
  bestOf: 5,
  pointsPerSet: 25,
  finalSetPoints: 15,
  rules: ''
})

const filteredMatches = computed(() => {
  if (!filterState.value) return matches.value
  return matches.value.filter((item) => item.state === filterState.value)
})

const readyTeams = computed(() =>
  teams.value.filter((team) => (team.members?.length || 0) === 6 && !team.locked)
)

const teamLabel = (team) => {
  const count = team.members?.length || 0
  const state = team.locked ? '比赛中' : count === 6 ? '可参赛' : `仅 ${count} 人`
  return `${team.name}（${state}）`
}

const loadMatches = async (silent = false) => {
  if (!silent) loading.value = true
  try {
    matches.value = await scorerApi.matches()
  } finally {
    loading.value = false
  }
}

const loadTeams = async () => {
  teams.value = await scorerApi.teams()
}

onMounted(async () => {
  await Promise.all([loadMatches(), loadTeams()])
  timer = window.setInterval(() => loadMatches(true), 20000)
})

onUnmounted(() => window.clearInterval(timer))

const isCreator = (item) => item.creatorId === auth.user?.id

const openCreate = async () => {
  await loadTeams()
  if (readyTeams.value.length < 2) {
    ElMessage.warning('至少需要两支满 6 人且不在比赛中的队伍，请先到队伍管理完善阵容')
    return
  }
  form.name = ''
  form.sponsor = ''
  form.leftTeamId = null
  form.rightTeamId = null
  form.bestOf = 5
  form.pointsPerSet = 25
  form.finalSetPoints = 15
  form.rules = ''
  dialogVisible.value = true
}

const submitCreate = async () => {
  if (!form.name.trim()) {
    ElMessage.warning('请输入比赛名称')
    return
  }
  if (!form.leftTeamId || !form.rightTeamId) {
    ElMessage.warning('请选择对阵双方')
    return
  }
  if (form.leftTeamId === form.rightTeamId) {
    ElMessage.warning('主客队不能相同')
    return
  }
  saving.value = true
  try {
    await scorerApi.createMatch({ ...form })
    ElMessage.success('比赛已创建，可在列表中开始')
    dialogVisible.value = false
    await loadMatches()
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    saving.value = false
  }
}

const startMatch = async (item) => {
  try {
    await ElMessageBox.confirm(
      `开始「${item.name}」后，双方阵容将被锁定，且只有你可以计分。`,
      '开始比赛',
      { confirmButtonText: '开始比赛', cancelButtonText: '再想想', type: 'warning' }
    )
  } catch {
    return
  }
  try {
    await scorerApi.startMatch(item.id)
    ElMessage.success('比赛已开始，进入计分台')
    router.push(`/admin/matches/${item.id}/score`)
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const deleteMatch = async (item) => {
  try {
    await ElMessageBox.confirm(
      item.state === 'FINISHED'
        ? '删除已结束的比赛将同时清理比分、MVP/SVP 与自动公告，且不可恢复。'
        : '确定删除这场比赛吗？',
      '删除比赛',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )
  } catch {
    return
  }
  try {
    await scorerApi.deleteMatch(item.id)
    ElMessage.success('比赛已删除')
    await loadMatches()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const enterConsole = (item) => {
  if (isCreator(item)) {
    router.push(`/admin/matches/${item.id}/score`)
  } else {
    router.push(`/live/${item.id}`)
  }
}
</script>

<template>
  <div>
    <div class="admin-page-head">
      <div>
        <h2>比赛管理</h2>
        <p>创建比赛、开始计分、查看赛果。进行中的比赛只有创建者可以操作。</p>
      </div>
      <div style="display: flex; gap: 10px; flex-wrap: wrap">
        <el-select v-model="filterState" placeholder="全部状态" style="width: 130px" clearable>
          <el-option label="准备中" value="READY" />
          <el-option label="进行中" value="ONGOING" />
          <el-option label="已结束" value="FINISHED" />
        </el-select>
        <el-button type="primary" @click="openCreate">＋ 创建比赛</el-button>
      </div>
    </div>

    <div class="admin-panel" v-loading="loading">
      <table v-if="filteredMatches.length" class="admin-table">
        <thead>
          <tr>
            <th>比赛</th>
            <th>对阵</th>
            <th>局分 / 本局</th>
            <th>状态</th>
            <th>时间</th>
            <th style="text-align: right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredMatches" :key="item.id">
            <td>
              <strong>{{ item.name }}</strong>
              <div class="muted">{{ item.rules }}</div>
            </td>
            <td>
              <strong>{{ item.leftTeamName }}</strong>
              <span class="muted" style="margin: 0 6px">VS</span>
              <strong>{{ item.rightTeamName }}</strong>
            </td>
            <td>
              <b style="font-size: 16px">{{ item.leftSets }} : {{ item.rightSets }}</b>
              <div class="muted">本局 {{ item.leftPoints }} : {{ item.rightPoints }}</div>
            </td>
            <td>
              <el-tag :type="(stateMap[item.state] || {}).type" effect="light" round>
                {{ (stateMap[item.state] || {}).label || item.state }}
              </el-tag>
              <div v-if="item.state === 'ONGOING'" class="muted" style="margin-top: 4px">
                {{ isCreator(item) ? '你正在计分' : '仅创建者可计分' }}
              </div>
            </td>
            <td class="muted">
              {{ formatTime(item.startTime || item.createdAt) }}
            </td>
            <td style="text-align: right">
              <div style="display: inline-flex; gap: 6px; flex-wrap: wrap; justify-content: flex-end">
                <el-button
                  v-if="item.state === 'READY' && isCreator(item)"
                  type="primary"
                  size="small"
                  @click="startMatch(item)"
                >
                  开始
                </el-button>
                <el-button
                  v-if="item.state === 'ONGOING'"
                  size="small"
                  :type="isCreator(item) ? 'primary' : 'default'"
                  @click="enterConsole(item)"
                >
                  {{ isCreator(item) ? '计分台' : '观战' }}
                </el-button>
                <el-button
                  v-if="item.state === 'FINISHED'"
                  size="small"
                  @click="router.push(`/results/${item.id}`)"
                >
                  赛果
                </el-button>
                <el-button
                  v-if="item.state !== 'ONGOING' && (isCreator(item) || auth.isAdmin)"
                  size="small"
                  type="danger"
                  text
                  @click="deleteMatch(item)"
                >
                  删除
                </el-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <EmptyState
        v-else-if="!loading"
        icon="🏐"
        title="还没有比赛"
        description="先准备两支满 6 人的队伍，然后创建一场新比赛。"
      >
        <el-button type="primary" @click="openCreate">创建第一场比赛</el-button>
      </EmptyState>
    </div>

    <el-dialog v-model="dialogVisible" title="创建比赛" width="620px" destroy-on-close>
      <el-form label-position="top">
        <el-row :gutter="16">
          <el-col :xs="24" :sm="14">
            <el-form-item label="比赛名称" required>
              <el-input v-model="form.name" maxlength="50" placeholder="例如：校排球联赛决赛" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="10">
            <el-form-item label="主办方">
              <el-input v-model="form.sponsor" maxlength="30" placeholder="选填" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :xs="24" :sm="12">
            <el-form-item label="主队" required>
              <el-select v-model="form.leftTeamId" placeholder="选择主队" style="width: 100%">
                <el-option
                  v-for="team in teams"
                  :key="team.id"
                  :label="teamLabel(team)"
                  :value="team.id"
                  :disabled="(team.members?.length || 0) !== 6 || team.locked"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="客队" required>
              <el-select v-model="form.rightTeamId" placeholder="选择客队" style="width: 100%">
                <el-option
                  v-for="team in teams"
                  :key="team.id"
                  :label="teamLabel(team)"
                  :value="team.id"
                  :disabled="(team.members?.length || 0) !== 6 || team.locked"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :xs="24" :sm="8">
            <el-form-item label="赛制">
              <el-select v-model="form.bestOf" style="width: 100%">
                <el-option label="1 局 1 胜" :value="1" />
                <el-option label="3 局 2 胜" :value="3" />
                <el-option label="5 局 3 胜" :value="5" />
                <el-option label="7 局 4 胜" :value="7" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="每局分数">
              <el-select v-model="form.pointsPerSet" style="width: 100%">
                <el-option label="5 分（趣味）" :value="5" />
                <el-option label="10 分（趣味）" :value="10" />
                <el-option label="15 分" :value="15" />
                <el-option label="21 分" :value="21" />
                <el-option label="25 分" :value="25" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="决胜局分数">
              <el-select v-model="form.finalSetPoints" style="width: 100%">
                <el-option label="5 分（趣味）" :value="5" />
                <el-option label="15 分" :value="15" />
                <el-option label="25 分" :value="25" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="规则说明（留空自动生成）">
          <el-input v-model="form.rules" placeholder="例如：5局3胜 · 25分制 · 决胜局15分" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitCreate">创建比赛</el-button>
      </template>
    </el-dialog>
  </div>
</template>
