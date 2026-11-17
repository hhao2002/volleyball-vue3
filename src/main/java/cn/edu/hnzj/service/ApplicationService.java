package cn.edu.hnzj.service;

import java.util.List;

import cn.edu.hnzj.pojo.Application;
import cn.edu.hnzj.pojo.User;

public interface ApplicationService {

	/**
	 * @MethodName: addApp
	 * @Description: 添加一条申请
	 * @param application, user
	 * @return boolean
	 * @date: 2023-04-23 06:32:36
	 */
	public boolean addApp(Application application, User user);
	
	/**
	 * @MethodName: updataApp
	 * @Description: 修改一条申请
	 * @param application
	 * @return boolean
	 * @date: 2023-05-03 09:10:34
	 */
	public boolean updataApp(Application application);

	/**
	 * @MethodName: getApps
	 * @Description: 获取相应的请求
	 * @return List<Application>
	 * @date: 2023-04-23 08:43:36
	 */
	public List<Application> getApps(User user);
}
