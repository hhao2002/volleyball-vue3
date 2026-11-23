package cn.edu.hnzj.volleyball.security;

import cn.edu.hnzj.volleyball.common.BusinessException;
import cn.edu.hnzj.volleyball.common.Roles;
import cn.edu.hnzj.volleyball.entity.User;
import cn.edu.hnzj.volleyball.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    public AuthInterceptor(JwtUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        if (isPublic(path)) {
            return true;
        }

        String token = resolveToken(request);
        if (!StringUtils.hasText(token)) {
            throw BusinessException.unauthorized("请先登录");
        }

        Claims claims;
        try {
            claims = jwtUtil.parse(token);
        } catch (Exception e) {
            throw BusinessException.unauthorized("登录状态已失效，请重新登录");
        }

        User user = userMapper.findById(Long.valueOf(claims.getSubject()));
        if (user == null || !"ACTIVE".equals(user.getStatus())) {
            throw BusinessException.unauthorized("账号不存在或已被禁用");
        }

        CurrentUser current = new CurrentUser(user.getId(), user.getUsername(), user.getNickname(), user.getRole());
        UserContext.set(current);

        if (path.startsWith("/api/admin/") && !Roles.isAdmin(user.getRole())) {
            throw BusinessException.forbidden("仅管理员可访问");
        }
        if (path.startsWith("/api/scorer/") && !Roles.isScorer(user.getRole())) {
            throw BusinessException.forbidden("仅计分员或管理员可访问");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private boolean isPublic(String path) {
        return path.equals("/api/auth/login")
                || path.equals("/api/auth/register")
                || path.startsWith("/api/public/")
                || path.startsWith("/error");
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
