package cn.edu.hnzj.volleyball.service;

import cn.edu.hnzj.volleyball.common.BusinessException;
import cn.edu.hnzj.volleyball.common.PageResult;
import cn.edu.hnzj.volleyball.common.Roles;
import cn.edu.hnzj.volleyball.dto.ApplicationRequest;
import cn.edu.hnzj.volleyball.entity.Application;
import cn.edu.hnzj.volleyball.entity.User;
import cn.edu.hnzj.volleyball.mapper.ApplicationMapper;
import cn.edu.hnzj.volleyball.mapper.UserMapper;
import cn.edu.hnzj.volleyball.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class ApplicationService {

    private final ApplicationMapper applicationMapper;
    private final UserMapper userMapper;

    public ApplicationService(ApplicationMapper applicationMapper, UserMapper userMapper) {
        this.applicationMapper = applicationMapper;
        this.userMapper = userMapper;
    }

    public List<Application> mine() {
        return applicationMapper.findByUser(UserContext.id());
    }

    @Transactional
    public Application apply(ApplicationRequest request) {
        String type = request.getType().toUpperCase(Locale.ROOT);
        if (!Roles.SCORER.equals(type) && !Roles.ADMIN.equals(type)) {
            throw new BusinessException("申请类型不正确");
        }
        User user = userMapper.findById(UserContext.id());
        if (Roles.ADMIN.equals(user.getRole())) {
            throw new BusinessException("管理员无需提交角色申请");
        }
        if (type.equals(user.getRole())) {
            throw new BusinessException("你已经是该角色，无需申请");
        }
        if (applicationMapper.countPending(user.getId(), type) > 0) {
            throw new BusinessException("已有相同的申请正在审核中，请耐心等待");
        }
        Application application = new Application();
        application.setUserId(user.getId());
        application.setType(type);
        application.setReason(request.getReason());
        application.setStatus("PENDING");
        applicationMapper.insert(application);
        return applicationMapper.findById(application.getId());
    }

    public PageResult<Application> adminPage(String status, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        List<Application> list = applicationMapper.search(status, (safePage - 1) * safeSize, safeSize);
        long total = applicationMapper.countSearch(status);
        return new PageResult<>(list, total, safePage, safeSize);
    }

    @Transactional
    public Application approve(Long id, String remark) {
        Application application = requirePending(id);
        String role = Roles.SCORER.equals(application.getType()) ? Roles.SCORER : Roles.ADMIN;
        applicationMapper.updateHandle(id, "APPROVED", UserContext.id(),
                StringUtils.hasText(remark) ? remark : "申请已通过", LocalDateTime.now());
        userMapper.updateRole(application.getUserId(), role);
        return applicationMapper.findById(id);
    }

    @Transactional
    public Application reject(Long id, String remark) {
        requirePending(id);
        applicationMapper.updateHandle(id, "REJECTED", UserContext.id(),
                StringUtils.hasText(remark) ? remark : "申请未通过", LocalDateTime.now());
        return applicationMapper.findById(id);
    }

    private Application requirePending(Long id) {
        Application application = applicationMapper.findById(id);
        if (application == null) {
            throw BusinessException.notFound("申请不存在");
        }
        if (!"PENDING".equals(application.getStatus())) {
            throw new BusinessException("该申请已处理，请勿重复操作");
        }
        return application;
    }
}
