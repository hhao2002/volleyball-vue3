package test.unit.service;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.edu.hnzj.service.MemberService;

public class MemberServiceTest {
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-*.xml");
	MemberService memberService = context.getBean("memberService", MemberService.class);

	@Test
	public void TestCoiledScoreNum() {
		System.out.println(memberService.coiledScoreNum(1, 101));
	}
	
	@Test
	public void TestSelectScoreByMemberAndMatch() {
		System.out.println(memberService.selectScoreByMemberAndMatch(1, 101));
	}
	
	@Test
	public void TestCoiledScoreSum() {
		System.out.println(memberService.coiledScoreSum(1, 101));
	}
	
	@Test
	public void TestGetScoreMatch() {
		System.out.println(memberService.getScoreMatch(1, 101));
	}
}
