package cn.edu.hnzj.pojo;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

/**
 * @ClassName: History
 * @Description: volleyball.history表 的 封装对象
 * @author: HHaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-07-01 10:44:33
*/
@Component
public class History {
	/**
	 * @Fields id : 自增属性
	 * @date: 2022-07-01 10:41:09
	 */
	private Integer id;
	/**
	 * @Fields matchsId : 比赛id
	 * @date: 2022-07-01 10:41:19
	 */
	private Integer matchsId;
	/**
	 * @Fields userId : 用户id
	 * @date: 2022-07-01 10:41:32
	 */
	private Integer userId;
	/**
	 * @Fields name : 比赛名称
	 * @date: 2022-07-01 10:41:43
	 */
	private String name;
	/**
	 * @Fields role : 用户身份
	 * @date: 2022-07-01 10:41:50
	 */
	private String role;
	/**
	 * @Fields time : 观看时间
	 * @date: 2022-07-01 10:42:01
	 */
	private Timestamp time;
	/**
	 * @Fields match : 比赛对象
	 * @date: 2022-07-01 10:42:27
	 */
	private Match match;
	/**
	 * @Fields user : 用户对象
	 * @date: 2022-07-01 10:42:33
	 */
	private User user;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMatchsId() {
		return matchsId;
	}

	public void setMatchsId(Integer matchsId) {
		this.matchsId = matchsId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getTime() {
		if (time == null)
			return null;
		return time.toString();
	}

	public void setTime(String time) {
		if (time.length() == 19)
			this.time = Timestamp.valueOf(time);
		else if (time != "")
			this.time = Timestamp.valueOf(time.replace('T', ' ') + ":00");
	}

	public Match getMatch() {
		return match;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "{\\\"id\\\":\\\"" + id + "\\\", \\\"matchsId\\\":\\\"" + matchsId + "\\\", \\\"userId\\\":\\\"" + userId
				+ "\\\", \\\"name\\\":\\\"" + name + "\\\", \\\"role\\\":\\\"" + role + "\\\", \\\"time\\\":\\\"" + time
				+ "\\\", \\\"match\\\":\\\"" + match + "\\\", \\\"user\\\":\\\"" + user + "\\\"}";
	}
}
