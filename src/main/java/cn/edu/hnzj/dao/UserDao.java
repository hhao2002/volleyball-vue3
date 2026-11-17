package cn.edu.hnzj.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import cn.edu.hnzj.pojo.User;

@Repository
@Mapper
public interface UserDao {

	/**
	 * @MethodName: save
	 * @Description:保存用户信息
	 * @param user void
	 * @date: 2022-06-23 08:57:50
	 */
	public int save(User user);
	
	/**
	 * @MethodName: findAllUserByname
	 * @Description: 通过姓名查找所有对应的用户信息
	 * @return List<User>
	 * @date: 2022-06-25 12:38:02
	 */
	public User findUserByname(String username);
	
	/**
	 * @MethodName: deleteUserByName
	 * @Description: 通过名字删除用户信息（便于save(User user)单元测试）
	 * @param username
	 * @return int
	 * @date: 2022-06-26 02:15:26
	 */
	public int deleteUserByName(String username);
	
	/**
	 * @MethodName: updateUserById
	 * @Description: 通过userId修改用户信息
	 * @param id
	 * @return int
	 * @date: 2022-06-27 04:01:31
	 */
	public  int updateUserById(User user);
	
	/**
	 * @MethodName: updateUserRole
	 * @Description: 修改用户权限
	 * @param user
	 * @return int
	 * @date: 2022-06-30 03:28:52
	 */
	public int updateUserRole(User user);
	
	/**
	 * @MethodName: updateUserByUserName
	 * @Description: 修改用户密码
	 * @param user
	 * @return int
	 * @date: 2022-06-28 04:20:05
	 */
	public int updateUserPassword(User user);
	
	/**
	 * @MethodName: findAllUser
	 * @Description: 查询所有用户信息
	 * @return List<User>
	 * @date: 2022-06-27 04:03:40
	 */
	public List<User> findAllUser();

	
}
