// 统一 API 封装：全部走 /api 前缀，由 Vite dev server 代理到后端
const BASE = '/api'

async function request(path, { method = 'GET', json, form } = {}) {
  const init = {
    method,
    credentials: 'include', // 保证 JSESSIONID cookie 随请求发送
    headers: {},
  }
  if (json !== undefined) {
    init.headers['Content-Type'] = 'application/json'
    init.body = JSON.stringify(json)
  } else if (form) {
    init.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8'
    init.body = new URLSearchParams(form).toString()
  }

  const resp = await fetch(BASE + path, init)
  const text = await resp.text()
  let data = null
  if (text) {
    try {
      data = JSON.parse(text)
    } catch {
      data = text
    }
  }
  if (!resp.ok) {
    throw new Error(`请求失败（HTTP ${resp.status}）：${typeof data === 'string' ? data.slice(0, 120) : ''}`)
  }
  return data
}

export const api = {
  login: (username, password) => request('/login.do', { method: 'POST', json: { username, password } }),
  logout: () => request('/logout.do', { method: 'POST' }),
  currentUser: () => request('/currentUser.do'),
  matches: () => request('/matches.do'),
  match: (id) => request(`/match.do?id=${encodeURIComponent(id)}`),
  freeTeams: () => request('/freeTeams.do'),
  addMatch: (payload) => request('/addMatch.do', { method: 'POST', form: payload }),
  deleteMatch: (id) => request(`/deleteMatch.do?id=${encodeURIComponent(id)}`, { method: 'POST' }),
  updateScores: (payload) => request('/updateScores.do', { method: 'POST', json: payload }),
  finishMatch: (id) => request(`/finishMatch.do?id=${encodeURIComponent(id)}`, { method: 'POST' }),
}

export default api