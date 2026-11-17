package cn.edu.hnzj.pojo;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName: Record
 * @Description: volleyball.record表 的 封装对象
 * @author: HHaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-03 04:48:55
 */
@Component
public class Record {
	/**
	 * @Fields id : TODO
	 * @date: 2022-05-10 11:11:48
	 */
	private Integer id;
	/**
	 * @Fields session : 当前的场次数
	 * @date: 2022-05-10 04:20:15
	 */
	@Value("1")
	private Integer session;
	/**
	 * @Fields inning : 当前的局数
	 * @date: 2022-05-10 04:20:24
	 */
	@Value("0")
	private Integer inning;
	/**
	 * @Fields matchId : 当前的比赛id
	 * @date: 2022-05-10 04:20:53
	 */
	private Integer matchId;
	/**
	 * @Fields teamId : 得分队伍的id
	 * @date: 2022-06-27 10:51:27
	 */
	private Integer teamId;
	/**
	 * @Fields memberId : 得分球员id
	 * @date: 2022-05-10 04:21:04
	 */
	private Integer memberId;
	/**
	 * @Fields scoureTime : 得分时间
	 * @date: 2022-05-10 04:21:26
	 */
	private Timestamp scoreTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSession() {
		return session;
	}

	public void setSession(Integer session) {
		this.session = session;
	}

	public Integer getInning() {
		return inning;
	}

	public void setInning(Integer inning) {
		this.inning = inning;
	}

	public Integer getMatchId() {
		return matchId;
	}

	public void setMatchId(Integer matchId) {
		this.matchId = matchId;
	}

	public Integer getTeamId() {
		return teamId;
	}

	public void setTeamId(Integer teamId) {
		this.teamId = teamId;
	}

	public void setScoreTime(Timestamp scoreTime) {
		this.scoreTime = scoreTime;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getScoreTime() {
		if (scoreTime == null)
			return null;
		return scoreTime.toString();
	}

	public void setScoreTime(String scoreTime) {
		if (scoreTime.length() == 19)
			this.scoreTime = Timestamp.valueOf(scoreTime);
		else if (scoreTime != "")
			this.scoreTime = Timestamp.valueOf(scoreTime.replace('T', ' ') + ":00");
	}

	@Override
	public String toString() {
		return "Record [id=" + id + ", session=" + session + ", inning=" + inning + ", matchId=" + matchId + ", teamId="
				+ teamId + ", memberId=" + memberId + ", scoreTime=" + scoreTime + "]";
	}

}
