package cn.edu.hnzj.volleyball.service;

import cn.edu.hnzj.volleyball.common.BusinessException;
import cn.edu.hnzj.volleyball.dto.MatchDetailVO;
import cn.edu.hnzj.volleyball.dto.MatchSummaryVO;
import cn.edu.hnzj.volleyball.entity.Match;
import cn.edu.hnzj.volleyball.entity.Member;
import cn.edu.hnzj.volleyball.entity.Team;
import cn.edu.hnzj.volleyball.entity.User;
import cn.edu.hnzj.volleyball.entity.Vp;
import cn.edu.hnzj.volleyball.mapper.MatchMapper;
import cn.edu.hnzj.volleyball.mapper.MatchSetMapper;
import cn.edu.hnzj.volleyball.mapper.MemberMapper;
import cn.edu.hnzj.volleyball.mapper.ScoreRecordMapper;
import cn.edu.hnzj.volleyball.mapper.TeamMapper;
import cn.edu.hnzj.volleyball.mapper.UserMapper;
import cn.edu.hnzj.volleyball.mapper.VpMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MatchQueryService {

    private final MatchMapper matchMapper;
    private final MatchSetMapper matchSetMapper;
    private final ScoreRecordMapper scoreRecordMapper;
    private final VpMapper vpMapper;
    private final TeamMapper teamMapper;
    private final MemberMapper memberMapper;
    private final UserMapper userMapper;

    public MatchQueryService(MatchMapper matchMapper, MatchSetMapper matchSetMapper,
                             ScoreRecordMapper scoreRecordMapper, VpMapper vpMapper,
                             TeamMapper teamMapper, MemberMapper memberMapper, UserMapper userMapper) {
        this.matchMapper = matchMapper;
        this.matchSetMapper = matchSetMapper;
        this.scoreRecordMapper = scoreRecordMapper;
        this.vpMapper = vpMapper;
        this.teamMapper = teamMapper;
        this.memberMapper = memberMapper;
        this.userMapper = userMapper;
    }

    public MatchDetailVO detail(Long matchId) {
        Match match = matchMapper.findById(matchId);
        if (match == null) {
            throw BusinessException.notFound("比赛不存在");
        }
        MatchDetailVO vo = new MatchDetailVO();
        vo.setMatch(match);
        vo.setLeftTeam(loadTeam(match.getLeftTeamId()));
        vo.setRightTeam(loadTeam(match.getRightTeamId()));
        vo.setSets(matchSetMapper.findByMatch(matchId));
        vo.setEvents(scoreRecordMapper.findByMatch(matchId));
        List<Vp> vps = vpMapper.findByMatch(matchId);
        vo.setVps(vps);
        for (Vp vp : vps) {
            if (Vp.MVP.equals(vp.getType())) {
                vo.setMvpName(vp.getMemberName());
            } else if (Vp.SVP.equals(vp.getType())) {
                vo.setSvpName(vp.getMemberName());
            }
        }
        User creator = userMapper.findById(match.getCreatorId());
        vo.setCreatorName(creator == null ? "未知" : creator.getNickname());
        return vo;
    }

    public List<MatchSummaryVO> summaries(List<Match> matches) {
        if (matches == null || matches.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> teamIds = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        for (Match m : matches) {
            addIfAbsent(teamIds, m.getLeftTeamId());
            addIfAbsent(teamIds, m.getRightTeamId());
            addIfAbsent(teamIds, m.getWinnerTeamId());
            addIfAbsent(userIds, m.getCreatorId());
        }
        Map<Long, Team> teamMap = new HashMap<>();
        if (!teamIds.isEmpty()) {
            for (Team team : teamMapper.findByIds(teamIds)) {
                teamMap.put(team.getId(), team);
            }
        }
        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            for (User user : userMapper.findByIds(userIds)) {
                userMap.put(user.getId(), user);
            }
        }

        List<MatchSummaryVO> result = new ArrayList<>();
        for (Match m : matches) {
            MatchSummaryVO vo = new MatchSummaryVO();
            BeanUtils.copyProperties(m, vo);
            Team left = teamMap.get(m.getLeftTeamId());
            Team right = teamMap.get(m.getRightTeamId());
            Team winner = m.getWinnerTeamId() == null ? null : teamMap.get(m.getWinnerTeamId());
            User creator = userMap.get(m.getCreatorId());
            vo.setLeftTeamName(left == null ? "未知队伍" : left.getName());
            vo.setRightTeamName(right == null ? "未知队伍" : right.getName());
            vo.setWinnerTeamName(winner == null ? null : winner.getName());
            vo.setCreatorName(creator == null ? "未知" : creator.getNickname());
            for (Vp vp : vpMapper.findByMatch(m.getId())) {
                if (Vp.MVP.equals(vp.getType())) {
                    vo.setMvpName(vp.getMemberName());
                } else if (Vp.SVP.equals(vp.getType())) {
                    vo.setSvpName(vp.getMemberName());
                }
            }
            result.add(vo);
        }
        return result;
    }

    public MatchSummaryVO summary(Long matchId) {
        Match match = matchMapper.findById(matchId);
        if (match == null) {
            throw BusinessException.notFound("比赛不存在");
        }
        List<Match> one = new ArrayList<>();
        one.add(match);
        return summaries(one).get(0);
    }

    private Team loadTeam(Long teamId) {
        Team team = teamMapper.findById(teamId);
        if (team == null) {
            return null;
        }
        List<Member> members = memberMapper.findByTeam(teamId);
        team.setMembers(members);
        return team;
    }

    private void addIfAbsent(List<Long> list, Long value) {
        if (value != null && !list.contains(value)) {
            list.add(value);
        }
    }
}
