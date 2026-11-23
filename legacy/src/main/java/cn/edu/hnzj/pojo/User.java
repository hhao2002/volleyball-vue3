package cn.edu.hnzj.pojo;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * @ClassName: User
 * @Description: volleyball.user表
 * @author: HHaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-06-27 11:26:57
 */
@Component
public class User {
	/**
	 * @Fields id : 自增属性
	 * @date: 2022-06-27 11:48:32
	 */
	private Integer id;
	/**
	 * @Fields username : 用户名
	 * @date: 2022-06-27 11:48:43
	 */
	private String username;
	/**
	 * @Fields password : 密码
	 * @date: 2022-06-27 11:48:49
	 */
	private String password;
	/**
	 * @Fields email : 邮箱
	 * @date: 2022-06-27 11:48:56
	 */
	private String email;
	/**
	 * @Fields role : 身份
	 * @date: 2022-06-27 11:49:12
	 */
	private String role;
	/**
	 * @Fields ap : 申请（管理员显示所有未处理申请 其他用户只显示申请人是自己的申请）
	 * @date: 2022-06-27 11:49:22
	 */
	private List<Application> ap;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<Application> getAp() {
		return ap;
	}

	public void setAp(List<Application> ap) {
		this.ap = ap;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", email=" + email + ", role="
				+ role + ", ap=" + ap + "]";
	}
}
