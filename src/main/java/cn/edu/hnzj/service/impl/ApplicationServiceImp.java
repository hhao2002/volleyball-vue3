package cn.edu.hnzj.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.edu.hnzj.dao.ApplicationDao;
import cn.edu.hnzj.pojo.Application;
import cn.edu.hnzj.pojo.User;
import cn.edu.hnzj.service.ApplicationService;

@Service("applicationService")
public class ApplicationServiceImp implements ApplicationService {

	@Resource
	ApplicationDao applicationDao;

	/**
	 * @MethodName: addApp
	 * @Description: 添加一条申请
	 * @param application, user
	 * @return 是否添加成功
	 * @see cn.edu.hnzj.service.ApplicationService#addApp(cn.edu.hnzj.pojo.Application)
	 * @date: 2023-04-23 06:33:55
	 */
	@Override
	public boolean addApp(Application application, User user) {
//		TODO 设置申请类型
		if ("记分员".equals(user.getRole()))
			application.setApTypeId(2);
		else
			application.setApTypeId(3);
//		TODO 设置申请用户的id
		application.setApUserId(user.getId());
//		TODO 设置申请用户的用户名
		application.setApUserName(user.getUsername());
//		TODO 初始化申请
		application.setDealUserId(1);
		application.setDealUserName("暂无用户");
		application.setTime("");
		application.setState(null);
//		TODO 判断是否成功添加
		if (applicationDao.insertApplicationByObject(application) == 1)
			return true;
		return false;
	}

	@Override
	public boolean updataApp(Application application) {
//		TODO 判断是否修改
		if (applicationDao.updateApplicationByObject(application) == 1)
			return true;
		return false;
	}

	/**
	 * @MethodName: getApps
	 * @Description: 获取相应的请求
	 * @return 请求集合
	 * @see cn.edu.hnzj.service.ApplicationService#getApps()
	 * @date: 2023-04-23 08:45:22
	 */
	@Override
	public List<Application> getApps(User user) {
//		TODO 判断用户是否空
		if (user == null)
			return null;
//		TODO 判断身份
		if ("管理员".equals(user.getRole()))
			return applicationDao.selectApplicationAll();
		return applicationDao.selectApplicationByApUserId(user.getId());
	}

}
