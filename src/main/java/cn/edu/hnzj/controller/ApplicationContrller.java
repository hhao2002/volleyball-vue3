package cn.edu.hnzj.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.edu.hnzj.pojo.Application;
import cn.edu.hnzj.pojo.User;
import cn.edu.hnzj.service.ApplicationService;

@Controller
@RequestMapping("/application")
public class ApplicationContrller {

	@Resource
	private ApplicationService applicationService;

	/**
	 * @MethodName: addApp
	 * @Description: 添加一个申请
	 * @param request
	 * @param application
	 * @return String
	 * @date: 2023-04-23 08:37:05
	 */
	@RequestMapping("/addApp.do")
	public String addApp(HttpServletRequest request, Application application) {
//		TODO 获取用户信息
		User user = (User) request.getAttribute("user");

//		TODO 跳转登录页面
		if (applicationService.addApp(application, user))
			return "forward:/login.do";
		return "redirect:error";
	}

	/**
	 * @MethodName: sendApp
	 * @Description: 用户详细信息页面 发送请求 的处理
	 * @param user
	 * @param application
	 * @return String
	 * @date: 2023-05-03 05:53:42
	 */
	@RequestMapping("/sendApp.do")
	public String sendApp(@RequestBody User user, Application application) {
//		TODO 添加申请
		System.out.println("username:" + user);
		if (applicationService.addApp(application, user))
			return "forward:/info.do";
		return "redirect:error";
	}

	@RequestMapping("/updataApp.do")
	public String updataApp(@RequestBody Application application, HttpServletRequest request) {
//		TODO 判断为同意是，修改权限
		System.out.println("修改的申请信息：" + application);
//		TODO 无论否定还是通过都会执行修改操作 报错返回
		if (!applicationService.updataApp(application))
			return "redirect:error";
		if ("通过".equals(application.getState())) {
			request.setAttribute("tid", application.getApTypeId());
			request.setAttribute("aid", application.getApUserId());
			return "forward:/users/updaterole.do";
		}
		return "redirect:/info.do";
	}

	/**
	 * @MethodName: getApps
	 * @Description: 获取所有 请求 或 某用户的所有请求
	 * @param session
	 * @return String
	 * @date: 2023-04-23 08:38:12
	 */
	@ResponseBody
	@RequestMapping("/getApps.do")
	public List<Application> getApps(HttpSession session) {
//		TODO 获取用户信息
		User user = (User) session.getAttribute("user");
//		TODO 返回对应的申请
		List<Application> apps = applicationService.getApps(user);
		return apps;
	}

}
