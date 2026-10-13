package cn.edu.hnzj.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.edu.hnzj.pojo.User;
import cn.edu.hnzj.service.UserService;

/**
 * @ClassName: UserController
 * @Description: TODO
 * @author:
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-06-24 12:31:41
 */
@Controller
@RequestMapping("/users")
public class UserController {

	@Resource
	private UserService userService;

	/**
	 * 
	 * @MethodName: isExist
	 * @Description: 检测用户名是否存在
	 * @param username
	 * @return String
	 * @date: 2022-06-23 08:56:49
	 */
	@RequestMapping("/isExist.do")
	@ResponseBody
	public boolean isExist(@RequestParam("username") String username) {
		System.out.println(username);
		if (userService.isExist(username))
			return true;
		return false;
	}

	@RequestMapping("/updaterole.do")
	public String updatarole(User user, HttpServletRequest request) {
//		TODO 设置用户身份
		switch ((int) request.getAttribute("tid")) {
		case 2:
			user.setRole("记分员");
			break;
		case 3:
			user.setRole("管理员");
			break;
		}
		user.setId((int) request.getAttribute("aid"));
		userService.updateUserById(user);
		return "redirect:/info.do?";
	}

	/**
	 * 
	 * @MethodName: save
	 * @Description: 保存用户注册信息
	 * @param user
	 * @return String
	 * @date: 2022-06-23 08:56:49
	 */
	@RequestMapping("/register.do")
	public String save(User user, HttpServletRequest request) {
		if (userService.save(user)) {
//			成功添加用户 跳转登录页面
			request.setAttribute("msg", "注册成功，请您进行登录！");
			return "forward:/login.do";
		} else {
			// request 传入user.getRole
			request.setAttribute("msg", "注册成功，暂时将您设置为用户，等待管理员确认您的身份！");
			System.out.println("注册用户：" + user);
			request.setAttribute("user", user);
			return "forward:/application/addApp.do";
		}
	}

	/**
	 * @MethodName: userlogin
	 * @Description: 用户登录
	 * @param user
	 * @param request
	 * @param session
	 * @return
	 * @throws IOException String
	 * @date: 2022-06-26 04:31:29
	 */
	@RequestMapping("/login.do")
	public String userlogin(User user, HttpServletRequest request) throws IOException {
		// 用户名是否匹配密码
		if (userService.userlogin(user)) {
//				设置session 跳转至首页
			request.getSession().setAttribute("user", user);
			System.out.println("session的user信息：" + user);
			return "redirect:/index.do?info=400";
		} else {
//			设置提示信息
			request.setAttribute("msg",
					"用户名或密码错误，<a href=\"" + request.getContextPath() + "/updatepassword.do\"> 点击找回密码！</a>");
			request.setAttribute("user", user);
			return "forward:/login.do";
		}

	}

	/**
	 * @MethodName: updatepassword
	 * @Description: 用户修改密码
	 * @param user
	 * @param request
	 * @param session
	 * @return String
	 * @date: 2022-06-29 11:49:16
	 */
	@RequestMapping("/updatepassword.do")
	public String updatepassword(User user, HttpServletRequest request, HttpSession session) {
//		是否获取到用户
		if (user != null) {
//			数据库是否存在此用户
			if (userService.isExist(user.getUsername())) {
//				邮箱和用户名是否匹配
				if (userService.updateUserPassword(user)) {
					request.setAttribute("msg", "密码修改成功");
					return "forward:/login.do";
				} else {
					request.setAttribute("msg", "邮箱错误！！");
					return "forward:/updatepassword.do";
				}
			}
		}
		request.setAttribute("msg", "没有此用户，请确认用户名！！");
		return "forward:/updatepassword.do";
	}
}
