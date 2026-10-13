package test.unit.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.Charset;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cn.edu.hnzj.controller.MatchController;

public class MatchControllerTest {
	private MockMvc mockMvc;

	public MatchControllerTest() {
		org.springframework.context.ApplicationContext ap = new ClassPathXmlApplicationContext(
				new String[] { "/springmvc.xml", "/mybatis.xml", "/applicationContext-*.xml" });
		this.mockMvc = MockMvcBuilders.standaloneSetup(ap.getBean(MatchController.class)).build();
	}

	/**
	 * @MethodName: getMatchs
	 * @Description: 返回所有team对象 JSON
	 * @throws Exception void
	 * @date: 2022-06-22 09:46:34
	 */
	@Test
	public void getMVPTest() throws Exception {
//		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/match/getMvp.do").param("matchId", "1")// uri
//				.contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_JSON))// 发出与接收的数据格式
//				.andExpect(status().isOk()).andReturn();// 判断服务皮是否正常 然后返回
//		// 是否 返回对象
//		System.out.println(result.getResponse().getContentAsString(Charset.forName("utf-8")));
	}
}
