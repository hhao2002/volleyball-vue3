<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { scorerApi } from '@/api'
import EmptyState from '@/components/EmptyState.vue'

const teams = ref([])
const members = ref([])
const available = ref([])
const loading = ref(true)

const dialogVisible = ref(false)
const editingId = ref(null)
const saving = ref(false)
const form = reactive({ name: '', coach: '', color: '#FF6B2C' })
const slots = ref(Array.from({ length: 6 }, () => null))
const pool = ref([])
const dragMember = ref(null)
const dragFrom = ref(null)
const dragOverSlot = ref(-1)
const poolOver = ref(false)

const quickForm = reactive({ name: '', role: '主攻', number: null })
const quickAdding = ref(false)

const memberDialogVisible = ref(false)
const memberSaving = ref(false)
const memberForm = reactive({ id: null, name: '', role: '主攻', number: null })

const loadAll = async () => {
  loading.value = true
  try {
    const [teamList, allMembers, freeMembers] = await Promise.all([
      scorerApi.teams(),
      scorerApi.members(false),
      scorerApi.members(true)
    ])
    teams.value = teamList
    members.value = allMembers
    available.value = freeMembers
  } finally {
    loading.value = false
  }
}

onMounted(loadAll)

const sortedMembers = (team) =>
  [...(team.members || [])].sort((a, b) => (a.courtPosition || 99) - (b.courtPosition || 99))

const openCreate = () => {
  editingId.value = null
  form.name = ''
  form.coach = ''
  form.color = '#FF6B2C'
  slots.value = Array.from({ length: 6 }, () => null)
  pool.value = [...available.value]
  dialogVisible.value = true
}

const openEdit = async (team) => {
  if (team.locked) {
    ElMessage.warning('该队伍正在参加进行中的比赛，无法修改')
    return
  }
  const fresh = await scorerApi.team(team.id)
  editingId.value = fresh.id
  form.name = fresh.name
  form.coach = fresh.coach || ''
  form.color = fresh.color || '#FF6B2C'
  const next = Array.from({ length: 6 }, () => null)
  ;(fresh.members || []).forEach((member) => {
    const index = (member.courtPosition || 0) - 1
    if (index >= 0 && index < 6) next[index] = member
  })
  let cursor = 0
  ;(fresh.members || []).forEach((member) => {
    if (!next.includes(member)) {
      while (cursor < 6 && next[cursor]) cursor += 1
      if (cursor < 6) next[cursor] = member
    }
  })
  slots.value = next
  pool.value = [...available.value]
  dialogVisible.value = true
}

const onDragStart = (member, fromSlot = null) => {
  dragMember.value = member
  dragFrom.value = fromSlot
}

const clearDrag = () => {
  dragMember.value = null
  dragFrom.value = null
  dragOverSlot.value = -1
  poolOver.value = false
}

const removeFromSlot = (index) => {
  const member = slots.value[index]
  if (!member) return
  slots.value[index] = null
  pool.value.unshift(member)
}

const assignToSlot = (index, member) => {
  if (!member) return
  const existing = slots.value.findIndex((item) => item?.id === member.id)
  if (existing >= 0) {
    slots.value[existing] = null
  }
  if (slots.value[index]) {
    pool.value.unshift(slots.value[index])
  }
  slots.value[index] = member
  const poolIndex = pool.value.findIndex((item) => item.id === member.id)
  if (poolIndex >= 0) pool.value.splice(poolIndex, 1)
}

const onDropSlot = (index) => {
  assignToSlot(index, dragMember.value)
  clearDrag()
}

const assignToFirstEmpty = (member) => {
  const index = slots.value.findIndex((item) => !item)
  if (index < 0) {
    ElMessage.warning('场上位置已满，请先移除一名球员')
    return
  }
  assignToSlot(index, member)
}

const onDropPool = () => {
  if (dragFrom.value !== null) {
    removeFromSlot(dragFrom.value)
  }
  clearDrag()
}

const addQuickMember = async () => {
  if (!quickForm.name.trim()) {
    ElMessage.warning('请输入队员姓名')
    return
  }
  quickAdding.value = true
  try {
    const member = await scorerApi.createMember({ ...quickForm })
    pool.value.push(member)
    quickForm.name = ''
    quickForm.number = null
    ElMessage.success('队员已加入可用列表，拖到场上位置即可')
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    quickAdding.value = false
  }
}

const saveTeam = async () => {
  if (!form.name.trim()) {
    ElMessage.warning('请输入队伍名称')
    return
  }
  if (slots.value.some((item) => !item)) {
    ElMessage.warning('请先把 6 个位置全部安排队员')
    return
  }
  const ids = slots.value.map((item) => item.id)
  if (new Set(ids).size !== 6) {
    ElMessage.warning('队员不能重复')
    return
  }
  saving.value = true
  try {
    const payload = {
      name: form.name,
      coach: form.coach,
      color: form.color,
      memberIds: ids
    }
    if (editingId.value) {
      await scorerApi.updateTeam(editingId.value, payload)
      ElMessage.success('队伍阵容已更新')
    } else {
      await scorerApi.createTeam(payload)
      ElMessage.success('队伍创建成功')
    }
    dialogVisible.value = false
    await loadAll()
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    saving.value = false
  }
}

