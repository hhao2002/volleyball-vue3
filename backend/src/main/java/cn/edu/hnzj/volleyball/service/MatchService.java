package cn.edu.hnzj.volleyball.service;

import cn.edu.hnzj.volleyball.common.BusinessException;
import cn.edu.hnzj.volleyball.dto.CreateMatchRequest;
import cn.edu.hnzj.volleyball.dto.MatchDetailVO;
import cn.edu.hnzj.volleyball.dto.MatchSummaryVO;
import cn.edu.hnzj.volleyball.dto.PointRequest;
import cn.edu.hnzj.volleyball.entity.Match;
import cn.edu.hnzj.volleyball.entity.MatchSet;
import cn.edu.hnzj.volleyball.entity.Member;
import cn.edu.hnzj.volleyball.entity.ScoreRecord;
import cn.edu.hnzj.volleyball.entity.Team;
import cn.edu.hnzj.volleyball.entity.Vp;
import cn.edu.hnzj.volleyball.mapper.AnnouncementMapper;
import cn.edu.hnzj.volleyball.mapper.MatchMapper;
import cn.edu.hnzj.volleyball.mapper.MatchSetMapper;
import cn.edu.hnzj.volleyball.mapper.MemberMapper;
import cn.edu.hnzj.volleyball.mapper.ScoreRecordMapper;
import cn.edu.hnzj.volleyball.mapper.TeamMapper;
import cn.edu.hnzj.volleyball.mapper.VpMapper;
import cn.edu.hnzj.volleyball.security.UserContext;
import cn.edu.hnzj.volleyball.ws.LiveBroadcaster;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class MatchService {

    private final MatchMapper matchMapper;
    private final MatchSetMapper matchSetMapper;
    private final ScoreRecordMapper scoreRecordMapper;
    private final VpMapper vpMapper;
    private final TeamMapper teamMapper;
    private final MemberMapper memberMapper;
    private final AnnouncementMapper announcementMapper;
    private final AnnouncementService announcementService;
    private final MatchQueryService queryService;
    private final LiveBroadcaster broadcaster;
    private final VpCalculator vpCalculator;

    public MatchService(MatchMapper matchMapper, MatchSetMapper matchSetMapper,
                        ScoreRecordMapper scoreRecordMapper, VpMapper vpMapper,
                        TeamMapper teamMapper, MemberMapper memberMapper,
                        AnnouncementMapper announcementMapper, AnnouncementService announcementService,
                        MatchQueryService queryService, LiveBroadcaster broadcaster,
                        VpCalculator vpCalculator) {
        this.matchMapper = matchMapper;
        this.matchSetMapper = matchSetMapper;
        this.scoreRecordMapper = scoreRecordMapper;
        this.vpMapper = vpMapper;
        this.teamMapper = teamMapper;
        this.memberMapper = memberMapper;
        this.announcementMapper = announcementMapper;
        this.announcementService = announcementService;
        this.queryService = queryService;
        this.broadcaster = broadcaster;
        this.vpCalculator = vpCalculator;
    }

    @Transactional
    public MatchDetailVO create(CreateMatchRequest request) {
        if (request.getLeftTeamId().equals(request.getRightTeamId())) {
            throw new BusinessException("主队和客队不能相同");
        }
        int bestOf = request.getBestOf() == null ? 5 : request.getBestOf();
        if (bestOf % 2 == 0) {
            throw new BusinessException("比赛局数必须为奇数，例如 3 局 2 胜或 5 局 3 胜");
        }
        Team left = requireTeam(request.getLeftTeamId());
        Team right = requireTeam(request.getRightTeamId());
        ensureLineup(left);
        ensureLineup(right);
        ensureTeamFree(left);
        ensureTeamFree(right);

        Match match = new Match();
        match.setName(request.getName().trim());
        match.setSponsor(request.getSponsor());
        match.setBestOf(bestOf);
        match.setPointsPerSet(request.getPointsPerSet() == null ? 25 : request.getPointsPerSet());
        match.setFinalSetPoints(request.getFinalSetPoints() == null ? 15 : request.getFinalSetPoints());
        match.setRules(StringUtils.hasText(request.getRules())
                ? request.getRules().trim()
                : defaultRules(match));
        match.setState(Match.READY);
        match.setCreatorId(UserContext.id());
        match.setLeftTeamId(left.getId());
        match.setRightTeamId(right.getId());
        match.setEventSeq(0);
        matchMapper.insert(match);
        return queryService.detail(match.getId());
    }

    @Transactional
    public MatchDetailVO start(Long id) {
        Match match = requireMatch(id);
        ensureCreator(match);
        if (!Match.READY.equals(match.getState())) {
            throw new BusinessException("只有准备中的比赛可以开始");
        }
        Team left = requireTeam(match.getLeftTeamId());
        Team right = requireTeam(match.getRightTeamId());
        ensureLineup(left);
        ensureLineup(right);
        ensureTeamFree(left);
        ensureTeamFree(right);

        matchMapper.start(id, LocalDateTime.now());
        Match started = matchMapper.findById(id);
        announcementService.announceMatchStart(started, left, right);
        return broadcast(id);
    }

    @Transactional
    public MatchDetailVO addPoint(Long id, PointRequest request) {
        Match match = requireOngoingOwned(id);
        boolean isLeft = request.getTeamId().equals(match.getLeftTeamId());
        boolean isRight = request.getTeamId().equals(match.getRightTeamId());
        if (!isLeft && !isRight) {
            throw new BusinessException("该队伍不属于本场比赛");
        }
        String type = StringUtils.hasText(request.getType())
                ? request.getType().toUpperCase(Locale.ROOT) : ScoreRecord.TYPE_ATTACK;
        if (!ScoreRecord.TYPE_ATTACK.equals(type) && !ScoreRecord.TYPE_OUT.equals(type)) {
            throw new BusinessException("未知的得分类型");
        }

        Long memberId = null;
        if (ScoreRecord.TYPE_OUT.equals(type)) {
            memberId = null;
        } else {
            if (request.getMemberId() == null) {
                throw new BusinessException("请选择得分队员");
            }
            Member member = memberMapper.findById(request.getMemberId());
            if (member == null || !request.getTeamId().equals(member.getTeamId())) {
                throw new BusinessException("该队员不属于得分队伍");
            }
            memberId = member.getId();
        }

        int seq = match.getEventSeq() + 1;
        ScoreRecord record = new ScoreRecord();
        record.setMatchId(id);
        record.setSeq(seq);
        record.setSetNo(match.getCurrentSet());
        record.setTeamId(request.getTeamId());
        record.setMemberId(memberId);
        record.setType(type);
        record.setCreatedBy(UserContext.id());
        scoreRecordMapper.insert(record);

        int leftPoints = match.getLeftPoints();
        int rightPoints = match.getRightPoints();
        if (isLeft) {
            leftPoints++;
        } else {
            rightPoints++;
        }

        int leftSets = match.getLeftSets();
        int rightSets = match.getRightSets();
        int currentSet = match.getCurrentSet();
        int target = setTarget(match, currentSet);

        boolean setWon = (isLeft && leftPoints >= target && leftPoints - rightPoints >= 2)
                || (isRight && rightPoints >= target && rightPoints - leftPoints >= 2);
        if (setWon) {
            MatchSet set = new MatchSet();
            set.setMatchId(id);
            set.setSetNo(currentSet);
            set.setLeftPoints(leftPoints);
            set.setRightPoints(rightPoints);
            set.setWinnerTeamId(isLeft ? match.getLeftTeamId() : match.getRightTeamId());
            matchSetMapper.insert(set);

            if (isLeft) {
                leftSets++;
            } else {
                rightSets++;
            }

            if (Math.max(leftSets, rightSets) > match.getBestOf() / 2) {
                Team left = requireTeam(match.getLeftTeamId());
                Team right = requireTeam(match.getRightTeamId());
                finishMatch(match, left, right, seq, currentSet, leftSets, rightSets, leftPoints, rightPoints);
                return broadcast(id);
            }
            currentSet++;
            leftPoints = 0;
            rightPoints = 0;
        }

        matchMapper.updateLive(id, currentSet, leftSets, rightSets, leftPoints, rightPoints, seq);
        return broadcast(id);
    }

    @Transactional
    public MatchDetailVO undoLastPoint(Long id) {
        Match match = requireOngoingOwned(id);
        ScoreRecord last = scoreRecordMapper.findLast(id);
        if (last == null) {
            throw new BusinessException("暂无可撤销的得分记录");
        }
        scoreRecordMapper.deleteById(last.getId());

        List<ScoreRecord> events = scoreRecordMapper.findByMatch(id);
        matchSetMapper.deleteByMatch(id);
        int currentSet = 1;
        int leftSets = 0;
        int rightSets = 0;
        int leftPoints = 0;
        int rightPoints = 0;
        for (ScoreRecord event : events) {
            boolean isLeft = event.getTeamId().equals(match.getLeftTeamId());
            if (isLeft) {
                leftPoints++;
            } else {
                rightPoints++;
            }
            int target = setTarget(match, currentSet);
            boolean setWon = (isLeft && leftPoints >= target && leftPoints - rightPoints >= 2)
                    || (!isLeft && rightPoints >= target && rightPoints - leftPoints >= 2);
            if (setWon) {
                MatchSet set = new MatchSet();
                set.setMatchId(id);
                set.setSetNo(currentSet);
                set.setLeftPoints(leftPoints);
                set.setRightPoints(rightPoints);
                set.setWinnerTeamId(isLeft ? match.getLeftTeamId() : match.getRightTeamId());
                matchSetMapper.insert(set);
                if (isLeft) {
                    leftSets++;
                } else {
                    rightSets++;
                }
                if (Math.max(leftSets, rightSets) > match.getBestOf() / 2) {
                    break;
                }
                currentSet++;
                leftPoints = 0;
                rightPoints = 0;
            }
        }
        int seq = events.isEmpty() ? 0 : events.get(events.size() - 1).getSeq();
        matchMapper.updateLive(id, currentSet, leftSets, rightSets, leftPoints, rightPoints, seq);
        return broadcast(id);
    }

    @Transactional
    public void delete(Long id) {
        Match match = requireMatch(id);
        if (Match.ONGOING.equals(match.getState())) {
            throw new BusinessException("比赛正在进行中，无法删除");
        }
        if (!UserContext.isAdmin()) {
            ensureCreator(match);
            if (!Match.READY.equals(match.getState())) {
                throw new BusinessException("计分员只能删除准备中的比赛");
            }
        }
        scoreRecordMapper.deleteByMatch(id);
        matchSetMapper.deleteByMatch(id);
        vpMapper.deleteByMatch(id);
        announcementMapper.deleteAutoByMatch(id);
        matchMapper.deleteById(id);
    }

    public List<MatchSummaryVO> listMine() {
        List<Match> matches = UserContext.isAdmin()
                ? matchMapper.findAll() : matchMapper.findByCreator(UserContext.id());
        return queryService.summaries(matches);
    }

    public List<MatchSummaryVO> liveMatches() {
        return queryService.summaries(matchMapper.findByState(Match.ONGOING));
    }

    public List<MatchSummaryVO> finishedResults() {
        return queryService.summaries(matchMapper.findFinished(30));
    }

    private void finishMatch(Match match, Team left, Team right, int seq, int currentSet,
                             int leftSets, int rightSets, int leftPoints, int rightPoints) {
        Long winnerTeamId = leftSets > rightSets ? left.getId() : right.getId();
        LocalDateTime endTime = LocalDateTime.now();
        matchMapper.finish(match.getId(), winnerTeamId, endTime, currentSet,
                leftSets, rightSets, leftPoints, rightPoints, seq);
        match.setState(Match.FINISHED);
        match.setWinnerTeamId(winnerTeamId);
        match.setEndTime(endTime);
        match.setCurrentSet(currentSet);
        match.setLeftSets(leftSets);
        match.setRightSets(rightSets);
        match.setLeftPoints(leftPoints);
        match.setRightPoints(rightPoints);
        match.setEventSeq(seq);

        computeVp(match, left, right, winnerTeamId);
        List<Vp> vps = vpMapper.findByMatch(match.getId());
        Vp mvp = null;
        Vp svp = null;
        for (Vp vp : vps) {
            if (Vp.MVP.equals(vp.getType())) {
                mvp = vp;
            } else if (Vp.SVP.equals(vp.getType())) {
                svp = vp;
            }
        }
        Team winner = winnerTeamId.equals(left.getId()) ? left : right;
        Team loser = winnerTeamId.equals(left.getId()) ? right : left;
        announcementService.announceMatchEnd(match, winner, loser, mvp, svp, leftSets, rightSets);
    }

    private void computeVp(Match match, Team left, Team right, Long winnerTeamId) {
        vpMapper.deleteByMatch(match.getId());
        List<ScoreRecord> events = scoreRecordMapper.findByMatch(match.getId());
        List<Member> leftMembers = memberMapper.findByTeam(left.getId());
        List<Member> rightMembers = memberMapper.findByTeam(right.getId());
        for (Vp vp : vpCalculator.calculate(leftMembers, rightMembers, events,
                winnerTeamId, left.getId())) {
            vp.setMatchId(match.getId());
            vpMapper.insert(vp);
        }
    }

    private MatchDetailVO broadcast(Long id) {
        MatchDetailVO detail = queryService.detail(id);
        broadcaster.broadcast(id, "MATCH_UPDATE", detail);
        return detail;
    }

    private int setTarget(Match match, int currentSet) {
        return currentSet == match.getBestOf() ? match.getFinalSetPoints() : match.getPointsPerSet();
    }

    private String defaultRules(Match match) {
        int wins = match.getBestOf() / 2 + 1;
        return match.getBestOf() + "局" + wins + "胜 · " + match.getPointsPerSet()
                + "分制 · 决胜局" + match.getFinalSetPoints() + "分";
    }

    private Match requireMatch(Long id) {
        Match match = matchMapper.findById(id);
        if (match == null) {
            throw BusinessException.notFound("比赛不存在");
        }
        return match;
    }

    private Match requireOngoingOwned(Long id) {
        Match match = requireMatch(id);
        ensureCreator(match);
        if (!Match.ONGOING.equals(match.getState())) {
            throw new BusinessException("比赛不在进行中");
        }
        return match;
    }

    private void ensureCreator(Match match) {
        if (!match.getCreatorId().equals(UserContext.id())) {
            throw BusinessException.forbidden("只有创建该比赛的计分员才能操作");
        }
    }

    private Team requireTeam(Long teamId) {
        Team team = teamMapper.findById(teamId);
        if (team == null) {
            throw BusinessException.notFound("队伍不存在");
        }
        return team;
    }

    private void ensureLineup(Team team) {
        if (memberMapper.findByTeam(team.getId()).size() != 6) {
            throw new BusinessException("队伍「" + team.getName() + "」必须满 6 名队员才能参赛");
        }
    }

    private void ensureTeamFree(Team team) {
        if (matchMapper.countOngoingByTeam(team.getId()) > 0) {
            throw new BusinessException("队伍「" + team.getName() + "」正在参加进行中的比赛");
        }
    }
}
