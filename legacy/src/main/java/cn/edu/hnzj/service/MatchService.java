package cn.edu.hnzj.service;

import java.util.List;

import cn.edu.hnzj.pojo.Match;
import cn.edu.hnzj.pojo.Member;

/**
 * @ClassName: MatchService
 * @Description: volleyball.match表 的 service层
 * @author: HhaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-14 06:04:40
 */
public interface MatchService {
	/**
	 * @MethodName: insertMatchByObject
	 * @Description: 比赛一条记录 并添加一个初始比分
	 * @param match 记录对象
	 * @return boolean 是否成功
	 * @date: 2022-05-03 04:46:06
	 */
	public boolean addMatchByObject(Match match);
	
	/**
	 * @MethodName: getMatchById
	 * @Description: 通过比赛id获取一场比赛
	 * @param id 比赛id
	 * @return Match 获取到的比赛
	 * @date: 2022-06-28 07:41:29
	 */
	public Match getMatchById(int id);
	
	/**
	 * @MethodName: deleteMatchById
	 * @Description: 通过比赛id刪除一场比赛
	 * @param id 比赛id
	 * @return int 为1表示删除成功
	 * @date: 2022-06-29 10:41:16
	 */
	public int deleteMatchById(int id);

	/**
	 * @MethodName: getMatchAll
	 * @Description: 获取所有的比赛
	 * @return List<Match>
	 * @date: 2022-05-14 06:05:42
	 */
	public List<Match> getMatchAll();
	
	/**
	 * @MethodName: getMvp
	 * @Description: 获得某次比赛的mvp
	 * @param matchId
	 * @return Member
	 * @date: 2022-06-22 09:49:23
	 */
	public Member getMvp(int matchId);
	
	/**
	 * @MethodName: updateMatchStateById
	 * @Description: 修改比赛状态为结束
	 * @param id 比赛id
	 * @return int
	 * @date: 2022-06-23 06:31:36
	 */
	public int updateMatchStateById(int id,String state);
	
}
