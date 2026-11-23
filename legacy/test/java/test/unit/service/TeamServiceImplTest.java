package test.unit.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.edu.hnzj.pojo.Match;
import cn.edu.hnzj.service.TeamService;

/**
 * @ClassName: TeamServiceImplTest
 * @Description: 队伍的业务层测试
 * @author: HhaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-06-24 03:30:57
*/
public class TeamServiceImplTest {
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-*.xml");
	TeamService teamService = context.getBean("teamService", TeamService.class);

	/**
	 * @MethodName: updateTeamInfoTest
	 * @Description: 测试 是否能获取完整比赛信息
	 * @date: 2022-06-24 03:29:27
	 */
	@Test
	public void updateTeamInfoTest() {
		Match match = new Match();
		match.setId(1);
		assertNotNull(teamService.updateTeamInfo(match));
	}
	
	@Test
	public void TestUpdateMatchIdOfTeamToOneByMatchId() {
		assertEquals(false, teamService.updateMatchIdOfTeamToOneByMatchId(1));
	}
}
