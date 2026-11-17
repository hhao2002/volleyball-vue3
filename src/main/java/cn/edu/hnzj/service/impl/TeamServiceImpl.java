package cn.edu.hnzj.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.edu.hnzj.dao.MatchDao;
import cn.edu.hnzj.dao.TeamDao;
import cn.edu.hnzj.pojo.Match;
import cn.edu.hnzj.pojo.Team;
import cn.edu.hnzj.service.TeamService;

@Service(value = "teamService")
public class TeamServiceImpl implements TeamService {

	@Resource
	private TeamDao teamDao;
	@Resource
	private MatchDao matchDao;

	@Override
	public List<String> getTeamNameAll() {
		return teamDao.selectTeamNameAll();
	}

	@Override
	public List<Team> getTeamAll() {
		return teamDao.selectTeamAll();
	}

	@Override
	public Match updateTeamInfo(Match match) {
		if (match == null)// 判空
			return null;
		// TODO 获取队伍信息
		List<Team> teams = match.getTeams();
		// TODO 设置每个team的比赛id
		if (teams != null) {
			for (Team team : teams) {
				team.setMatchId(match.getId());
				// TODO 修改比赛id
				teamDao.updateTeamByName(team);
			}
		}
		return updateTeamsToMatch(match);
	}

	@Override
	public Match updateTeamsToMatch(Match match) {
		// TODO 通过比赛id获取完整的队伍信息
		match.setTeams(teamDao.selectTeamByMatchId(match.getId()));
		return match;
	}

	@Override
	public boolean updateMatchIdOfTeamToOneByMatchId(Integer matchId) {
		// TODO Auto-generated method stub

		return teamDao.updateMatchIdOfTeamToOneByMatchId(matchId) == 2;
	}

	/**
	 * @MethodName: getTeamById
	 * @Description: 通过队伍id返回队伍以及队员信息
	 * @param id
	 * @return
	 * @see cn.edu.hnzj.service.TeamService#getTeamById(int)
	 * @date: 2023-05-04 03:55:33
	 */
	@Override
	public Team getTeamById(int id) {
		return teamDao.selectTeamById(id);
	}

	@Override
	public List<Team> getTeamByIds(int matchId) {
		return teamDao.selectTeamByMatchIdForApi(matchId);
	}

	@Override
	public boolean releaseTeams(int matchId) {
		return teamDao.updateTeamMatchIdToFree(matchId) >= 0;
	}

	@Override
	public boolean bindTeamsToMatch(int matchId, List<Integer> teamIds) {
		if (teamIds == null || teamIds.size() < 2)
			return false;
		int row = 0;
		for (Integer teamId : teamIds) {
			Team team = new Team();
			team.setId(teamId.intValue());
			team.setMatchId(matchId);
			row += teamDao.updateTeamMatchIdById(team);
		}
		return row == 2;
	}
}
