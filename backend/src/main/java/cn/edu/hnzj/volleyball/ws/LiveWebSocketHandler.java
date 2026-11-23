package cn.edu.hnzj.volleyball.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LiveWebSocketHandler extends TextWebSocketHandler {

    private final LiveBroadcaster broadcaster;
    private final ObjectMapper objectMapper;

    public LiveWebSocketHandler(LiveBroadcaster broadcaster, ObjectMapper objectMapper) {
        this.broadcaster = broadcaster;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long matchId = (Long) session.getAttributes().get("matchId");
        if (matchId == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }
        broadcaster.register(matchId, session);
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "CONNECTED");
        payload.put("matchId", matchId);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if ("ping".equalsIgnoreCase(message.getPayload())) {
            session.sendMessage(new TextMessage("pong"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        broadcaster.unregister(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.debug("websocket transport error: {}", exception.getMessage());
        broadcaster.unregister(session);
    }
}
