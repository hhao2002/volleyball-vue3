package cn.edu.hnzj.volleyball.controller;

import cn.edu.hnzj.volleyball.common.ApiResponse;
import cn.edu.hnzj.volleyball.common.BusinessException;
import cn.edu.hnzj.volleyball.dto.MatchDetailVO;
import cn.edu.hnzj.volleyball.dto.MatchSummaryVO;
import cn.edu.hnzj.volleyball.entity.Match;
import cn.edu.hnzj.volleyball.service.MatchQueryService;
import cn.edu.hnzj.volleyball.service.MatchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/live")
public class LiveController {

    private final MatchService matchService;
    private final MatchQueryService matchQueryService;

    public LiveController(MatchService matchService, MatchQueryService matchQueryService) {
        this.matchService = matchService;
        this.matchQueryService = matchQueryService;
    }

    @GetMapping("/matches")
    public ApiResponse<List<MatchSummaryVO>> liveMatches() {
        return ApiResponse.ok(matchService.liveMatches());
    }

    @GetMapping("/matches/{id}")
    public ApiResponse<MatchDetailVO> detail(@PathVariable Long id) {
        MatchDetailVO detail = matchQueryService.detail(id);
        if (Match.READY.equals(detail.getMatch().getState())) {
            throw BusinessException.notFound("比赛尚未开始");
        }
        return ApiResponse.ok(detail);
    }
}
