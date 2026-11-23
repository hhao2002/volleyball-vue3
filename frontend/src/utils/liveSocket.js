export function connectLive(matchId, token, handlers = {}) {
  let socket = null
  let pingTimer = null
  let reconnectTimer = null
  let attempts = 0
  let closedByUser = false

  const connect = () => {
    if (closedByUser) return
    const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws'
    const url = `${protocol}://${window.location.host}/ws/live?token=${encodeURIComponent(token)}&matchId=${matchId}`
    socket = new WebSocket(url)

    socket.onopen = () => {
      attempts = 0
      handlers.onOpen?.()
      pingTimer = window.setInterval(() => {
        if (socket?.readyState === WebSocket.OPEN) socket.send('ping')
      }, 25000)
    }

    socket.onmessage = (event) => {
      if (event.data === 'pong') return
      try {
        handlers.onMessage?.(JSON.parse(event.data))
      } catch {
        // 忽略非 JSON 消息
      }
    }

    socket.onclose = () => {
      window.clearInterval(pingTimer)
      handlers.onClose?.()
      if (!closedByUser && attempts < 6) {
        attempts += 1
        const delay = Math.min(1000 * 2 ** (attempts - 1), 15000)
        reconnectTimer = window.setTimeout(connect, delay)
      }
    }

    socket.onerror = () => {
      socket?.close()
    }
  }

  connect()

  return {
    close() {
      closedByUser = true
      window.clearInterval(pingTimer)
      window.clearTimeout(reconnectTimer)
      socket?.close()
    }
  }
}
