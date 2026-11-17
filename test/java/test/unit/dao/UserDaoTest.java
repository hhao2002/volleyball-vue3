package test.unit.dao;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.edu.hnzj.dao.UserDao;
import cn.edu.hnzj.pojo.User;

public class UserDaoTest {

	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-dao.xml");
	UserDao userDao = context.getBean("userDao", UserDao.class);
	User user = new User();

	/**
	 * @MethodName: saveTest
	 * @Description: 测试点击注册按钮向数据库中添加一条记录（并删除）
	 * @date: 2022-06-26 02:40:53 public boolean save(User user);
	 */
	@Test
	public void saveTest() {
		user.setId(88);
		user.setPassword("123456");
		user.setRole("用户");
		user.setUsername("qi六");
		user.setEmail("123@qq.com");
		System.out.println();
		if (userDao.findUserByname(user.getUsername()) == null)
			assertEquals(1, userDao.save(user));
		int deleteUserByName = userDao.deleteUserByName("qi六");
		assertEquals(1, deleteUserByName);
	}

	/**
	 * @MethodName: findAllUserByname
	 * @Description: 测试查询姓名为张三的用户的信息
	 * @date: 2022-06-26 02:41:38 public User findAllUserByname(String username);
	 */
	@Test
	public void findUserByname() {
		User user = userDao.findUserByname("admin");
		System.out.println(user);
		assertEquals("管理员", user.getRole());
	}

	/**
	 * @MethodName: updateUserById
	 * @Description: 测试根据useid修改用户姓名
	 * @date: 2022-06-27 04:57:00 public int updateUserById(int id)
	 */
	@Test
	public void updateUserById() {
		user.setUsername("张三");
		user.setId(2);
		int updateUserById = userDao.updateUserById(user);
		assertEquals(1, updateUserById);
		user.setUsername("admin");
		user.setId(2);
		userDao.updateUserById(user);
	}

	/**
	 * @MethodName: findAllUser
	 * @Description: 测试查询所有用户信息
	 * @date: 2022-06-27 04:50:45 public List<User> findAllUser();
	 */
	@Test
	public void findAllUser() {
		assertEquals(2, userDao.findAllUser().size());
	}

	// public int updateUserPassword (String username ,String email);
	// @Test
//	public void updateUserPassword() {
//		user.setUsername("admin");
//		int updateUserPassword = userDao.updateUserPassword(user.getUsername(), user.getEmail(),user.getPassword());
//		assertEquals(1, updateUserPassword);
//	}
}
