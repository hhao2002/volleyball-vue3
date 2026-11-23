package cn.edu.hnzj.volleyball.controller;

import cn.edu.hnzj.volleyball.common.ApiResponse;
import cn.edu.hnzj.volleyball.common.BusinessException;
import cn.edu.hnzj.volleyball.dto.CreateMatchRequest;
import cn.edu.hnzj.volleyball.dto.MatchDetailVO;
import cn.edu.hnzj.volleyball.dto.MatchSummaryVO;
import cn.edu.hnzj.volleyball.dto.PointRequest;
import cn.edu.hnzj.volleyball.security.UserContext;
import cn.edu.hnzj.volleyball.service.MatchQueryService;
import cn.edu.hnzj.volleyball.service.MatchService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/scorer/matches")
public class ScorerMatchController {

    private final MatchService matchService;
    private final MatchQueryService matchQueryService;

    public ScorerMatchController(MatchService matchService, MatchQueryService matchQueryService) {
        this.matchService = matchService;
        this.matchQueryService = matchQueryService;
    }

    @GetMapping
    public ApiResponse<List<MatchSummaryVO>> list() {
        return ApiResponse.ok(matchService.listMine());
    }

    @PostMapping
    public ApiResponse<MatchDetailVO> create(@Valid @RequestBody CreateMatchRequest request) {
        return ApiResponse.ok(matchService.create(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<MatchDetailVO> detail(@PathVariable Long id) {
        MatchDetailVO detail = matchQueryService.detail(id);
        boolean creator = detail.getMatch().getCreatorId().equals(UserContext.id());
        if (!creator && !UserContext.isAdmin()) {
            throw BusinessException.forbidden("无权查看该比赛");
        }
        return ApiResponse.ok(detail);
    }

    @PostMapping("/{id}/start")
    public ApiResponse<MatchDetailVO> start(@PathVariable Long id) {
        return ApiResponse.ok(matchService.start(id));
    }

    @PostMapping("/{id}/points")
    public ApiResponse<MatchDetailVO> addPoint(@PathVariable Long id,
                                               @Valid @RequestBody PointRequest request) {
        return ApiResponse.ok(matchService.addPoint(id, request));
    }

    @PostMapping("/{id}/undo")
    public ApiResponse<MatchDetailVO> undo(@PathVariable Long id) {
        return ApiResponse.ok(matchService.undoLastPoint(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        matchService.delete(id);
        return ApiResponse.ok();
    }
}
