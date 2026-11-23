package cn.edu.hnzj.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.edu.hnzj.pojo.Match;
import cn.edu.hnzj.pojo.Member;
import cn.edu.hnzj.service.MatchService;

/**
 * @ClassName: MatchController
 * @Description: 比赛的控制层
 * @author: HhaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-03 05:50:49
 */
@Controller
@RequestMapping("/match")
public class MatchController {
	/**
	 * @Fields service : 服务层实现类
	 * @date: 2022-05-03 05:56:39
	 */
	@Resource
	private MatchService matchService;

	/**
	 * @MethodName: addMatch
	 * @Description: 添加比赛到数据库
	 * @param match
	 * @param request
	 * @return String
	 * @date: 2022-06-28 08:20:14
	 */
	@RequestMapping(value = "/addMatch.do")
	public String addMatch(Match match, HttpServletRequest request) {
		// TODO 添加match到数据库
		if (!matchService.addMatchByObject(match))
			return "redirect:error";
		// TODO match添加到request内
		request.setAttribute("match", match);
		// TODO 转发给TeamController处理
		return "forward:/team/updateTeams.do";
	}

	/**
	 * @MethodName: deleteMatch
	 * @Description: 刪除数据库的比赛
	 * @param match
	 * @param request
	 * @return String
	 * @date: 2022-06-29 21:35:43
	 */
	@RequestMapping(value = "/deleteMatch.do")
	public String deleteMatch(@RequestBody Match match, HttpSession session) {
		if (match.getId() == null) {
			return "redirect:matchs.do";
		} else {
			matchService.deleteMatchById(match.getId());
			return "redirect:matchs.do";
		}
	}

	/**
	 * @MethodName: getMatch
	 * @Description: 获取比赛信息存入请求对象
	 * @param match   比赛对象
	 * @param request 请求对象
	 * @return String 跳转 队伍控制层 错误
	 * @date: 2022-06-28 08:20:30
	 */
	@RequestMapping(value = "/getMatch.do")
	public String getMatch(@RequestBody Match match, HttpServletRequest request) {
		match = matchService.getMatchById(match.getId());
		if (match == null)
			return "redirect:error";
		// TODO match添加到request内
		request.setAttribute("match", match);
		// TODO 转发给TeamController处理
		return "forward:/team/getTeamsByMatch.do";
	}

	@RequestMapping(value = "/getAll.do")
	@ResponseBody
	public List<Match> getMatch() {
		return matchService.getMatchAll();
	}

	@RequestMapping("/getMvp.do")
	@ResponseBody
	public Member getMvp(int matchId) {
		return matchService.getMvp(matchId);
	}

	@RequestMapping("/updateMatchStateById.do")
	@ResponseBody
	public int updateMatchStateById(@RequestBody Match match, HttpSession session) {
		System.out.println("updateMatchStateById执行了");
		// TODO 修改session的状态为结束
		Match attr = (Match) session.getAttribute("match");
		attr.setState(match.getState());
		System.out.println((Match) session.getAttribute("match"));
		// TODO 修改数据库的状态为结束
		return matchService.updateMatchStateById(match.getId(), match.getState());
	}

}
