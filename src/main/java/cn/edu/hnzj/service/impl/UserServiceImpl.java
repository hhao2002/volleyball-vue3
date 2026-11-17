package cn.edu.hnzj.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.edu.hnzj.dao.UserDao;
import cn.edu.hnzj.pojo.User;
import cn.edu.hnzj.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;

	/**
	 * @MethodName: isExist
	 * @Description: 判断用户名是否存在
	 * @param boolean
	 * @see cn.edu.hnzj.service.UserService#save(java.lang.String)
	 * @date: 2023-04-16 09:18:16
	 */
	public boolean isExist(String username) {
		if (userDao.findUserByname(username) == null)
			return false;
		return true;
	}

	/**
	 * @MethodName: save
	 * @Description: 向user表中添加普通用户的数据
	 * @param user
	 * @see cn.edu.hnzj.service.UserService#save(cn.edu.hnzj.pojo.User)
	 * @date: 2022-06-24 01:18:50
	 */
	public boolean save(User user) {
		// 向user表中存储数据
		if ("用户".equals(user.getRole())) {
			userDao.save(user);
//			普通用户
			return true;
		} else {
//			先设置为用户，等待管理员同意
			String role = user.getRole();
			user.setRole("用户");
			userDao.save(user);
			user.setRole(role);
//			申请权限的用户
			return false;
		}
	}

	/**
	 * @MethodName: findAllUserByname
	 * @Description: 通过用户名查询用户具体实现
	 * @param username
	 * @return
	 * @see cn.edu.hnzj.service.UserService#findAllUserByname(java.lang.String)
	 * @date: 2022-06-26 12:06:13
	 */
	@Override
	public User findUserByname(User user) {
		// TODO Auto-generated method stub
		return userDao.findUserByname(user.getUsername());
	}

	/**
	 * @MethodName: userlogin
	 * @Description: 判断用户密码与输入密码是否一致
	 * @param user
	 * @return
	 * @see cn.edu.hnzj.service.UserService#userlogin(cn.edu.hnzj.pojo.User)
	 * @date: 2022-06-26 12:06:19
	 */
	@Override
	public boolean userlogin(User user) {
		// TODO Auto-generated method stub
		User findUserByName = userDao.findUserByname(user.getUsername());
		if (findUserByName != null) {
			if (findUserByName.getPassword().equals(user.getPassword())) {
				BeanUtils.copyProperties(findUserByName, user);
				return true;
			}
		}
		return false;
	}

	/**
	 * @MethodName: updateUserPasswordByUserName
	 * @Description: 判断邮箱是否与原邮箱一致，以便确定是用户本人
	 * @param user
	 * @return
	 * @see cn.edu.hnzj.service.UserService#updateUserPasswordByUserName(cn.edu.hnzj.pojo.User)
	 * @date: 2022-06-28 04:24:37
	 */

	public boolean updateUserPassword(User user) {
		// TODO Auto-generated method stub
		int updateUserPassword = userDao.updateUserPassword(user);
		if (updateUserPassword != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @MethodName: updateUserRole
	 * @Description: 修改用户权限
	 * @param user
	 * @return
	 * @see cn.edu.hnzj.service.UserService#updateUserRole(cn.edu.hnzj.pojo.User)
	 * @date: 2022-06-30 03:31:25
	 */
	public boolean updateUserRole(User user) {
		int updateUserRole = userDao.updateUserRole(user);

		if (updateUserRole != 0) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * @MethodName: deleteUserByName
	 * @Description: 通过用户名删除用户信息
	 * @param username
	 * @return
	 * @see cn.edu.hnzj.service.UserService#deleteUserByName(java.lang.String)
	 * @date: 2022-06-26 02:43:33
	 */
	@Override
	public int deleteUserByName(String username) {
		// TODO Auto-generated method stub
		return userDao.deleteUserByName(username);
	}

	/**
	 * @MethodName: updateUserById
	 * @Description: 通过用户id修改用户信息
	 * @param id
	 * @return
	 * @see cn.edu.hnzj.service.UserService#updateUserById(int)
	 * @date: 2022-06-27 04:06:02
	 */
	@Override
	public int updateUserById(User user) {
		// TODO Auto-generated method stub
		return userDao.updateUserById(user);
	}

	/**
	 * @MethodName: findAllUser
	 * @Description: 查询所有用户
	 * @return
	 * @see cn.edu.hnzj.service.UserService#findAllUser()
	 * @date: 2022-06-27 04:06:27
	 */
	@Override
	public List<User> findAllUser() {
		// TODO Auto-generated method stub
		return userDao.findAllUser();
	}

}
