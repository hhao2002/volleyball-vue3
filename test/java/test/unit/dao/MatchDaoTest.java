package test.unit.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.edu.hnzj.dao.MatchDao;
import cn.edu.hnzj.pojo.Match;
import cn.edu.hnzj.pojo.Record;
import cn.edu.hnzj.pojo.Score;

/**
 * @ClassName: MatchDaoTest
 * @Description: 测试MatchDao的sql语句操作是否有问题
 * @author: HhaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-13 04:20:57
 */
public class MatchDaoTest {
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-*.xml");
	MatchDao matchDao = context.getBean("matchDao", MatchDao.class);

	/**
	 * @MethodName: insertMatchByObjectNullTest
	 * @Description: 测试添加空对象时
	 * @date: 2022-05-13 04:28:08
	 */
	@Test
	public void insertMatchByObjectNullTest() {
//		TODO 初始化变量
		Match match = new Match();
		match.setId(2);
		match.setScorerId(1);
//		TODO 添加数据
		int row = matchDao.insertMatchByObject(match);
		assertEquals(1, row);
//		TODO 回复数据
		row = matchDao.deleteMatchById(match.getId());
		assertEquals(1, row);
	}

	/**
	 * @MethodName: insertMatchByObjectNoIdSenIngAScTmTest
	 * @Description: 测试只有MatchId和MemberId属性时，添加进入数据库
	 * @date: 2022-05-13 04:22:00
	 */
	@Test
	public void insertMatchByObjectNoIdSenIngAScTmTest() {
//		TODO 初始化变量
		Match match = new Match();
		match.setName("大学比赛");
		match.setRules("3-5");
		match.setScorerId(1);
		match.setSponsor("院长");
		match.setState("结束");
//		TODO 添加数据
		int row = matchDao.insertMatchByObject(match);
		assertEquals(1, row);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		TODO 回复数据
		row = matchDao.deleteMatchById(match.getId());
		assertEquals(1, row);
	}
	
	/**
	 * @MethodName: selectMatchAllTest
	 * @Description: TODO 测试 查询所有比赛
	 * @date: 2022-05-14 05:58:25
	 */
	@Test
	public void selectMatchAllTest() {
		List<Match> selectMatchAll = matchDao.selectMatchAll();
		for (Match match : selectMatchAll) {
			assertNotNull(match);
			System.out.println(match.getEndTime());
		}
	}
	
	/**
	 * @MethodName: TestUpdateMatchStateById
	 * @Description: 测试更改比赛状态
	 * @date: 2022-06-23 06:39:40
	 */
	@Test
	public void TestUpdateMatchStateById() {
		Match match = new Match();
		match.setId(2);
		match.setScorerId(1);
		match.setName("test");
		match.setRules("准备");
		match.setScorerId(2);
		matchDao.insertMatchByObject(match);
		int a = matchDao.updateMatchStateById(2,"结束");
		assertEquals(1, a);
		matchDao.deleteMatchById(2);
	}
	
	@Test
	public void selectMatchByIdTest() {
		Match match = new Match();
		match.setId(2);
		match.setScorerId(1);
		match.setName("test");
		match.setRules("准备");
		match.setScorerId(2);
		matchDao.insertMatchByObject(match);
		System.out.println(matchDao.selectMatchById(2));
		matchDao.deleteMatchById(2);
	}
	/**
	 * @MethodName: deleteMatchByIdTest
	 * @Description: 通过 id 删除 数据库数据 测试
	 * @date: 2022-06-30 21:40:37
	 */
	@Test
	public void deleteMatchByIdTest() {
		Match match = new Match();
		match.setId(3);
		match.setScorerId(2);
		match.setName("测试");
		match.setRules("5-25");
		match.setScorerId(2);
		matchDao.insertMatchByObject(match);
		assertEquals(matchDao.deleteMatchById(match.getId()),1);
	}
}
