package cn.edu.hnzj.service;

import java.util.List;
import cn.edu.hnzj.pojo.User;

public interface UserService {
	/**
	 * @MethodName: isExist
	 * @Description: 判断用户名是否存在
	 * @param boolean
	 * @date: 2023-04-16 09:18:16
	 */
	public boolean isExist(String username);

	/**
	 * @MethodName: save
	 * @Description: 保存用户注册信息
	 * @param boolean
	 * @date: 2022-06-23 02:49:26
	 */
	public boolean save(User user);

	/**
	 * @MethodName: findAllUserByname
	 * @Description: 通过用户名查询用户信息
	 * @param username
	 * @return User
	 * @date: 2022-06-26 02:43:06
	 */
	public User findUserByname(User user);

	/**
	 * @MethodName: userlogin
	 * @Description: 判断用户密码与输入密码是否一致
	 * @param user
	 * @return boolean
	 * @date: 2022-06-26 02:43:56
	 */
	public boolean userlogin(User user);

	/**
	 * @MethodName: deleteUserByName
	 * @Description: 根据用户名称删除用户信息
	 * @param username
	 * @return int
	 * @date: 2022-06-26 02:17:36
	 */
	public int deleteUserByName(String username);

	/**
	 * @MethodName: updateUserById
	 * @Description: 通过userId修改用户信息
	 * @param id
	 * @return int
	 * @date: 2022-06-27 04:02:29
	 */
	public int updateUserById(User user);

	/**
	 * @MethodName: updateUserPasswordByUserName
	 * @Description: 通过username修改用户密码
	 * @param user
	 * @return int
	 * @date: 2022-06-28 04:22:55
	 */
	public boolean updateUserPassword(User user);

	/**
	 * @MethodName: updateUserPassword
	 * @Description: TODO
	 * @param user
	 * @return boolean
	 * @date: 2022-06-30 03:29:32
	 */
	public boolean updateUserRole(User user);

	/**
	 * @MethodName: findAllUser
	 * @Description: 查询所有用户信息
	 * @return List<User>
	 * @date: 2022-06-27 04:04:05
	 */
	public List<User> findAllUser();
}
