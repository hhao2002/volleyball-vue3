package test.unit.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.Charset;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cn.edu.hnzj.controller.TeamController;
import cn.edu.hnzj.pojo.Match;
import cn.edu.hnzj.pojo.Team;

//@RunWith(SpringJUnit4ClassRunner.class) 不知到为啥我用注释一直没加载出来
//@WebAppConfiguration
//@ContextConfiguration(locations = {"classpath:/springmvc.xml,/mybatis.xml,/applicationContext-*.xml" })
public class TeamControllerTest {
	private MockMvc mockMvc;

	// 初始化MockMvc
	public TeamControllerTest() {
		// 测试Team类
		org.springframework.context.ApplicationContext ap = new ClassPathXmlApplicationContext(
				new String[] { "/springmvc.xml", "/mybatis.xml", "/applicationContext-*.xml" });
		this.mockMvc = MockMvcBuilders.standaloneSetup(ap.getBean(TeamController.class)).build();
	}

	/**
	 * @MethodName: getTeams
	 * @Description: 返回所有team对象 JSON
	 * @throws Exception void
	 * @date: 2022-06-22 09:46:34
	 */
	@Test
	public void getTeams() throws Exception {
		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/team/getAll.do")// uri
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_JSON))// 发出与接收的数据格式
				.andExpect(status().isOk()).andReturn();// 判断服务皮是否正常 然后返回
		// 是否 返回对象
		assertNotNull(result.getResponse().getContentAsString(Charset.forName("utf-8")));
	}

	/**
	 * @MethodName: updateTeamsNoMatch
	 * @Description: session没有match时，返回error 页面
	 * @throws Exception void
	 * @date: 2022-06-22 09:37:30
	 */
	@Test
	public void updateTeamsNoMatch() throws Exception {
		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/team/updateTeams.do")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().is(302)).andReturn();// 跳转状态 302
		// 是否 没有match
		assertNull(result.getRequest().getSession().getAttribute("match"));
		// 是否跳转到 /error.do
		assertEquals(result.getResponse().getRedirectedUrl(), "/error.do");
	}

	/**
	 * @MethodName: updateTeamsHasMatch
	 * @Description: 更改队伍的比赛id测试 没有得分信息 因此返回报错页面
	 * @throws Exception void
	 * @date: 2022-06-22 09:48:47
	 */
	@Test
	public void updateTeamsHasMatch() throws Exception {
		ArrayList<Team> teams = new ArrayList<Team>();
		Team team = new Team();
		team.setName("暂无队伍");
		teams.add(team);
		Match match = new Match();
		match.setId(1);
		match.setTeams(teams);

		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/team/updateTeams.do")
				.sessionAttr("match", match)// 设置session属性
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().is(302)).andReturn();
		// 是否会跳转到 /recording.do
		assertEquals(result.getResponse().getRedirectedUrl(), "/error.do");
	}
}
