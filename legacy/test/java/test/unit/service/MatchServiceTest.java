package test.unit.service;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.edu.hnzj.service.MatchService;

public class MatchServiceTest {
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-*.xml");
	MatchService matchService = context.getBean("matchService", MatchService.class);

	@Test
	public void TestGetMvp() {
//		System.out.println(matchService.getMvp(1));
	}

	@Test
	public void addMatchByObjectTest() {
		// 创建一个比赛
//		matchService.addMatchByObject(new Match());
	}
}