package cn.edu.hnzj.volleyball.service;

import cn.edu.hnzj.volleyball.common.BusinessException;
import cn.edu.hnzj.volleyball.dto.MemberRequest;
import cn.edu.hnzj.volleyball.dto.TeamSaveRequest;
import cn.edu.hnzj.volleyball.entity.Member;
import cn.edu.hnzj.volleyball.entity.Team;
import cn.edu.hnzj.volleyball.mapper.MatchMapper;
import cn.edu.hnzj.volleyball.mapper.MemberMapper;
import cn.edu.hnzj.volleyball.mapper.TeamMapper;
import cn.edu.hnzj.volleyball.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TeamService {

    private final TeamMapper teamMapper;
    private final MemberMapper memberMapper;
    private final MatchMapper matchMapper;

    public TeamService(TeamMapper teamMapper, MemberMapper memberMapper, MatchMapper matchMapper) {
        this.teamMapper = teamMapper;
        this.memberMapper = memberMapper;
        this.matchMapper = matchMapper;
    }

    public List<Team> listAll() {
        List<Team> teams = teamMapper.findAll();
        for (Team team : teams) {
            team.setMembers(memberMapper.findByTeam(team.getId()));
            team.setLocked(matchMapper.countOngoingByTeam(team.getId()) > 0);
        }
        return teams;
    }

    public Team detail(Long id) {
        Team team = teamMapper.findById(id);
        if (team == null) {
            throw BusinessException.notFound("队伍不存在");
        }
        team.setMembers(memberMapper.findByTeam(id));
        team.setLocked(matchMapper.countOngoingByTeam(id) > 0);
        return team;
    }

    public List<Member> availableMembers() {
        return memberMapper.findAvailable();
    }

    public List<Member> allMembers() {
        return memberMapper.findAll();
    }

    @Transactional
    public Team create(TeamSaveRequest request) {
        validateTeamName(request.getName(), null);
        List<Member> members = validateMembers(request.getMemberIds(), null);

        Team team = new Team();
        team.setName(request.getName().trim());
        team.setCoach(request.getCoach());
        team.setColor(normalizeColor(request.getColor()));
        team.setCreatedBy(UserContext.id());
        teamMapper.insert(team);

        assignMembers(team.getId(), members);
        return detail(team.getId());
    }

    @Transactional
    public Team update(Long id, TeamSaveRequest request) {
        Team team = teamMapper.findById(id);
        if (team == null) {
            throw BusinessException.notFound("队伍不存在");
        }
        ensureUnlocked(id, "该队伍正在参加进行中的比赛，无法修改");
        validateTeamName(request.getName(), id);
        List<Member> members = validateMembers(request.getMemberIds(), id);

        team.setName(request.getName().trim());
        team.setCoach(request.getCoach());
        team.setColor(normalizeColor(request.getColor()));
        teamMapper.update(team);

        memberMapper.releaseTeam(id);
        assignMembers(id, members);
        return detail(id);
    }

    @Transactional
    public void delete(Long id) {
        Team team = teamMapper.findById(id);
        if (team == null) {
            throw BusinessException.notFound("队伍不存在");
        }
        ensureUnlocked(id, "该队伍正在参加进行中的比赛，无法删除");
        if (teamMapper.countMatches(id) > 0) {
            throw new BusinessException("该队伍已有比赛记录，无法删除");
        }
        memberMapper.releaseTeam(id);
        teamMapper.deleteById(id);
    }

    @Transactional
    public Member createMember(MemberRequest request) {
        Member member = new Member();
        member.setName(request.getName().trim());
        member.setRole(StringUtils.hasText(request.getRole()) ? request.getRole() : "主攻");
        member.setNumber(request.getNumber());
        memberMapper.insert(member);
        return memberMapper.findById(member.getId());
    }

    @Transactional
    public Member updateMember(Long id, MemberRequest request) {
        Member member = memberMapper.findById(id);
        if (member == null) {
            throw BusinessException.notFound("队员不存在");
        }
        if (member.getTeamId() != null) {
            ensureUnlocked(member.getTeamId(), "该队员所在队伍正在比赛中，无法修改");
        }
        member.setName(request.getName().trim());
        member.setRole(StringUtils.hasText(request.getRole()) ? request.getRole() : "主攻");
        member.setNumber(request.getNumber());
        memberMapper.update(member);
        return memberMapper.findById(id);
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = memberMapper.findById(id);
        if (member == null) {
            throw BusinessException.notFound("队员不存在");
        }
        if (member.getTeamId() != null) {
            ensureUnlocked(member.getTeamId(), "该队员所在队伍正在比赛中，无法删除");
        }
        if (memberMapper.countReferences(id) > 0) {
            throw new BusinessException("该队员已有比赛记录，无法删除");
        }
        memberMapper.deleteById(id);
    }

    private void validateTeamName(String name, Long excludeId) {
        if (!StringUtils.hasText(name)) {
            throw new BusinessException("请输入队伍名称");
        }
        if (teamMapper.countByName(name.trim(), excludeId) > 0) {
            throw new BusinessException("队伍名称已存在");
        }
    }

    private List<Member> validateMembers(List<Long> memberIds, Long teamId) {
        Set<Long> unique = new HashSet<>(memberIds);
        if (unique.size() != 6) {
            throw new BusinessException("队伍必须有 6 名不重复的队员");
        }
        List<Member> members = memberMapper.findByIds(new ArrayList<>(unique));
        if (members.size() != 6) {
            throw new BusinessException("部分队员不存在，请刷新后重试");
        }
        for (Member member : members) {
            if (member.getTeamId() != null && !member.getTeamId().equals(teamId)) {
                throw new BusinessException("队员「" + member.getName() + "」已被其他队伍占用");
            }
        }
        return members;
    }

    private void assignMembers(Long teamId, List<Member> members) {
        for (int i = 0; i < members.size(); i++) {
            memberMapper.assign(members.get(i).getId(), teamId, i + 1);
        }
    }

    private void ensureUnlocked(Long teamId, String message) {
        if (matchMapper.countOngoingByTeam(teamId) > 0) {
            throw new BusinessException(message);
        }
    }

    private String normalizeColor(String color) {
        if (StringUtils.hasText(color) && color.matches("^#[0-9a-fA-F]{6}$")) {
            return color;
        }
        return "#FF6B2C";
    }
}
