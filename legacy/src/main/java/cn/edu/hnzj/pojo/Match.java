/**
 * @Title: Match.java
 * @Description: TODO
 * @author: 
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-03 04:51:17
 */
package cn.edu.hnzj.pojo;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * @ClassName: Match
 * @Description: volleyball.match表 比赛信息
 * @author: HhaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-03 04:51:17
 */
@Component
public class Match {
	/**
	 * @Fields id : 自增属性
	 * @date: 2022-05-10 09:33:07
	 */
	private Integer id;
	/**
	 * @Fields name : 比赛名称
	 * @date: 2022-05-10 09:33:31
	 */
	private String name;
	/**
	 * @Fields rules : 比赛规则（场数-局数）
	 * @date: 2022-05-10 09:33:41
	 */
	private String rules;
	/**
	 * @Fields startTime : 开始时间（无：现在时间）
	 * @date: 2022-05-10 09:34:14
	 */
	private Timestamp startTime;
	/**
	 * @Fields EndTime : 结束时间
	 * @date: 2022-05-10 09:34:42
	 */
	private Timestamp endTime;
	/**
	 * @Fields sponsor : 举办方
	 * @date: 2022-05-10 09:35:01
	 */
	private String sponsor;
	/**
	 * @Fields state : 状态（预备|进行中|结束）
	 * @date: 2022-05-10 09:35:08
	 */
	private String state;
	
	/**
	 * @Fields scorerId : 记分员的id
	 * @date: 2022-06-27 10:49:56
	 */
	private Integer scorerId;

	/**
	 * @Fields teams : 比赛的队伍
	 * @date: 2022-05-10 09:41:46
	 */
	private List<Team> teams;
	
	/**
	 * @Fields socres : 此比赛的得分
	 * @date: 2022-06-27 11:24:49
	 */
	private List<Score> scores;

	public List<Score> getScores() {
		return scores;
	}

	public void setScores(List<Score> scores) {
		this.scores = scores;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRules() {
		return rules;
	}

	public void setRules(String rules) {
		this.rules = rules;
	}

	public String getStartTime() {
		if (startTime == null)
			return null;
		return startTime.toString();
	}

	public void setStartTime(String startTime) {
		if (startTime.length() == 19)
			this.startTime = Timestamp.valueOf(startTime);
		else if (startTime != "")
			this.startTime = Timestamp.valueOf(startTime.replace('T', ' ') + ":00");
	}

	public String getEndTime() {
		if (endTime == null)
			return null;
		return endTime.toString();
	}

	public void setEndTime(String endTime) {
		if (endTime.length() == 19)
			this.endTime = Timestamp.valueOf(endTime);
		else if (endTime != "")
			this.endTime = Timestamp.valueOf(endTime.replace('T', ' ') + ":00");
	}

	public String getSponsor() {
		return sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<Team> getTeams() {
		return teams;
	}

	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}

	public Integer getScorerId() {
		return scorerId;
	}

	public void setScorerId(Integer scorerId) {
		this.scorerId = scorerId;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		return "{\\\"id\\\":\\\"" + id + "\\\", \\\"name\\\":\\\"" + name + "\\\", \\\"rules\\\":\\\"" + rules
				+ "\\\", \\\"startTime\\\":\\\"" + startTime + "\\\", \\\"endTime\\\":\\\"" + endTime
				+ "\\\", \\\"sponsor\\\":\\\"" + sponsor + "\\\", \\\"state\\\":\\\"" + state
				+ "\\\", \\\"scorerId\\\":\\\"" + scorerId + "\\\", \\\"teams\\\":\\\"" + teams
				+ "\\\", \\\"scores\\\":" + scores + "}";
	}

}
