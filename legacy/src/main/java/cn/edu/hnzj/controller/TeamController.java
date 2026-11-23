package cn.edu.hnzj.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.edu.hnzj.pojo.Match;
import cn.edu.hnzj.pojo.Team;
import cn.edu.hnzj.service.TeamService;

/**
 * @ClassName: TeamController
 * @Description: 队伍的控制层
 * @author: HHaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-03 05:50:49
 */
@Controller
@RequestMapping("/team")
public class TeamController {
	/**
	 * @Fields teamService : 服务层实现类
	 * @date: 2022-05-03 05:56:39
	 */
	@Resource
	private TeamService teamService;

	@RequestMapping(value = "/getAll.do")
	@ResponseBody
	public List<Team> getTeams() {
		return teamService.getTeamAll();
	}

	/**
	 * @MethodName: getTeamsById
	 * @Description: 获取所有的队伍信息
	 * @param list
	 * @param request
	 * @return List<Team>
	 * @date: 2023-05-04 04:24:35
	 */
	@RequestMapping("/getTeamsById.do")
	@ResponseBody
	public List<Team> getTeamsById(@RequestBody Integer[] tids) {
		int t1 = tids[0];
		int t2 = tids[1];
		List<Team> list = new ArrayList<Team>();
		list.add(teamService.getTeamById(t1));
		list.add(teamService.getTeamById(t2));
		return list;
	}

	/**
	 * @MethodName: updateTeams
	 * @Description: 修改team的比赛Id并向match内添加带有队员的队伍对象列表
	 * @return String
	 * @date: 2022-05-17 08:31:04
	 */
	@RequestMapping(value = "/updateTeams.do")
	public String updateTeams(HttpServletRequest request) {
		// TODO 从request中获取添加比赛的比赛对象
		Match match = (Match) request.getAttribute("match");
		if (match == null)// TODO 没有没有值报错
			return "redirect:/error.do";
		// TODO 更新队伍对象，并得到新的match对象(通过队名获取完整的队伍信息)
		Match updateMatch = teamService.updateTeamInfo(match);
		if (match != null)
			match = updateMatch;
		// TODO 把获取的新match重新加载进request中
		request.setAttribute("match", match);
		// TODO 转发给ScoreController处理
		return "forward:/score/addScores.do";
	}

	/**
	 * @MethodName: getTeamsByMatch
	 * @Description: 获取完整的Match对象（单击模场比赛进行观看的请求）
	 * @param request 请求对象 用于获取比赛 和 上传之session
	 * @return String 转发之scoreController 获取得分
	 * @date: 2022-06-28 08:17:48
	 */
	@RequestMapping(value = "/getTeamsByMatch.do")
	public String getTeamsByMatch(HttpServletRequest request) {
		Match match = (Match) request.getAttribute("match");
		// TODO 更新队伍对象，并得到新的match对象
		Match updateMatch = teamService.updateTeamsToMatch(match);
		if (match != null)
			match = updateMatch;
		System.out.println(match);
		// TODO 把获取的新match重新加载进request中
		request.setAttribute("match", match);
		// TODO 转发至score更新得分信息
		return "forward:/score/getScores.do";
	}

	@RequestMapping(value = "/updateMatchIdOfTeamToOneByMatchId.do")
	@ResponseBody
	public boolean updateMatchIdOfTeamToOneByMatchId(@RequestBody Integer matchId) {
		System.out.println(matchId);
		return teamService.updateMatchIdOfTeamToOneByMatchId(matchId);
	}
}