const deleteTeam = async (team) => {
  try {
    await ElMessageBox.confirm(`确定删除队伍「${team.name}」吗？`, '删除队伍', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  try {
    await scorerApi.deleteTeam(team.id)
    ElMessage.success('队伍已删除')
    await loadAll()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const openMemberDialog = (member = null) => {
  memberForm.id = member?.id || null
  memberForm.name = member?.name || ''
  memberForm.role = member?.role || '主攻'
  memberForm.number = member?.number ?? null
  memberDialogVisible.value = true
}

const saveMember = async () => {
  if (!memberForm.name.trim()) {
    ElMessage.warning('请输入队员姓名')
    return
  }
  memberSaving.value = true
  try {
    const payload = {
      name: memberForm.name,
      role: memberForm.role,
      number: memberForm.number
    }
    if (memberForm.id) {
      await scorerApi.updateMember(memberForm.id, payload)
      ElMessage.success('队员信息已更新')
    } else {
      await scorerApi.createMember(payload)
      ElMessage.success('队员已创建')
    }
    memberDialogVisible.value = false
    await loadAll()
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    memberSaving.value = false
  }
}

const deleteMember = async (member) => {
  try {
    await ElMessageBox.confirm(
      `确定删除队员「${member.name}」吗？已有比赛记录的队员无法删除。`,
      '删除队员',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )
  } catch {
    return
  }
  try {
    await scorerApi.deleteMember(member.id)
    ElMessage.success('队员已删除')
    await loadAll()
  } catch (e) {
    ElMessage.error(e.message)
  }
}
</script>

<template>
  <div>
    <div class="admin-page-head">
      <div>
        <h2>队伍管理</h2>
        <p>每队必须有 6 名队员；拖拽可用球员到 1-6 号位。比赛进行中队伍会被锁定。</p>
      </div>
      <div style="display: flex; gap: 10px; flex-wrap: wrap">
        <el-button @click="openMemberDialog()">＋ 新增队员</el-button>
        <el-button type="primary" @click="openCreate">＋ 创建队伍</el-button>
      </div>
    </div>

    <div v-loading="loading">
      <div v-if="teams.length" class="team-grid">
        <div
          v-for="team in teams"
          :key="team.id"
          class="team-card"
          :class="{ locked: team.locked }"
          :style="{ '--team-color': team.color || '#FF6B2C' }"
        >
          <div class="team-card-head">
            <div>
              <h3>{{ team.name }}</h3>
              <small>{{ team.coach ? '主教练：' + team.coach : '未填写教练' }}</small>
            </div>
            <el-tag v-if="team.locked" type="warning" size="small" effect="light" round>比赛中</el-tag>
            <el-tag v-else type="success" size="small" effect="light" round>{{ team.members?.length || 0 }} 人</el-tag>
          </div>
          <div class="team-roster-chips">
            <span v-for="member in sortedMembers(team)" :key="member.id" class="member-chip">
              <b>{{ member.courtPosition }}</b> {{ member.name }} · {{ member.role }}
            </span>
          </div>
          <div class="team-card-actions">
            <el-button size="small" :disabled="team.locked" @click="openEdit(team)">编辑阵容</el-button>
            <el-button size="small" type="danger" text :disabled="team.locked" @click="deleteTeam(team)">
              删除
            </el-button>
          </div>
        </div>
      </div>

      <EmptyState
        v-else-if="!loading"
        icon="🧩"
        title="还没有队伍"
        description="创建两支满 6 人的队伍，就可以开始比赛了。"
      >
        <el-button type="primary" @click="openCreate">创建第一支队伍</el-button>
      </EmptyState>

      <div class="admin-panel" style="margin-top: 22px">
        <div class="admin-panel-title">
          <h3>队员库</h3>
          <span class="tag gray">共 {{ members.length }} 人 · 可用 {{ available.length }} 人</span>
        </div>
        <table class="admin-table">
          <thead>
            <tr>
              <th>姓名</th>
              <th>位置</th>
              <th>号码</th>
              <th>所属队伍</th>
              <th style="text-align: right">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="member in members" :key="member.id">
              <td><strong>{{ member.name }}</strong></td>
              <td>{{ member.role }}</td>
              <td>{{ member.number ?? '—' }}</td>
              <td>
                <span v-if="member.teamName">{{ member.teamName }}</span>
                <span v-else class="tag gray">可用</span>
              </td>
              <td style="text-align: right">
                <el-button size="small" text @click="openMemberDialog(member)">编辑</el-button>
                <el-button size="small" type="danger" text @click="deleteMember(member)">删除</el-button>
              </td>
            </tr>
            <tr v-if="!members.length">
              <td colspan="5" class="muted">还没有队员，先新增一名吧。</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '编辑队伍阵容' : '创建队伍'"
      width="860px"
      destroy-on-close
    >
      <el-row :gutter="16">
        <el-col :xs="24" :sm="9">
          <el-form-item label="队伍名称" required>
            <el-input v-model="form.name" maxlength="30" placeholder="例如：中国女排" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="9">
          <el-form-item label="主教练">
            <el-input v-model="form.coach" maxlength="30" placeholder="选填" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="6">
          <el-form-item label="队伍颜色">
            <el-color-picker v-model="form.color" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="18">
        <el-col :xs="24" :sm="14">
          <div style="font-size: 13px; font-weight: 700; margin-bottom: 8px">
            场上位置 <span style="color: var(--muted); font-weight: 400">（点击已安排球员可移除）</span>
          </div>
          <div class="lineup-slots">
            <div
              v-for="(member, index) in slots"
              :key="index"
              class="lineup-slot"
              :class="{ filled: member, 'drag-over': dragOverSlot === index }"
              :draggable="Boolean(member)"
              @dragstart="onDragStart(member, index)"
              @dragend="clearDrag"
              @dragover.prevent="dragOverSlot = index"
              @dragleave="dragOverSlot = -1"
              @drop.prevent="onDropSlot(index)"
              @click="removeFromSlot(index)"
            >
              <span class="slot-pos">{{ index + 1 }} 号位</span>
              <template v-if="member">
                <span class="slot-name">{{ member.name }}</span>
                <span class="slot-role">{{ member.role }} · #{{ member.number ?? '—' }}</span>
              </template>
              <template v-else>
                <span>拖拽球员到这里</span>
              </template>
            </div>
          </div>

          <div
            class="pool-panel"
            :class="{ 'drag-over': poolOver }"
            style="margin-top: 16px; min-height: 180px"
            @dragover.prevent="poolOver = true"
            @dragleave="poolOver = false"
            @drop.prevent="onDropPool"
          >
            <div style="font-size: 13px; font-weight: 700">
              可用球员
              <span style="color: var(--muted); font-weight: 400">（不属于任何队伍）</span>
            </div>
            <div class="pool-list">
              <span
                v-for="member in pool"
                :key="member.id"
                class="pool-chip"
                draggable="true"
                @dragstart="onDragStart(member)"
                @dragend="clearDrag"
                @click="assignToFirstEmpty(member)"
              >
                {{ member.name }}
                <small>{{ member.role }} · #{{ member.number ?? '—' }}</small>
              </span>
            </div>
            <div v-if="!pool.length" class="pool-empty">没有可用球员，可在下方快速新增。</div>
          </div>

          <div style="margin-top: 14px; padding: 12px; border-radius: 12px; background: #fff7f0">
            <div style="font-size: 12px; font-weight: 700; margin-bottom: 8px">快速新增队员</div>
            <div style="display: flex; gap: 8px; flex-wrap: wrap">
              <el-input v-model="quickForm.name" placeholder="姓名" style="width: 120px" maxlength="20" />
              <el-select v-model="quickForm.role" style="width: 110px">
                <el-option v-for="role in ['主攻', '副攻', '二传', '接应', '自由人']" :key="role" :label="role" :value="role" />
              </el-select>
              <el-input-number v-model="quickForm.number" :min="0" :max="99" placeholder="号码" style="width: 110px" />
              <el-button :loading="quickAdding" @click="addQuickMember">加入可用列表</el-button>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="10">
          <div style="font-size: 13px; font-weight: 700; margin-bottom: 8px">阵容预览</div>
          <div class="admin-panel" style="padding: 16px">
            <div class="panel-head" style="margin-bottom: 10px">
              <strong>{{ form.name || '未命名队伍' }}</strong>
              <span>{{ form.coach || '无教练' }}</span>
            </div>
            <div class="team-roster">
              <div v-for="(member, index) in slots" :key="index" class="roster-row">
                <span class="pos-badge">{{ index + 1 }}</span>
                <span v-if="member">{{ member.name }} <small style="color: var(--muted)">{{ member.role }}</small></span>
                <span v-else style="color: var(--muted)">待安排</span>
              </div>
            </div>
            <p style="margin-top: 12px; font-size: 12px; color: var(--muted); line-height: 1.7">
              必须满 6 名不重复球员才能保存。保存后，未在阵容中的原队员会自动释放回可用列表。
            </p>
          </div>
        </el-col>
      </el-row>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveTeam">保存队伍</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="memberDialogVisible" :title="memberForm.id ? '编辑队员' : '新增队员'" width="420px">
      <el-form label-position="top">
        <el-form-item label="姓名" required>
          <el-input v-model="memberForm.name" maxlength="20" placeholder="队员姓名" />
        </el-form-item>
        <el-form-item label="位置">
          <el-select v-model="memberForm.role" style="width: 100%">
            <el-option v-for="role in ['主攻', '副攻', '二传', '接应', '自由人']" :key="role" :label="role" :value="role" />
          </el-select>
        </el-form-item>
        <el-form-item label="球衣号码">
          <el-input-number v-model="memberForm.number" :min="0" :max="99" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="memberDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="memberSaving" @click="saveMember">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
