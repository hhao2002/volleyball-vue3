package cn.edu.hnzj.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.edu.hnzj.pojo.Match;
import cn.edu.hnzj.pojo.Member;
import cn.edu.hnzj.pojo.Score;
import cn.edu.hnzj.pojo.Team;
import cn.edu.hnzj.pojo.User;
import cn.edu.hnzj.service.MatchService;
import cn.edu.hnzj.service.ScoreService;
import cn.edu.hnzj.service.TeamService;
import cn.edu.hnzj.service.UserService;

/**
 * @ClassName: ApiController
 * @Description: 供 Vue 前端调用的纯 JSON 接口（不修改原有 JSP 相关逻辑）
 * @author: HHaoAn
 * @date: 2026-11-17
 */
@Controller
@RequestMapping("/api")
public class ApiController {

	@Resource
	private UserService userService;
	@Resource
	private MatchService matchService;
	@Resource
	private TeamService teamService;
	@Resource
	private ScoreService scoreService;

	private static final String SESSION_MATCH = "match";
	private static final String SESSION_TEAM_ORDER = "apiTeamOrder";

	/**
	 * @MethodName: login
	 * @Description: 登录，成功时把 user 放入 session 并返回用户信息
	 */
	@RequestMapping(value = "/login.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, Object> login(@RequestBody User body, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (body == null || isEmpty(body.getUsername()) || isEmpty(body.getPassword())) {
			result.put("success", false);
			result.put("msg", "用户名和密码不能为空");
			return result;
		}
		User user = new User();
		user.setUsername(body.getUsername().trim());
		user.setPassword(body.getPassword());
		try {
			if (userService.userlogin(user)) {
				request.getSession().setAttribute("user", user);
				result.put("success", true);
				result.put("msg", "登录成功");
				result.put("user", toUserMap(user));
			} else {
				result.put("success", false);
				result.put("msg", "用户名或密码错误");
			}
		} catch (Exception e) {
			result.put("success", false);
			result.put("msg", "登录异常：" + e.getMessage());
		}
		return result;
	}

	/**
	 * @MethodName: logout
	 * @Description: 退出登录，清理 session
	 */
	@RequestMapping(value = "/logout.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, Object> logout(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (request.getSession(false) != null)
			request.getSession(false).invalidate();
		result.put("success", true);
		return result;
	}

	/**
	 * @MethodName: currentUser
	 * @Description: 获取当前 session 中的登录用户
	 */
	@RequestMapping(value = "/currentUser.do", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, Object> currentUser(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		User user = (User) request.getSession().getAttribute("user");
		result.put("success", user != null);
		if (user != null)
			result.put("user", toUserMap(user));
		return result;
	}

	/**
	 * @MethodName: matches
	 * @Description: 比赛列表（附带两支队伍名称与比分）
	 */
	@RequestMapping(value = "/matches.do", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public List<Map<String, Object>> matches() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Match> matches = matchService.getMatchAll();
		if (matches == null)
			return list;
		for (Match match : matches) {
			if (match == null || match.getId() == null)
				continue;
			list.add(buildMatchView(match));
		}
		return list;
	}

	/**
	 * @MethodName: matchDetail
	 * @Description: 单场比赛详情（含队伍、队员、比分），并把 match 放入 session
	 */
	@RequestMapping(value = "/match.do", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, Object> matchDetail(@RequestParam("id") Integer id, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		Match match = matchService.getMatchById(id);
		if (match == null) {
			result.put("success", false);
			result.put("msg", "比赛不存在");
			return result;
		}
		List<Team> teams = teamService.getTeamByIds(match.getId());
		match.setTeams(teams);
		List<Score> scores = scoreService.getScoreByMatchId(match.getId());
		match.setScores(scores);
		request.getSession().setAttribute(SESSION_MATCH, match);
		request.getSession().setAttribute(SESSION_TEAM_ORDER, teamIds(teams, scores));
		result.put("success", true);
		result.put("match", buildDetailView(match, teams, scores));
		return result;
	}

	/**
	 * @MethodName: freeTeams
	 * @Description: 可用的空闲队伍（未关联比赛或关联到占位比赛）
	 */
	@RequestMapping(value = "/freeTeams.do", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public List<Map<String, Object>> freeTeams() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Team> teams = teamService.getTeamAll();
		if (teams == null)
			return list;
		for (Team team : teams) {
			if (team == null)
				continue;
			if (team.getMatchId() != 0 && team.getMatchId() != 1)
				continue;
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", team.getId());
			item.put("name", team.getName());
			item.put("coach", team.getCoach());
			list.add(item);
		}
		return list;
	}

	/**
	 * @MethodName: addMatch
	 * @Description: 新建比赛，复用原有 addMatch→updateTeams→addScores 的服务链路
	 */
	@RequestMapping(value = "/addMatch.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, Object> addMatch(@RequestParam("name") String name, @RequestParam("rules") String rules,
			@RequestParam("sponsor") String sponsor, @RequestParam("teamAId") Integer teamAId,
			@RequestParam("teamBId") Integer teamBId, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (isEmpty(name) || isEmpty(rules) || teamAId == null || teamBId == null) {
			result.put("success", false);
			result.put("msg", "比赛名称、规则、队伍为必填项");
			return result;
		}
		if (teamAId.equals(teamBId)) {
			result.put("success", false);
			result.put("msg", "两支队伍不能相同");
			return result;
		}
		Match match = new Match();
		match.setName(name.trim());
		match.setRules(rules.trim());
		match.setSponsor(isEmpty(sponsor) ? "" : sponsor.trim());
		match.setState("进行中");
		try {
			if (!matchService.addMatchByObject(match)) {
				result.put("success", false);
				result.put("msg", "比赛写入数据库失败");
				return result;
			}
			List<Integer> teamIds = new ArrayList<Integer>();
			teamIds.add(teamAId);
			teamIds.add(teamBId);
			// 按队伍 id 精确绑定，避免中文队名编码差异导致更新失败
			if (!teamService.bindTeamsToMatch(match.getId(), teamIds)) {
				result.put("success", false);
				result.put("msg", "队伍绑定失败，请删除该比赛后重试");
				result.put("matchId", match.getId());
				return result;
			}
			List<Team> teams = teamService.getTeamByIds(match.getId());
			match.setTeams(teams);
			if (!scoreService.addScoresByMatch(match)) {
				result.put("success", false);
				result.put("msg", "比分初始化失败，请删除该比赛后重试");
				result.put("matchId", match.getId());
				return result;
			}
			match.setScores(scoreService.getScoreByMatchId(match.getId()));
			request.getSession().setAttribute(SESSION_MATCH, match);
			request.getSession().setAttribute(SESSION_TEAM_ORDER, teamIds);
			result.put("success", true);
			result.put("msg", "比赛创建成功");
			result.put("matchId", match.getId());
		} catch (Exception e) {
			result.put("success", false);
			result.put("msg", "创建比赛异常：" + e.getMessage());
		}
		return result;
	}

	/**
	 * @MethodName: deleteMatch
	 * @Description: 删除比赛，并释放被该比赛占用的队伍
	 */
	@RequestMapping(value = "/deleteMatch.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, Object> deleteMatch(@RequestParam("id") Integer id, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (id == null || id.intValue() == 1) {
			result.put("success", false);
			result.put("msg", "该比赛不允许删除");
			return result;
		}
		try {
			List<Team> teams = teamService.getTeamByIds(id);
			scoreService.deleteScoresByMatchId(id);
			teamService.releaseTeams(id);
			matchService.deleteMatchById(id);
			if (teams != null) {
				for (Team team : teams) {
					System.out.println("释放队伍：" + team.getName());
				}
			}
			Object sessionMatch = request.getSession().getAttribute(SESSION_MATCH);
			if (sessionMatch instanceof Match && id.equals(((Match) sessionMatch).getId()))
				request.getSession().removeAttribute(SESSION_MATCH);
			result.put("success", true);
			result.put("msg", "比赛已删除");
		} catch (Exception e) {
			result.put("success", false);
			result.put("msg", "删除异常：" + e.getMessage());
		}
		return result;
	}

	/**
	 * @MethodName: updateScores
	 * @Description: 保存两队比分（两级赛制：大比分 dScore 为胜局数，小比分 xScore 为当前局分）
	 */
	@RequestMapping(value = "/updateScores.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, Object> updateScores(@RequestBody Map<String, Object> body) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (body == null || body.get("matchId") == null) {
			result.put("success", false);
			result.put("msg", "缺少比赛 id");
			return result;
		}
		Integer matchId = toInt(body.get("matchId"));
		Match match = matchService.getMatchById(matchId);
		if (match == null) {
			result.put("success", false);
			result.put("msg", "比赛不存在");
			return result;
		}
		List<Team> teams = teamService.getTeamByIds(matchId);
		if (teams == null || teams.size() < 2) {
			result.put("success", false);
			result.put("msg", "该比赛缺少队伍信息");
			return result;
		}
		List<Score> existing = scoreService.getScoreByMatchId(matchId);
		Integer state = toInt(body.get("state"));
		Integer inning = toInt(body.get("inning"));
		Integer scoreA = toInt(body.get("scoreA"));
		Integer scoreB = toInt(body.get("scoreB"));
		Integer setsA = toInt(body.get("setsA"));
		Integer setsB = toInt(body.get("setsB"));
		Integer leftId = toInt(body.get("leftTeamId"));
		List<Score> payload = new ArrayList<Score>();
		for (Team team : teams) {
			Score score = new Score();
			score.setMatchsId(matchId);
			score.setTeamId(team.getId());
			Score old = findScore(existing, team.getId());
			if (old != null)
				score.setId(old.getId());
			if (team.getId() == (leftId == null ? teams.get(0).getId() : leftId.intValue())) {
				score.setdScore(setsA == null ? 0 : setsA);
				score.setxScore(scoreA == null ? 0 : scoreA);
			} else {
				score.setdScore(setsB == null ? 0 : setsB);
				score.setxScore(scoreB == null ? 0 : scoreB);
			}
			payload.add(score);
		}
		boolean saved = false;
		try {
			if (existing != null && existing.size() >= 2) {
				saved = scoreService.updateScores(payload);
				if (!saved) {
					for (Score score : payload)
						scoreService.updateScoreById(score);
					saved = true;
				}
			} else {
				for (Score score : payload)
					scoreService.saveScore(score);
				saved = true;
			}
		} catch (Exception e) {
			saved = false;
			result.put("msg", "保存比分异常：" + e.getMessage());
		}
		result.put("success", saved);
		if (!result.containsKey("msg"))
			result.put("msg", saved ? "比分已保存" : "比分保存失败");
		List<Score> latest = scoreService.getScoreByMatchId(matchId);
		result.put("scores", buildScoreView(latest));
		if (state != null) {
			result.put("state", match.getState());
		}
		if (inning != null)
			result.put("inning", inning);
		return result;
	}

	/**
	 * @MethodName: finishMatch
	 * @Description: 结束比赛并释放队伍，返回 MVP 信息
	 */
	@RequestMapping(value = "/finishMatch.do", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, Object> finishMatch(@RequestParam("id") Integer id, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		Match match = matchService.getMatchById(id);
		if (match == null) {
			result.put("success", false);
			result.put("msg", "比赛不存在");
			return result;
		}
		try {
			matchService.updateMatchStateById(id, "结束");
			teamService.releaseTeams(id);
			Object sessionMatch = request.getSession().getAttribute(SESSION_MATCH);
			if (sessionMatch instanceof Match && id.equals(((Match) sessionMatch).getId()))
				((Match) sessionMatch).setState("结束");
			result.put("success", true);
			result.put("msg", "比赛已结束");
			Member mvp = matchService.getMvp(id);
			if (mvp != null) {
				Map<String, Object> mp = new HashMap<String, Object>();
				mp.put("id", mvp.getId());
				mp.put("name", mvp.getName());
				mp.put("position", mvp.getPosition());
				mp.put("teamId", mvp.getTeamId());
				result.put("mvp", mp);
			}
		} catch (Exception e) {
			result.put("success", false);
			result.put("msg", "结束比赛异常：" + e.getMessage());
		}
		return result;
	}

	/* ============================ 私有工具方法 ============================ */

	private Map<String, Object> buildMatchView(Match match) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("id", match.getId());
		item.put("name", match.getName());
		item.put("rules", match.getRules());
		item.put("startTime", match.getStartTime());
		item.put("endTime", match.getEndTime());
		item.put("sponsor", match.getSponsor());
		item.put("state", match.getState());
		List<Score> scores = scoreService.getScoreByMatchId(match.getId() == null ? 0 : match.getId());
		List<Team> teams = teamService.getTeamByIds(match.getId() == null ? 0 : match.getId());
		item.put("teams", buildTeamNames(teams));
		item.put("scores", buildScoreView(scores));
		return item;
	}

	private Map<String, Object> buildDetailView(Match match, List<Team> teams, List<Score> scores) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("id", match.getId());
		item.put("name", match.getName());
		item.put("rules", match.getRules());
		item.put("startTime", match.getStartTime());
		item.put("endTime", match.getEndTime());
		item.put("sponsor", match.getSponsor());
		item.put("state", match.getState());
		List<Map<String, Object>> teamViews = new ArrayList<Map<String, Object>>();
		if (teams != null) {
			for (Team team : teams) {
				Map<String, Object> tv = new HashMap<String, Object>();
				tv.put("id", team.getId());
				tv.put("name", team.getName());
				tv.put("coach", team.getCoach());
				List<Map<String, Object>> members = new ArrayList<Map<String, Object>>();
				if (team.getMembers() != null) {
					for (Member member : team.getMembers()) {
						Map<String, Object> mv = new HashMap<String, Object>();
						mv.put("id", member.getId());
						mv.put("name", member.getName());
						mv.put("position", member.getPosition());
						members.add(mv);
					}
				}
				tv.put("members", members);
				teamViews.add(tv);
			}
		}
		item.put("teams", teamViews);
		item.put("scores", buildScoreView(scores));
		return item;
	}

	private List<Map<String, Object>> buildTeamNames(List<Team> teams) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (teams == null)
			return list;
		for (Team team : teams) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", team.getId());
			item.put("name", team.getName());
			list.add(item);
		}
		return list;
	}

	private List<Map<String, Object>> buildScoreView(List<Score> scores) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (scores == null)
			return list;
		for (Score score : scores) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", score.getId());
			item.put("matchsId", score.getMatchsId());
			item.put("teamId", score.getTeamId());
			item.put("dScore", score.getdScore() == null ? 0 : score.getdScore());
			item.put("xScore", score.getxScore() == null ? 0 : score.getxScore());
			list.add(item);
		}
		return list;
	}

	private Map<String, Object> toUserMap(User user) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", user.getId());
		map.put("username", user.getUsername());
		map.put("email", user.getEmail());
		map.put("role", user.getRole());
		return map;
	}

	private List<Integer> teamIds(List<Team> teams, List<Score> scores) {
		List<Integer> ids = new ArrayList<Integer>();
		if (teams != null) {
			for (Team team : teams)
				ids.add(team.getId());
		}
		if (ids.size() < 2 && scores != null) {
			for (Score score : scores) {
				if (!ids.contains(score.getTeamId()))
					ids.add(score.getTeamId());
			}
		}
		return ids;
	}

	private Score findScore(List<Score> scores, int teamId) {
		if (scores == null)
			return null;
		for (Score score : scores) {
			if (score.getTeamId() != null && score.getTeamId().intValue() == teamId)
				return score;
		}
		return null;
	}

	private Integer toInt(Object value) {
		if (value == null)
			return null;
		if (value instanceof Number)
			return Integer.valueOf(((Number) value).intValue());
		try {
			return Integer.valueOf(String.valueOf(value).trim());
		} catch (Exception e) {
			return null;
		}
	}

	private boolean isEmpty(String value) {
		return value == null || value.trim().length() == 0;
	}
}