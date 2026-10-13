package cn.edu.hnzj.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.edu.hnzj.pojo.Match;
import cn.edu.hnzj.pojo.Score;
import cn.edu.hnzj.service.ScoreService;

/**
 * @ClassName: ScoreController
 * @Description: 比分的控制层
 * @author: HHaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-03 05:50:49
 */
@Controller
@RequestMapping("/score")
public class ScoreController {
	/**
	 * @Fields score : 服务层实现类
	 * @date: 2022-05-03 05:56:39
	 */
	@Resource
	private ScoreService scoreService;

	/**
	 * @MethodName: AddScore
	 * @Description: 初始化比赛的分数，并将比赛传入到session内
	 * @param request 用于获取参数
	 * @return String 计分jsp
	 * @date: 2022-06-28 06:54:09
	 */
	@RequestMapping("addScores.do")
	public String AddScore(HttpServletRequest request) {
		// TODO 从request中获取添加比赛的比赛对象
		Match match = (Match) request.getAttribute("match");
		// 向数据库初始化得分对象
		if (!scoreService.addScoresByMatch(match))
			return "redirect:error";
		// TODO 对match对象补充Score对象信息
		match.setScores(scoreService.getScoreByMatchId(match.getId()));
		// TODO 把比赛信息添加到session内
		System.out.println(match);
		request.getSession().setAttribute("match", match);
		// TODO 跳转到记录页面
		return "redirect:/recording.do";
	}

	@RequestMapping("updateScores.do")
	@ResponseBody
	public List<Score> updateScores(@RequestBody List<Score> scores) {
		if (scoreService.updateScores(scores))
			return scores;
		return null;
	}

	/**
	 * @MethodName: getRecords
	 * @Description: 通过session的比赛id 返回比赛当前的比分
	 * @param request
	 * @return Match
	 * @date: 2022-06-21 10:56:52
	 */
	@RequestMapping(value = "/getScores.do")
	@ResponseBody
	public Match getScores(HttpServletRequest request) {
		Match match = (Match) request.getAttribute("match");
		if (match == null)// TODO 没有时 返回空
			return null;
		// TODO 获取得分对象，并设置得到match内
		match.setScores(scoreService.getScoreByMatchId(match.getId()));
		// TODO 添加至session内
		request.getSession().setAttribute("match", match);
		// TODO 将match返回到ajax
		return match;
	}
}
