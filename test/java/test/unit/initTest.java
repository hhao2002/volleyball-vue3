package test.unit;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @ClassName: initTest
 * @Description: 测试配置文件是否可运行，以及是否可链接数据库
 * @author: HhaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-07 11:18:02
*/
public class initTest {

	/**
	 * @MethodName: applicationinitTest
	 * @Description: TODO 初始化测试
	 * @date: 2022-05-07 11:17:51
	 */
	@Test
	public void applicationinitTest() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext-*.xml", "springmvc.xml" });
		assertNotNull(context);
		assertNotNull(context.getBean("sqlSessionFactory"));
		assertNotNull(context.getBean("txAdvice"));
		assertNotNull(context.getBean("recordController"));
		assertNotNull(context.getBean("record"));
		// TODO 测试是否能连接到数据库
		JdbcTemplate jdbcTemplate = context.getBean("jdbcTemplate",org.springframework.jdbc.core.JdbcTemplate.class);
		jdbcTemplate.execute("show databases;");
	}

}
