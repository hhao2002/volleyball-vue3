package cn.edu.hnzj.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.edu.hnzj.pojo.Score;

/**
 * @ClassName: ScoreDao
 * @Description: volleyball.score表 的 dao层
 * @author: HhaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-06-23 09:20:26
 */
@Repository
public interface ScoreDao {

	/**
	 * @MethodName: insertScoreByObject
	 * @Description: 传入Score对象 向 volleyball.score表 添加一条记录
	 * @param score 添加得分的信息
	 * @return int 添加得分的数量
	 * @date: 2022-06-23 09:18:26
	 */
	public int insertScoreByObject(Score score);
	
	/**
	 * @MethodName: deleteScoreById
	 * @Description: 传入id 向volleyball.score 删除一条记录
	 * @param matchsId 删除的id
	 * @return int 删除的数量
	 * @date: 2022-06-23 09:31:21
	 */
	public int deleteScoreById(int id);

	/**
	 * @MethodName: deleteScoreByMatchsId
	 * @Description: 传入比赛id 向volleyball.score 删除两条记录
	 * @param matchsId 删除的比赛id
	 * @return int 删除的数量
	 * @date: 2022-06-23 09:27:44
	 */
	public int deleteScoreByMatchsId(int matchsId);

	/**
	 * @MethodName: updateScoreByObject
	 * @Description: 传入Score对象 向 volleyball.score表 通过id更新一条记录
	 * @param Score 修改的得分对象
	 * @return int 修改匹配到的行数
	 * @date: 2022-06-23 09:18:26
	 */
	public int updateScoreByObject(Score score);

	/**
	 * @MethodName: updateScoreByObject
	 * @Description: 传入Score对象 向 volleyball.score表 通过比赛id和队伍id更新得分
	 * @param Score 修改的得分对象
	 * @return int 修改匹配到的行数
	 * @date: 2022-06-23 09:18:26
	 */
	public int updateScoreByMIdTId(Score score);

	/**
	 * @MethodName: selectScoreById
	 * @Description: 传入id 向 volleyball.score 表 查询一条记录
	 * @param id 得分id
	 * @return Score 得分信息
	 * @date: 2022-06-27 01:41:37
	 */
	public Score selectScoreById(int id);

	/**
	 * @MethodName: selectScoreByMatchsId
	 * @Description: 传入比赛id 向 volleyball.score表 查询两队伍的得分
	 * @param matchsId 要查询的比赛id
	 * @return List<Score> 两个队伍的得分信息
	 * @date: 2022-06-23 09:18:26
	 */
	public List<Score> selectScoreByMatchsId(int matchsId);

	/**
	 * @MethodName: selectScoreByMatchsId
	 * @Description: 向 volleyball.score表 查询所有的得分
	 * @return List<Score> 所有得分
	 * @date: 2022-06-23 09:18:26
	 */
	public List<Score> selectScoreAll();

	/**
	 * @MethodName: deleteScoreByMatchsIdForApi
	 * @Description: 通过比赛id删除两队伍的得分
	 * @param matchsId 比赛id
	 * @return int 删除数量
	 * @date: 2026-11-17
	 */
	public int deleteScoreByMatchsIdForApi(int matchsId);
}
