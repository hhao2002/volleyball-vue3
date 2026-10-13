package cn.edu.hnzj.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.edu.hnzj.pojo.Application;

/**
 * @ClassName: ApplicationDao
 * @Description: volleyball.application表 的 dao层
 * @author: HhaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-06-28 09:21:05
 */
@Repository
public interface ApplicationDao {

	/**
	 * @MethodName: insertApplicationByObject
	 * @Description: 传入Application对象 向 volleyball.application表 添加一条记录
	 * @param application 添加申请的对象
	 * @return int 添加申请的数量
	 * @date: 2022-06-28 09:24:01
	 */
	public int insertApplicationByObject(Application application);

	/**
	 * @MethodName: deleteApplicationById
	 * @Description: 传入id 向 volleyball.application表 删除一条记录
	 * @param id 删除申请的id
	 * @return int 删除申请的数量
	 * @date: 2022-06-28 09:26:43
	 */
	public int deleteApplicationById(int id);

	/**
	 * @MethodName: updateApplicationByObject
	 * @Description: 传入Application对象 向 volleyball.application表 通过id更改一条记录
	 * @param application 修改申请的对象
	 * @return int 修改匹配到的行数
	 * @date: 2022-06-28 09:27:43
	 */
	public int updateApplicationByObject(Application application);

	/**
	 * @MethodName: selectApplicationById
	 * @Description: 传入id 向 volleyball.application表 查询一条记录
	 * @param id 申请的id
	 * @return Application 申请的对象
	 * @date: 2022-06-28 09:28:52
	 */
	public Application selectApplicationById(int id);

	/**
	 * @MethodName: selectApplicationByApUserId
	 * @Description: 传入userid 向 volleyball.application表 查询 该用户的所有申请
	 * @param userid
	 * @return Application 某用户的所有申请
	 * @date: 2023-04-20 11:48:38
	 */
	public List<Application> selectApplicationByApUserId(int apUserId);

	/**
	 * @MethodName: selectApplicationAll
	 * @Description: 向 volleyball.application表 查询所有记录
	 * @return List<Application> 所有的申请
	 * @date: 2022-06-28 09:29:44
	 */
	public List<Application> selectApplicationAll();
}
