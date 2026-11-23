package cn.edu.hnzj.volleyball.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class LiveBroadcaster {

    private final ObjectMapper objectMapper;
    private final Map<Long, Set<WebSocketSession>> matchSessions = new ConcurrentHashMap<>();

    public LiveBroadcaster(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void register(Long matchId, WebSocketSession session) {
        matchSessions.computeIfAbsent(matchId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void unregister(WebSocketSession session) {
        matchSessions.values().forEach(set -> set.remove(session));
    }

    public void broadcast(Long matchId, String type, Object data) {
        Set<WebSocketSession> sessions = matchSessions.get(matchId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", type);
        payload.put("data", data);
        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            log.error("serialize ws payload failed", e);
            return;
        }
        sessions.forEach(session -> send(session, json));
    }

    private void send(WebSocketSession session, String json) {
        if (!session.isOpen()) {
            unregister(session);
            return;
        }
        try {
            synchronized (session) {
                session.sendMessage(new TextMessage(json));
            }
        } catch (Exception e) {
            log.warn("send websocket message failed: {}", e.getMessage());
            unregister(session);
        }
    }
}
