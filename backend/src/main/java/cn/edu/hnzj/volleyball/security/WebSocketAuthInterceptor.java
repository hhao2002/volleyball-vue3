package cn.edu.hnzj.volleyball.security;

import cn.edu.hnzj.volleyball.entity.User;
import cn.edu.hnzj.volleyball.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = UriComponentsBuilder.fromUri(request.getURI()).build()
                .getQueryParams().getFirst("token");
        String matchId = UriComponentsBuilder.fromUri(request.getURI()).build()
                .getQueryParams().getFirst("matchId");
        if (!StringUtils.hasText(token) || !StringUtils.hasText(matchId)) {
            return false;
        }
        try {
            Claims claims = jwtUtil.parse(token);
            User user = userMapper.findById(Long.valueOf(claims.getSubject()));
            if (user == null || !"ACTIVE".equals(user.getStatus())) {
                return false;
            }
            attributes.put("userId", user.getId());
            attributes.put("nickname", user.getNickname());
            attributes.put("role", user.getRole());
            attributes.put("matchId", Long.valueOf(matchId));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
