package test.unit.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.edu.hnzj.dao.ApplicationDao;
import cn.edu.hnzj.pojo.Application;

public class ApplicationDaoTest {
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
			"applicationContext-*.xml");
	private static ApplicationDao applicationDao = context.getBean("applicationDao", ApplicationDao.class);
	private static Application application = new Application();

	/**
	 * @MethodName: insertApplicationByObjectTest
	 * @Description: 添加一个申请对象 测试 （必须有 ap_type_id、 ap_user_id 和 deal_user_id）
	 * @date: 2022-06-27 04:30:11
	 */
	@BeforeAll
	public static void insertApplicationByObjectTest() {
		// TODO 初始化信息
		application.setApTypeId(1);
		application.setApUserId(1);
		application.setDealUserId(2);
		// TODO 添加数据
		assertEquals(applicationDao.insertApplicationByObject(application), 1);
	}

	/**
	 * @MethodName: deleteApplicationByIdTest
	 * @Description: 通过 id 删除 数据库数据 测试
	 * @date: 2022-06-27 04:29:37
	 */
	@AfterAll
	public static void deleteApplicationByIdTest() {
		assertEquals(applicationDao.deleteApplicationById(application.getId()), 1);
	}

	/**
	 * @MethodName: updateApplicationByObjectTest
	 * @Description: 通过id修改Application对象 测试
	 * @date: 2022-06-23 09:18:26
	 */
	@Test
	public void updateApplicationByObjectTest() {
		// TODO 修改信息
		application.setState("成功");
		// TODO 通过id修改方法
		assertEquals(applicationDao.updateApplicationByObject(application), 1);
	}

	/**
	 * @MethodName: selectApplicationByIdTest
	 * @Description: 通过id查询一个申请 测试
	 * @date: 2022-06-27 05:13:51
	 */
	@Test
	public void selectApplicationByIdTest() {
		// 验证是否长度为2 且 是否对有信息
		assertEquals(applicationDao.selectApplicationById(application.getId()).getApType().getDescription(), "默认申请");
	}

	/**
	 * @MethodName: selectApplicationByIdTest
	 * @Description: 通过id查询一个申请 测试
	 * @date: 2022-06-27 05:13:51
	 */
	@Test
	public void selectApplicationByUserIdTest() {
		// 验证是否长度为2 且 是否对有信息
		System.out.println(applicationDao.selectApplicationByApUserId(application.getApUserId()));
		assertEquals(applicationDao.selectApplicationByApUserId(application.getId()).get(0).getApType().getDescription(), "默认申请");
	}
	
	/**
	 * @MethodName: selectApplicationAllTest
	 * @Description: 获取所有的得分对象 测试
	 * @date: 2022-06-27 05:32:42
	 */
	@Test
	public void selectApplicationAllTest() {
		assertNotNull(applicationDao.selectApplicationAll());
	}
}
