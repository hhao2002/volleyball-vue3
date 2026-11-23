package test.unit.dao;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.edu.hnzj.dao.MatchDao;
import cn.edu.hnzj.dao.MemberDao;

public class MemberDaoTest {
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-*.xml");
	MemberDao memberDao = context.getBean("memberDao", MemberDao.class);
	MatchDao matchDao = context.getBean("matchDao", MatchDao.class);

	@Test
	public void selectMemberByIdTest() {
		assertNotNull(memberDao.selectMemberAll());
	}

	@Test
	public void TestSelectScoreByMemberAndMatch() {
		assertNotNull(memberDao.selectScoreByMemberAndMatch(8, 100));
	}

	@Test
	public void TestGetScoreIdByMemberAndMatch() {
		assertNotNull("asdfasdfsa" + memberDao.getScoreIdByMemberAndMatch(8, 100));
	}

	@Test
	public void getMembersBymatchId() {
		assertNotNull(matchDao.getMembersBymatchId(101));
	}

	@Test
	public void TestSelectMemberById() {
		assertNotNull(memberDao.selectMemberById(1));
	}
}
