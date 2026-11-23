package test.unit.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.edu.hnzj.dao.HistoryDao;
import cn.edu.hnzj.pojo.History;

public class HistoryDaoTest {
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
			"applicationContext-*.xml");
	private static HistoryDao historyDao = context.getBean("historyDao", HistoryDao.class);
	private static History history = new History();

	/**
	 * @MethodName: insertHistoryByObjectTest
	 * @Description: 添加一个历史对象 测试 （必须有 matchs_id 和 user_id）
	 * @date: 2022-06-27 04:30:11
	 */
	@BeforeAll
	public static void insertHistoryByObjectTest() {
		// TODO 初始化信息
		history.setMatchsId(1);
		history.setUserId(2);
		// TODO 添加数据
		assertEquals(historyDao.insertHistoryByObject(history), 1);
	}

	/**
	 * @MethodName: deleteHistoryByIdTest
	 * @Description: 通过 id 删除 数据库数据 测试
	 * @date: 2022-06-27 04:29:37
	 */
	@AfterAll
	public static void deleteHistoryByIdTest() {
		assertEquals(historyDao.deleteHistoryById(history.getId()), 1);
	}

	/**
	 * @MethodName: selectHistoryByMatchsIdTest
	 * @Description: 查询比赛的所有历史 测试
	 * @date: 2022-06-27 05:13:51
	 */
	@Test
	public void selectHistoryByMatchsIdTest() {
		// TOOD 查询某比赛的所有历史
		List<History> historys = historyDao.selectHistoryByMatchsId(history.getMatchsId());
		// 验证是否长度为2 且 是对有信息
		assertEquals(2, historys.size());
		assertEquals(historys.get(1).getUserId(), history.getUserId());
	}

	/**
	 * @MethodName: selectHistoryByUserIdTest
	 * @Description: 查询用户的所有历史 测试
	 * @date: 2022-06-27 05:13:51
	 */
	@Test
	public void selectHistoryByUserIdTest() {
		// TOOD 查询id为1的用户的所有历史
		List<History> historys = historyDao.selectHistoryByUserId(history.getUserId());
		// 验证是否长度为1 且 是否有信息
		assertEquals(1, historys.size());
		assertEquals(historys.get(0).getUserId(), history.getUserId());
	}

	/**
	 * @MethodName: selectHistoryAllTest
	 * @Description: 获取所有的history对象 测试
	 * @date: 2022-06-27 05:32:42
	 */
	@Test
	public void selectHistoryAllTest() {
		assertNotNull(historyDao.selectHistoryAll());
	}
}
