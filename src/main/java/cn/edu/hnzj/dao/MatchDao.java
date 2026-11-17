package cn.edu.hnzj.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.edu.hnzj.pojo.Match;

/**
 * @ClassName: matchDao
 * @Description: volleyball.match表 的 dao层
 * @author: HhaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-14 05:20:26
 */
@Repository
public interface MatchDao {
	/**
	 * @MethodName: insertMatchByObject
	 * @Description: 通过match对象 向 volleyball.match表 添加一条记录
	 * @param match 记录对象
	 * @return int 影响行数
	 * @date: 2022-05-03 04:46:06
	 */
	public int insertMatchByObject(Match match);
	
	/**
	 * @MethodName: deleteMatchById
	 * @Description: 通过id 向 volleyball.match表 删除一条记录
	 * @param id 比赛id
	 * @return int 1为删除成功
	 * @date: 2022-05-13 04:12:58
	 */
	public int deleteMatchById(int id);
	
	/**
	 * @MethodName: selectMatchById
	 * @Description: 通过id 向 volleyball.match表 查询一个比赛对象
	 * @param id
	 * @return Match
	 * @date: 2022-06-24 03:22:44
	 */
	public Match selectMatchById(int id);
	
	/**
	 * @MethodName: selectMatchAll
	 * @Description: 查询volleyball.match表 的所有比赛
	 * @return List<Match>
	 * @date: 2022-05-14 05:25:43
	 */
	public List<Match> selectMatchAll();
	
	/**
	 * @MethodName: getMembersBymatchId
	 * @Description: 获得指定比赛的所有参赛人的id
	 * @param matchId
	 * @return int[] 比赛id
	 * @date: 2022-06-22 02:19:58
	 */
	public int[] getMembersBymatchId(int matchId);
	
	/**
	 * @MethodName: updateMatchStateById
	 * @Description: 修改指定比赛id的比赛的状态为指定状态
	 * @param id 比赛id
	 * @param state 比赛状态
	 * @return int
	 * @date: 2022-06-24 04:09:47
	 */
	public int updateMatchStateById(@Param("id")int id,@Param("state") String state);
}
