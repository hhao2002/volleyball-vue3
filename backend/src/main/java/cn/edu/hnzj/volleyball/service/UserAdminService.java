package cn.edu.hnzj.volleyball.service;

import cn.edu.hnzj.volleyball.common.BusinessException;
import cn.edu.hnzj.volleyball.common.PageResult;
import cn.edu.hnzj.volleyball.common.Roles;
import cn.edu.hnzj.volleyball.entity.User;
import cn.edu.hnzj.volleyball.mapper.UserMapper;
import cn.edu.hnzj.volleyball.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class UserAdminService {

    private final UserMapper userMapper;

    public UserAdminService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public PageResult<User> page(String keyword, String role, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        List<User> list = userMapper.search(keyword, role, (safePage - 1) * safeSize, safeSize);
        long total = userMapper.countSearch(keyword, role);
        return new PageResult<>(list, total, safePage, safeSize);
    }

    @Transactional
    public User updateRole(Long id, String role) {
        String targetRole = role.toUpperCase(Locale.ROOT);
        if (!Roles.ADMIN.equals(targetRole) && !Roles.SCORER.equals(targetRole) && !Roles.USER.equals(targetRole)) {
            throw new BusinessException("角色不正确");
        }
        User user = requireUser(id);
        if (user.getId().equals(UserContext.id())) {
            throw new BusinessException("不能修改自己的角色");
        }
        if (Roles.ADMIN.equals(user.getRole()) && !Roles.ADMIN.equals(targetRole)
                && userMapper.countByRole(Roles.ADMIN) <= 1) {
            throw new BusinessException("系统至少需要保留一名管理员");
        }
        userMapper.updateRole(id, targetRole);
        return userMapper.findById(id);
    }

    @Transactional
    public User updateStatus(Long id, String status) {
        String targetStatus = status.toUpperCase(Locale.ROOT);
        if (!"ACTIVE".equals(targetStatus) && !"DISABLED".equals(targetStatus)) {
            throw new BusinessException("状态不正确");
        }
        User user = requireUser(id);
        if (user.getId().equals(UserContext.id())) {
            throw new BusinessException("不能禁用自己的账号");
        }
        if ("DISABLED".equals(targetStatus) && Roles.ADMIN.equals(user.getRole())
                && userMapper.countByRole(Roles.ADMIN) <= 1) {
            throw new BusinessException("系统至少需要保留一名可用管理员");
        }
        userMapper.updateStatus(id, targetStatus);
        return userMapper.findById(id);
    }

    private User requireUser(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }
        return user;
    }
}
