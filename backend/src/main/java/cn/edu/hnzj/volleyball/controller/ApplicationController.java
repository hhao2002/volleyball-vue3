package cn.edu.hnzj.volleyball.controller;

import cn.edu.hnzj.volleyball.common.ApiResponse;
import cn.edu.hnzj.volleyball.dto.ApplicationRequest;
import cn.edu.hnzj.volleyball.entity.Application;
import cn.edu.hnzj.volleyball.service.ApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/mine")
    public ApiResponse<List<Application>> mine() {
        return ApiResponse.ok(applicationService.mine());
    }

    @PostMapping
    public ApiResponse<Application> apply(@Valid @RequestBody ApplicationRequest request) {
        return ApiResponse.ok(applicationService.apply(request));
    }
}
