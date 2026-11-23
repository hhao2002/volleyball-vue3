package cn.edu.hnzj.volleyball.controller;

import cn.edu.hnzj.volleyball.common.ApiResponse;
import cn.edu.hnzj.volleyball.common.BusinessException;
import cn.edu.hnzj.volleyball.dto.MatchDetailVO;
import cn.edu.hnzj.volleyball.dto.MatchSummaryVO;
import cn.edu.hnzj.volleyball.entity.Announcement;
import cn.edu.hnzj.volleyball.entity.Match;
import cn.edu.hnzj.volleyball.service.AnnouncementService;
import cn.edu.hnzj.volleyball.service.MatchQueryService;
import cn.edu.hnzj.volleyball.service.MatchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final AnnouncementService announcementService;
    private final MatchService matchService;
    private final MatchQueryService matchQueryService;

    public PublicController(AnnouncementService announcementService, MatchService matchService,
                            MatchQueryService matchQueryService) {
        this.announcementService = announcementService;
        this.matchService = matchService;
        this.matchQueryService = matchQueryService;
    }

    @GetMapping("/announcements")
    public ApiResponse<List<Announcement>> announcements(@RequestParam(defaultValue = "20") int limit) {
        return ApiResponse.ok(announcementService.latest(limit));
    }

    @GetMapping("/results")
    public ApiResponse<List<MatchSummaryVO>> results() {
        return ApiResponse.ok(matchService.finishedResults());
    }

    @GetMapping("/results/{id}")
    public ApiResponse<MatchDetailVO> resultDetail(@PathVariable Long id) {
        MatchDetailVO detail = matchQueryService.detail(id);
        if (!Match.FINISHED.equals(detail.getMatch().getState())) {
            throw BusinessException.notFound("比赛结果不存在或尚未结束");
        }
        return ApiResponse.ok(detail);
    }
}
