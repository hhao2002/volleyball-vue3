package cn.edu.hnzj.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.edu.hnzj.dao.ScoreDao;
import cn.edu.hnzj.pojo.Match;
import cn.edu.hnzj.pojo.Score;
import cn.edu.hnzj.pojo.Team;
import cn.edu.hnzj.service.ScoreService;

@Service("scoreService")
public class ScoreServiceImpl implements ScoreService {
	/**
	 * @Fields scoreDao : 得分数据层
	 * @date: 2022-06-28 07:03:51
	 */
	@Resource
	private ScoreDao scoreDao;
	/**
	 * @Fields score : 得分对象
	 * @date: 2022-06-28 06:58:02
	 */
	@Resource
	private Score score;

	public void setScoreDao(ScoreDao scoreDao) {
		this.scoreDao = scoreDao;
	}

	@Override
	public List<Score> getScoreByMatchId(int matchId) {
		// TODO Auto-generated method stub
		return scoreDao.selectScoreByMatchsId(matchId);
	}

	@Override
	public boolean addScoresByMatch(Match match) {
		List<Team> teams = match.getTeams();
		if (teams == null)// TODO 判断空
			return false;
		// TODO 设置比赛id
		score.setMatchsId(match.getId());
		int row = 0;// 用户判断
		for (Team team : teams) {
			// TODO 设置队伍id
			score.setTeamId(team.getId());
			// TODO 将得分对象添加到数据库
			row += scoreDao.insertScoreByObject(score);
			// TODO id自增
			score.setId(score.getId() + 1);
		}
		return row == 2;
	}

	@Override
	public boolean updateScores(List<Score> scores) {
		// TODO 遍历并逐一修改
		int row = 0;
		for (Score score : scores)
			row += scoreDao.updateScoreByMIdTId(score);
		System.out.println(scores);
		return row == 2;
	}
}
