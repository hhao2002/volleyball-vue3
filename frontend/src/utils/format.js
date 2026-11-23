import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

export function formatTime(value, pattern = 'YYYY-MM-DD HH:mm') {
  if (!value) return '—'
  return dayjs(value).format(pattern)
}

export function fromNow(value) {
  if (!value) return ''
  return dayjs(value).fromNow()
}

export const stateMap = {
  READY: { label: '准备中', type: 'info' },
  ONGOING: { label: '进行中', type: 'warning' },
  FINISHED: { label: '已结束', type: 'success' }
}

export const roleMap = {
  ADMIN: { label: '管理员', type: 'danger' },
  SCORER: { label: '计分员', type: 'warning' },
  USER: { label: '普通用户', type: 'info' }
}

export const applicationTypeMap = {
  SCORER: '申请计分员',
  ADMIN: '申请管理员'
}

export const applicationStatusMap = {
  PENDING: { label: '待处理', type: 'warning' },
  APPROVED: { label: '已通过', type: 'success' },
  REJECTED: { label: '已拒绝', type: 'danger' }
}

export const announcementTypeMap = {
  MANUAL: { label: '公告', type: 'warning' },
  MATCH_START: { label: '开赛', type: 'danger' },
  MATCH_END: { label: '赛果', type: 'success' }
}

export function scoreTypeLabel(type) {
  return type === 'OUT' ? '出界得分' : '球员得分'
}
