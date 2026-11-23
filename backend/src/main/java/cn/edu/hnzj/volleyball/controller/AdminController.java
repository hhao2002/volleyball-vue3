package cn.edu.hnzj.volleyball.controller;

import cn.edu.hnzj.volleyball.common.ApiResponse;
import cn.edu.hnzj.volleyball.common.PageResult;
import cn.edu.hnzj.volleyball.dto.AnnouncementRequest;
import cn.edu.hnzj.volleyball.dto.HandleApplicationRequest;
import cn.edu.hnzj.volleyball.dto.RoleUpdateRequest;
import cn.edu.hnzj.volleyball.dto.StatusUpdateRequest;
import cn.edu.hnzj.volleyball.dto.SystemInfoVO;
import cn.edu.hnzj.volleyball.entity.Announcement;
import cn.edu.hnzj.volleyball.entity.Application;
import cn.edu.hnzj.volleyball.entity.LoginLog;
import cn.edu.hnzj.volleyball.entity.User;
import cn.edu.hnzj.volleyball.mapper.LoginLogMapper;
import cn.edu.hnzj.volleyball.service.AnnouncementService;
import cn.edu.hnzj.volleyball.service.ApplicationService;
import cn.edu.hnzj.volleyball.service.SystemService;
import cn.edu.hnzj.volleyball.service.UserAdminService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserAdminService userAdminService;
    private final ApplicationService applicationService;
    private final AnnouncementService announcementService;
    private final SystemService systemService;
    private final LoginLogMapper loginLogMapper;

    public AdminController(UserAdminService userAdminService, ApplicationService applicationService,
                           AnnouncementService announcementService, SystemService systemService,
                           LoginLogMapper loginLogMapper) {
        this.userAdminService = userAdminService;
        this.applicationService = applicationService;
        this.announcementService = announcementService;
        this.systemService = systemService;
        this.loginLogMapper = loginLogMapper;
    }

    @GetMapping("/users")
    public ApiResponse<PageResult<User>> users(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) String role) {
        return ApiResponse.ok(userAdminService.page(keyword, role, page, size));
    }

    @PutMapping("/users/{id}/role")
    public ApiResponse<User> updateRole(@PathVariable Long id,
                                        @Valid @RequestBody RoleUpdateRequest request) {
        return ApiResponse.ok(userAdminService.updateRole(id, request.getRole()));
    }

    @PutMapping("/users/{id}/status")
    public ApiResponse<User> updateStatus(@PathVariable Long id,
                                          @Valid @RequestBody StatusUpdateRequest request) {
        return ApiResponse.ok(userAdminService.updateStatus(id, request.getStatus()));
    }

    @GetMapping("/applications")
    public ApiResponse<PageResult<Application>> applications(@RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(required = false) String status) {
        return ApiResponse.ok(applicationService.adminPage(status, page, size));
    }

    @PostMapping("/applications/{id}/approve")
    public ApiResponse<Application> approve(@PathVariable Long id,
                                            @RequestBody(required = false) HandleApplicationRequest request) {
        return ApiResponse.ok(applicationService.approve(id, request == null ? null : request.getRemark()));
    }

    @PostMapping("/applications/{id}/reject")
    public ApiResponse<Application> reject(@PathVariable Long id,
                                           @RequestBody(required = false) HandleApplicationRequest request) {
        return ApiResponse.ok(applicationService.reject(id, request == null ? null : request.getRemark()));
    }

    @GetMapping("/login-logs")
    public ApiResponse<PageResult<LoginLog>> loginLogs(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "15") int size,
                                                       @RequestParam(required = false) String keyword) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        List<LoginLog> list = loginLogMapper.search(keyword, (safePage - 1) * safeSize, safeSize);
        long total = loginLogMapper.countSearch(keyword);
        return ApiResponse.ok(new PageResult<>(list, total, safePage, safeSize));
    }

    @GetMapping("/system")
    public ApiResponse<SystemInfoVO> system() {
        return ApiResponse.ok(systemService.info());
    }

    @GetMapping("/announcements")
    public ApiResponse<List<Announcement>> announcements() {
        return ApiResponse.ok(announcementService.findAll());
    }

    @PostMapping("/announcements")
    public ApiResponse<Announcement> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        return ApiResponse.ok(announcementService.create(request));
    }

    @PutMapping("/announcements/{id}")
    public ApiResponse<Announcement> updateAnnouncement(@PathVariable Long id,
                                                        @Valid @RequestBody AnnouncementRequest request) {
        return ApiResponse.ok(announcementService.update(id, request));
    }

    @DeleteMapping("/announcements/{id}")
    public ApiResponse<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.delete(id);
        return ApiResponse.ok();
    }
}
