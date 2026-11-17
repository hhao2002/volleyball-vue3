package cn.edu.hnzj.service;

import java.util.List;

import cn.edu.hnzj.pojo.Match;
import cn.edu.hnzj.pojo.Team;

/**
 * @ClassName: TeamService
 * @Description: 队伍的服务层
 * @author: HhaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-15 07:39:29
 */
public interface TeamService {

	/**
	 * 
	 * @MethodName: getAllTeamName
	 * @Description: TODO
	 * @return List<String>
	 * @date: 2022-05-15 07:47:10
	 */
	public List<String> getTeamNameAll();

	/**
	 * @MethodName: getTeamById
	 * @Description: 通过id获取队伍以及队员
	 * @param id
	 * @return Team
	 * @date: 2023-05-04 11:32:44
	 */
	public Team getTeamById(int id);

	/**
	 * @MethodName: getTeamAll
	 * @Description: 获取所有的队伍
	 * @return List<Team>
	 * @date: 2022-05-16 08:36:04
	 */
	public List<Team> getTeamAll();

	/**
	 * @MethodName: updateTeamInfo
	 * @Description: 更新队伍的比赛id，并得完整的match对象
	 * @param match
	 * @return boolean
	 * @date: 2022-05-16 09:28:28
	 */
	public Match updateTeamInfo(Match match);

	/**
	 * @MethodName: updateTeamsToMatch
	 * @Description: 把完整的队伍信息更新到比赛对象内
	 * @param match
	 * @return Match
	 * @date: 2022-06-28 07:53:29
	 */
	public Match updateTeamsToMatch(Match match);

	/**
	 * @MethodName: updateMatchIdOfTeamToOneByMatchId
	 * @Description: 修改队伍中指定参与比赛id为1
	 * @param matchId
	 * @return int
	 * @date: 2022-06-24 10:48:14
	 */
	public boolean updateMatchIdOfTeamToOneByMatchId(Integer matchId);

	/**
	 * @MethodName: getTeamByIds
	 * @Description: 通过比赛id获取完整的队伍（含队员）信息
	 * @param matchId 比赛id
	 * @return List<Team>
	 * @date: 2026-11-17
	 */
	public List<Team> getTeamByIds(int matchId);

	/**
	 * @MethodName: releaseTeams
	 * @Description: 释放某场比赛占用的队伍（置为 1 号占位比赛）
	 * @param matchId 比赛id
	 * @return boolean
	 * @date: 2026-11-17
	 */
	public boolean releaseTeams(int matchId);

	/**
	 * @MethodName: bindTeamsToMatch
	 * @Description: 通过队伍id把两支队伍绑定到比赛上
	 * @param matchId 比赛id
	 * @param teamIds 队伍id列表
	 * @return boolean
	 * @date: 2026-11-17
	 */
	public boolean bindTeamsToMatch(int matchId, List<Integer> teamIds);
}
