package cn.edu.hnzj.volleyball.service;

import cn.edu.hnzj.volleyball.common.BusinessException;
import cn.edu.hnzj.volleyball.common.Roles;
import cn.edu.hnzj.volleyball.dto.ChangePasswordRequest;
import cn.edu.hnzj.volleyball.dto.LoginRequest;
import cn.edu.hnzj.volleyball.dto.ProfileRequest;
import cn.edu.hnzj.volleyball.dto.RegisterRequest;
import cn.edu.hnzj.volleyball.entity.LoginLog;
import cn.edu.hnzj.volleyball.entity.User;
import cn.edu.hnzj.volleyball.mapper.LoginLogMapper;
import cn.edu.hnzj.volleyball.mapper.UserMapper;
import cn.edu.hnzj.volleyball.security.JwtUtil;
import cn.edu.hnzj.volleyball.security.UserContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final LoginLogMapper loginLogMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, LoginLogMapper loginLogMapper,
                       BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.loginLogMapper = loginLogMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public Map<String, Object> register(RegisterRequest request) {
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new BusinessException("用户名已被占用");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname() : request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(Roles.USER);
        user.setStatus("ACTIVE");
        userMapper.insert(user);
        return buildLoginResult(userMapper.findById(user.getId()));
    }

    @Transactional
    public Map<String, Object> login(LoginRequest request, HttpServletRequest httpRequest) {
        String ip = resolveIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        if (userAgent != null && userAgent.length() > 250) {
            userAgent = userAgent.substring(0, 250);
        }

        User user = userMapper.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            recordLog(user == null ? null : user.getId(), request.getUsername(), ip, userAgent,
                    false, "用户名或密码错误");
            throw BusinessException.unauthorized("用户名或密码错误");
        }
        if (!"ACTIVE".equals(user.getStatus())) {
            recordLog(user.getId(), user.getUsername(), ip, userAgent, false, "账号已被禁用");
            throw BusinessException.forbidden("账号已被禁用，请联系管理员");
        }

        userMapper.updateLastLogin(user.getId(), LocalDateTime.now());
        recordLog(user.getId(), user.getUsername(), ip, userAgent, true, "登录成功");
        return buildLoginResult(userMapper.findById(user.getId()));
    }

    public User me() {
        User user = userMapper.findById(UserContext.id());
        if (user == null) {
            throw BusinessException.unauthorized("用户不存在");
        }
        return user;
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = userMapper.findById(UserContext.id());
        if (user == null || !passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }
        userMapper.updatePassword(user.getId(), passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public User updateProfile(ProfileRequest request) {
        userMapper.updateProfile(UserContext.id(), request.getNickname(), request.getEmail());
        return userMapper.findById(UserContext.id());
    }

    private Map<String, Object> buildLoginResult(User user) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", jwtUtil.generate(user));
        result.put("user", user);
        return result;
    }

    private void recordLog(Long userId, String username, String ip, String userAgent,
                           boolean success, String message) {
        LoginLog log = new LoginLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setIp(ip);
        log.setUserAgent(userAgent);
        log.setSuccess(success ? 1 : 0);
        log.setMessage(message);
        loginLogMapper.insert(log);
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
