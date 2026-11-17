package test.unit.dao;

import static org.junit.Assert.assertEquals;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.edu.hnzj.dao.RecordDao;
import cn.edu.hnzj.pojo.Record;

/**
 * @ClassName: RecordDaoTest
 * @Description: 测试RecordDao的sql语句操作是否有问题
 * @author: HhaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-13 04:20:57
 */
public class RecordDaoTest {
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
			"applicationContext-*.xml");
	private static RecordDao recordDao = context.getBean("recordDao", RecordDao.class);
	private static Record record = context.getBean(Record.class);

	/**
	 * @MethodName: insertRecordByObjectTest
	 * @Description: 添加一个记录对象 测试 （必须有 matchs_id、 team_id 和 member_id）
	 * @date: 2022-06-27 04:30:11
	 */
	@BeforeAll
	public static void insertRecordByObjectTest() {
		// TODO 初始化信息
		System.out.println("rd" + record);
		record.setMatchId(1);
		record.setMemberId(1);
		record.setTeamId(1);
		// TODO 添加数据
		assertEquals(recordDao.insertRecordByObject(record), 1);
	}

	/**
	 * @MethodName: deleteRecordByIdTest
	 * @Description: 通过比赛id 删除 数据库数据 测试
	 * @date: 2022-06-27 04:29:37
	 */
	@AfterAll
	public static void deleteRecordByIdTest() {
		assertEquals(1, recordDao.deleteRecordByMatchId(record.getMatchId()));
	}

	/**
	 * @MethodName: selectListRecordByMatchIdTest
	 * @Description: 通过比赛id删除两个记录 测试
	 * @date: 2022-06-23 09:33:23
	 */
	@Test
	public void selectListRecordByMatchIdTest() {
		// TODO 查询最后一个记录
		assertEquals(recordDao.selectLastRecordByMatchId(record.getMatchId()).getTeamId(), 1);
	}

	/**
	 * @MethodName: updateRecordByMatIdSinIn
	 * @Description: 通过 比赛id、场数、局数 修改 得分者、得分队伍 测试
	 * @date: 2022-06-27 04:23:43
	 */
	@Test
	public void updateRecordByMatIdSinIn() {
		// TODO 修改信息
		record.setMemberId(2);
		record.setTeamId(2);
		// TODO 通过id修改方法
		assertEquals(recordDao.updateRecordByMatIdSinIn(record), 1);
		// TODO 恢复数据
		record.setMemberId(1);
		record.setTeamId(1);
		assertEquals(recordDao.updateRecordByMatIdSinIn(record), 1);
	}

	/**
	 * @MethodName: selectRecordByMatchsIdTest
	 * @Description: 查询同一比赛的两个得分 测试
	 * @date: 2022-06-27 05:13:51
	 */
	@Test
	public void selectRecordByMatchsIdTest() {
		// TOOD 查询某次记录
		assertEquals(1, recordDao.selectRecordByMatIdSinIn(record).getTeamId());
	}

	/**
	 * @MethodName: deleteRecordByMatchId
	 * @Description: 通过id删除记录 测试
	 * @date: 2022-06-27 05:32:42
	 */
	@Test
	public void deleteRecordByMatchId() {
		assertEquals(recordDao.deleteRecordById(record.getId()), 1);
		// TODO 恢复数据
		insertRecordByObjectTest();
	}
}
