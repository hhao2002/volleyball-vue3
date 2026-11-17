package cn.edu.hnzj.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName: RecordController
 * @Description: 进行jsp页面跳转
 * @author: HHaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-03 05:50:49
 */
@Controller
public class ForwardController {
	@RequestMapping("/index.do")
	public void index() {
	}

	@RequestMapping("/matchs.do")
	public void matchs() {
	}

	@RequestMapping("/recording.do")
	public void recording() {
	}

	@RequestMapping("/statistics.do")
	public void statistics() {
	}

	@RequestMapping("/login.do")
	public void login(HttpServletRequest request) {
	}

	@RequestMapping("/info.do")
	public void info() {
	}
	
	@RequestMapping("/register.do")
	public void register() {
	}
	
	@RequestMapping("/updatepassword.do")
	public void updatepassword() {	
	}
	
	/*
	 * @RequestMapping("/manager.do") public String manager(HttpSession session) {
	 * // 判断session内是否有用户登录 Object user = session.getAttribute("user");
	 * System.out.println(user); if(user != null) { // 判断用户权限是否为管理员 if
	 * ("管理员".equals(((User) user).getRole())) // 跳转管理申请页面 return "manager"; } //
	 * 跳转首页 return "redirect:/index.do"; }
	 */
	
	@RequestMapping("/apply.do")
	public void apply() {
	}

}
