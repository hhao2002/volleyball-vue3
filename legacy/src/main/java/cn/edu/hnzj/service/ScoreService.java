package cn.edu.hnzj.service;

import java.util.List;

import cn.edu.hnzj.pojo.Match;
import cn.edu.hnzj.pojo.Score;

/**
 * @ClassName: ScoreService
 * @Description: 比分的服务层
 * @author: HHaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-06-24 02:31:50
*/
public interface ScoreService {
	/**
	 * @MethodName: addScoresByMatch
	 * @Description: 获取比赛id 队伍id 添加两个初始得分记录
	 * @param match 比赛对象
	 * @return boolean 是否添加成功
	 * @date: 2022-06-28 07:01:47
	 */
	public boolean addScoresByMatch(Match match);
	/**
	 * @MethodName: updateScores
	 * @Description: 修改某局比赛的两个得分
	 * @param scores 更新的两个得分对象
	 * @return boolean 是否更新成功
	 * @date: 2022-06-29 01:23:32
	 */
	public boolean updateScores(List<Score> scores);
	/**
	 * @MethodName: getScoreByMatchId
	 * @Description: 查询莫场比赛的 得分
	 * @param matchId
	 * @return List<Score>
	 * @date: 2022-05-20 06:15:40
	 */
	public List<Score> getScoreByMatchId(int matchId);
}
