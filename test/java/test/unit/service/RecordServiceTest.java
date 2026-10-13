package test.unit.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.edu.hnzj.service.RecordService;

class RecordServiceTest {
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-*.xml");
	RecordService recordService = context.getBean("recordService", RecordService.class);
}
