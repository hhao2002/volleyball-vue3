package cn.edu.hnzj.volleyball.controller;

import cn.edu.hnzj.volleyball.common.ApiResponse;
import cn.edu.hnzj.volleyball.dto.ChangePasswordRequest;
import cn.edu.hnzj.volleyball.dto.LoginRequest;
import cn.edu.hnzj.volleyball.dto.ProfileRequest;
import cn.edu.hnzj.volleyball.dto.RegisterRequest;
import cn.edu.hnzj.volleyball.entity.User;
import cn.edu.hnzj.volleyball.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request,
                                                  HttpServletRequest httpRequest) {
        return ApiResponse.ok(authService.login(request, httpRequest));
    }

    @GetMapping("/me")
    public ApiResponse<User> me() {
        return ApiResponse.ok(authService.me());
    }

    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ApiResponse.ok();
    }

    @PutMapping("/profile")
    public ApiResponse<User> updateProfile(@Valid @RequestBody ProfileRequest request) {
        return ApiResponse.ok(authService.updateProfile(request));
    }
}
