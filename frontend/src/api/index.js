import http from './http'

export const authApi = {
  login: (data) => http.post('/auth/login', data),
  register: (data) => http.post('/auth/register', data),
  me: () => http.get('/auth/me'),
  changePassword: (data) => http.put('/auth/password', data),
  updateProfile: (data) => http.put('/auth/profile', data)
}

export const publicApi = {
  announcements: (limit = 20) => http.get('/public/announcements', { params: { limit } }),
  results: () => http.get('/public/results'),
  resultDetail: (id) => http.get(`/public/results/${id}`)
}

export const liveApi = {
  matches: () => http.get('/live/matches'),
  detail: (id) => http.get(`/live/matches/${id}`)
}

export const scorerApi = {
  matches: () => http.get('/scorer/matches'),
  matchDetail: (id) => http.get(`/scorer/matches/${id}`),
  createMatch: (data) => http.post('/scorer/matches', data),
  startMatch: (id) => http.post(`/scorer/matches/${id}/start`),
  addPoint: (id, data) => http.post(`/scorer/matches/${id}/points`, data),
  undo: (id) => http.post(`/scorer/matches/${id}/undo`),
  deleteMatch: (id) => http.delete(`/scorer/matches/${id}`),

  teams: () => http.get('/scorer/teams'),
  team: (id) => http.get(`/scorer/teams/${id}`),
  createTeam: (data) => http.post('/scorer/teams', data),
  updateTeam: (id, data) => http.put(`/scorer/teams/${id}`, data),
  deleteTeam: (id) => http.delete(`/scorer/teams/${id}`),

  members: (available) =>
    http.get('/scorer/members', { params: available === undefined ? {} : { available } }),
  createMember: (data) => http.post('/scorer/members', data),
  updateMember: (id, data) => http.put(`/scorer/members/${id}`, data),
  deleteMember: (id) => http.delete(`/scorer/members/${id}`)
}

export const applicationApi = {
  mine: () => http.get('/applications/mine'),
  apply: (data) => http.post('/applications', data)
}

export const adminApi = {
  users: (params) => http.get('/admin/users', { params }),
  updateUserRole: (id, role) => http.put(`/admin/users/${id}/role`, { role }),
  updateUserStatus: (id, status) => http.put(`/admin/users/${id}/status`, { status }),
  applications: (params) => http.get('/admin/applications', { params }),
  approveApplication: (id, remark) => http.post(`/admin/applications/${id}/approve`, { remark }),
  rejectApplication: (id, remark) => http.post(`/admin/applications/${id}/reject`, { remark }),
  loginLogs: (params) => http.get('/admin/login-logs', { params }),
  system: () => http.get('/admin/system'),
  announcements: () => http.get('/admin/announcements'),
  createAnnouncement: (data) => http.post('/admin/announcements', data),
  updateAnnouncement: (id, data) => http.put(`/admin/announcements/${id}`, data),
  deleteAnnouncement: (id) => http.delete(`/admin/announcements/${id}`)
}
