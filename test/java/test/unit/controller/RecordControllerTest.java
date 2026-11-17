package test.unit.controller;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cn.edu.hnzj.controller.RecordController;

public class RecordControllerTest {
	private MockMvc mockMvc;

	// 初始化MockMvc
	public RecordControllerTest() {
		org.springframework.context.ApplicationContext ap = new ClassPathXmlApplicationContext(
				new String[] { "/springmvc.xml", "/mybatis.xml", "/applicationContext-*.xml" });
		this.mockMvc = MockMvcBuilders.standaloneSetup(ap.getBean(RecordController.class)).build();
	}
}
