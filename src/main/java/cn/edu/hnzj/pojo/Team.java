/**
 * @Title: Team.java
 * @Description: TODO
 * @author: 
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-03 04:52:00
 */
package cn.edu.hnzj.pojo;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * @ClassName: Team
 * @Description: volleyball.team表 的 封装对象
 * @author: HhaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-03 04:52:00
 */
@Component
public class Team {
	/**
	 * @Fields id : 自增属性
	 * @date: 2022-05-10 10:28:29
	 */
	private int id;
	/**
	 * @Fields matchId : 比赛的id（1：暂时没进行比赛）
	 * @date: 2022-05-10 10:28:39
	 */
	private int matchId;
	/**
	 * @Fields name : 队名
	 * @date: 2022-05-10 10:28:52
	 */
	private String name;
	/**
	 * @Fields coach : 教练
	 * @date: 2022-05-10 10:29:05
	 */
	private String coach;

	/**
	 * @Fields members : 拥有的队员
	 * @date: 2022-05-10 10:30:12
	 */
	private List<Member> members;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMatchId() {
		return matchId;
	}

	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCoach() {
		return coach;
	}

	public void setCoach(String coach) {
		this.coach = coach;
	}

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

	@Override
	public String toString() {
		return "Team [id=" + id + ", matchId=" + matchId + ", name=" + name + ", coach=" + coach + ", members="
				+ members + "]";
	}
}
