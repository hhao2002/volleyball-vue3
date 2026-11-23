package cn.edu.hnzj.volleyball.controller;

import cn.edu.hnzj.volleyball.common.ApiResponse;
import cn.edu.hnzj.volleyball.dto.MemberRequest;
import cn.edu.hnzj.volleyball.dto.TeamSaveRequest;
import cn.edu.hnzj.volleyball.entity.Member;
import cn.edu.hnzj.volleyball.entity.Team;
import cn.edu.hnzj.volleyball.service.TeamService;
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
@RequestMapping("/api/scorer")
public class ScorerTeamController {

    private final TeamService teamService;

    public ScorerTeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/teams")
    public ApiResponse<List<Team>> teams() {
        return ApiResponse.ok(teamService.listAll());
    }

    @GetMapping("/teams/{id}")
    public ApiResponse<Team> team(@PathVariable Long id) {
        return ApiResponse.ok(teamService.detail(id));
    }

    @PostMapping("/teams")
    public ApiResponse<Team> createTeam(@Valid @RequestBody TeamSaveRequest request) {
        return ApiResponse.ok(teamService.create(request));
    }

    @PutMapping("/teams/{id}")
    public ApiResponse<Team> updateTeam(@PathVariable Long id,
                                        @Valid @RequestBody TeamSaveRequest request) {
        return ApiResponse.ok(teamService.update(id, request));
    }

    @DeleteMapping("/teams/{id}")
    public ApiResponse<Void> deleteTeam(@PathVariable Long id) {
        teamService.delete(id);
        return ApiResponse.ok();
    }

    @GetMapping("/members")
    public ApiResponse<List<Member>> members(@RequestParam(required = false) Boolean available) {
        return ApiResponse.ok(Boolean.TRUE.equals(available)
                ? teamService.availableMembers() : teamService.allMembers());
    }

    @PostMapping("/members")
    public ApiResponse<Member> createMember(@Valid @RequestBody MemberRequest request) {
        return ApiResponse.ok(teamService.createMember(request));
    }

    @PutMapping("/members/{id}")
    public ApiResponse<Member> updateMember(@PathVariable Long id,
                                            @Valid @RequestBody MemberRequest request) {
        return ApiResponse.ok(teamService.updateMember(id, request));
    }

    @DeleteMapping("/members/{id}")
    public ApiResponse<Void> deleteMember(@PathVariable Long id) {
        teamService.deleteMember(id);
        return ApiResponse.ok();
    }
}
