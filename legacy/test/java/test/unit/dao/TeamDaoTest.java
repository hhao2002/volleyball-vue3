package test.unit.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.edu.hnzj.dao.TeamDao;

/**
 * @ClassName: TeamDaoTest
 * @Description: 测试TeamDao的sql语句操作是否有问题
 * @author: HhaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-13 04:20:57
 */
public class TeamDaoTest {
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-*.xml");
	TeamDao teamDao = context.getBean("teamDao", TeamDao.class);

	/**
	 * @MethodName: selectTeamByMatchIdTest
	 * @Description: 测试获取比赛的所有队伍
	 * @date: 2022-05-13 04:22:00
	 */
	@Test
	public void selectTeamByMatchIdTest() {
		assertEquals(0, teamDao.selectTeamByMatchId(2).size());
	}
	
	@Test
	public void updateMatchIdToOne() {
		assertEquals(0, teamDao.updateMatchIdOfTeamToOneByMatchId(2));
	}
	
}
