package test;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

public class UnionTest {

	/**
	 * @MethodName: runAllTest
	 * @Description: 自动执行所有的单元测试
	 * @throws Exception void
	 * @date: 2022-05-04 12:54:53
	 */
	@Test
	public void runAllTest() throws Exception {
		try {
			runPackage("");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("------------------------------------------------------------------------------------------");
			System.out.println("test.unit包内的类有问题");
			e.printStackTrace();
			throw e;
		}
		try {
			runPackage(".controller");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("------------------------------------------------------------------------------------------");
			System.out.println("test.unit.controller包内的类有问题");
			e.printStackTrace();
			throw e;
		}
		try {
			runPackage(".service");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("------------------------------------------------------------------------------------------");
			System.out.println("test.unit.service包内的类有问题");
			e.printStackTrace();
			throw e;
		}
		try {
			runPackage(".dao");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("------------------------------------------------------------------------------------------");
			System.out.println("test.unit,dao包内的类有问题");
			e.printStackTrace();
			throw e;
		}
	}
	
	private void runPackage(String pack) throws Exception {
		new ClassUtil();
		Iterator<Class<?>> iterator = ClassUtil.getClassList("test.unit" + pack, false).iterator();
		while (iterator.hasNext()) {
			Class<?> Class = (Class<?>) iterator.next();
			Method[] methods = Class.getDeclaredMethods();
			for (Method method : methods) {
				System.out.println(Class.getName());
				method.setAccessible(true);
				method.invoke(Class.getConstructor().newInstance());
				System.out.println("------------------------------------------------------------------------------------------");
				System.out.println("测试成功：" + Class.getName() + "\t-\t" + method.getName() + ":");
				System.out.println("------------------------------------------------------------------------------------------");
			}
		}
	}

}
