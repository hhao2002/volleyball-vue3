package test.unit.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.edu.hnzj.dao.ScoreDao;
import cn.edu.hnzj.pojo.Score;

public class ScoreDaoTest {
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
			"applicationContext-*.xml");
	private static ScoreDao scoreDao = context.getBean("scoreDao", ScoreDao.class);
	private static Score score = new Score();

	/**
	 * @MethodName: insertScoreByObjectTest
	 * @Description: 添加一个得分对象 测试 （必须有 matchs_id 和 team_id）
	 * @date: 2022-06-27 04:30:11
	 */
	@BeforeAll
	public static void insertScoreByObjectTest() {
		// TODO 初始化信息
		score.setMatchsId(1);
		score.setTeamId(1);
		// TODO 添加数据
		assertEquals(scoreDao.insertScoreByObject(score), 1);
	}

	/**
	 * @MethodName: deleteScoreByIdTest
	 * @Description: 通过 id 删除 数据库数据 测试
	 * @date: 2022-06-27 04:29:37
	 */
	@AfterAll
	public static void deleteScoreByIdTest() {
		assertEquals(scoreDao.deleteScoreById(score.getId()), 1);
	}

	/**
	 * @MethodName: deleteScoreByMatchsIdTest
	 * @Description: 通过比赛id删除两个记录 测试
	 * @date: 2022-06-23 09:33:23
	 */
	@Test
	public void deleteScoreByMatchsIdTest() {
		// TODO 初始化数据
		score.setId(score.getId() + 1);
		insertScoreByObjectTest();
		// TODO 删除数据
		assertEquals(scoreDao.deleteScoreByMatchsId(score.getMatchsId()), 2);
		// TODO 恢复数据
		score.setId(score.getId() + 1);
		insertScoreByObjectTest();
	}

	/**
	 * @MethodName: updateScoreByObjectTest
	 * @Description: 通过id修改Score对象 测试
	 * @date: 2022-06-23 09:18:26
	 */
	@Test
	public void updateScoreByObjectTest() {
		// TODO 修改信息
		score.setTeamId(2);
		score.setdScore(2);
		score.setxScore(6);
		// TODO 通过id修改方法
		assertEquals(scoreDao.updateScoreByObject(score), 1);
		// TODO 恢复数据
		score.setMatchsId(1);
		score.setTeamId(1);
		assertEquals(scoreDao.updateScoreByObject(score), 1);
	}

	/**
	 * @MethodName: updateScoreByMIdTIdTest
	 * @Description: 通过比赛id和队伍id修改得分信息 测试
	 * @date: 2022-06-27 04:23:43
	 */
	@Test
	public void updateScoreByMIdTIdTest() {
		// TODO 修改信息
		score.setdScore(2);
		score.setxScore(0);
		// TODO 通过id修改方法
		assertEquals(scoreDao.updateScoreByMIdTId(score), 1);
	}

	/**
	 * @MethodName: updateScoreByMIdTIdTest
	 * @Description: 修改 分数为负数时 测试
	 * @date: 2022-06-27 04:23:43
	 */
	@Test
	public void nagtiveWithUpdateScoreByMIdTIdTest() {
		// TODO 修改信息
		score.setdScore(-1);
		score.setxScore(-3);
		// TODO 通过id修改方法
		assertEquals(scoreDao.updateScoreByMIdTId(score), 1);
		// TODO 验证是否不会修改 并 测试 查询一个
		assertNotEquals(scoreDao.selectScoreById(score.getId()), score);
	}

	/**
	 * @MethodName: selectScoreByMatchsIdTest
	 * @Description: 查询同一比赛的两个得分 测试
	 * @date: 2022-06-27 05:13:51
	 */
	@Test
	public void selectScoreByMatchsIdTest() {
		// TODO 初始化数据
		score.setId(score.getId() + 1);
		insertScoreByObjectTest();
		// TOOD 查询两个队伍信息
		List<Score> scores = scoreDao.selectScoreByMatchsId(score.getMatchsId());
		// 验证是否长度为2 且 是否对有信息
		assertEquals(2, scores.size());
		assertEquals(scores.get(1).getxScore(), score.getxScore());
		// TODO 恢复数据
		scoreDao.deleteScoreById(score.getId());
		score.setId(score.getId() - 1);
	}
	
	/**
	 * @MethodName: selectScoreAllTest
	 * @Description: 获取所有的得分对象 测试
	 * @date: 2022-06-27 05:32:42
	 */
	@Test
	public void selectScoreAllTest() {
		assertNotNull(scoreDao.selectScoreAll());
	}
}
