package cn.edu.hnzj.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.edu.hnzj.pojo.History;

/**
 * @ClassName: HistoryDao
 * @Description: volleyball.history表 的 dao层
 * @author: HhaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-07-01 11:04:30
*/
@Repository
public interface HistoryDao {

	/**
	 * @MethodName: insertHistoryByObject
	 * @Description: 传入History对象 向 volleyball.history表 添加一条history记录
	 * @param history 添加历史的信息
	 * @return int 添加历史的数量
	 * @date: 2022-06-23 09:18:26
	 */
	public int insertHistoryByObject(History history);
	
	/**
	 * @MethodName: deleteHistoryById
	 * @Description: 传入id 向volleyball.history 删除一条history记录
	 * @param matchsId 删除的id
	 * @return int 删除的数量
	 * @date: 2022-06-23 09:31:21
	 */
	public int deleteHistoryById(int id);

	/**
	 * @MethodName: selectHistoryById
	 * @Description: 传入id 向 volleyball.history 表 查询一条history对象
	 * @param id 历史id
	 * @return History 历史信息
	 * @date: 2022-06-27 01:41:37
	 */
	public History selectHistoryById(int id);

	/**
	 * @MethodName: selectHistoryByMatchsId
	 * @Description: 传入比赛id 向 volleyball.history表 查询该比赛的所有history对象
	 * @param matchsId 要查询的比赛id
	 * @return List<History> 该比赛的所有history对象
	 * @date: 2022-06-23 09:18:26
	 */
	public List<History> selectHistoryByMatchsId(int matchsId);
	
	/**
	 * @MethodName: selectHistoryByMatchsId
	 * @Description: 传入用户id 向 volleyball.history表 查询该用户的所有history对象
	 * @param userId 要查询的用户id
	 * @return List<History> 该用户的所有history对象
	 * @date: 2022-06-23 09:18:26
	 */
	public List<History> selectHistoryByUserId(int userId);

	/**
	 * @MethodName: selectHistoryByMatchsId
	 * @Description: 向 volleyball.history表 查询所有的history对象
	 * @return List<History> 所有history对象
	 * @date: 2022-06-23 09:18:26
	 */
	public List<History> selectHistoryAll();
}
