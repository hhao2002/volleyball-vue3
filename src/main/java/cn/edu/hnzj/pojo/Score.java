package cn.edu.hnzj.pojo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName: Score
 * @Description: 存比赛的比分
 * @author: HHaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-06-24 02:31:09
 */
@Component
public class Score {
	/**
	 * @Fields id : 分数的id
	 * @date: 2022-06-23 08:23:20
	 */
	private Integer id;
	/**
	 * @Fields matchsId : 比赛的id （外键）
	 * @date: 2022-06-23 08:23:30
	 */
	private Integer matchsId;
	/**
	 * @Fields teamId : 队伍id
	 * @date: 2022-06-27 11:05:16
	 */
	private Integer teamId;
	/**
	 * @Fields dScore : 该队伍的大比分
	 * @date: 2022-06-27 11:05:30
	 */
	@Value("0")
	private Integer dScore;
	/**
	 * @Fields xScore : 该队伍的小比分
	 * @date: 2022-06-27 11:05:46
	 */
	@Value("0")
	private Integer xScore;

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

	public Integer getTeamId() {
		return teamId;
	}

	public void setTeamId(Integer teamId) {
		this.teamId = teamId;
	}

	public Integer getdScore() {
		return dScore;
	}

	public void setdScore(Integer dScore) {
		this.dScore = dScore;
	}

	public Integer getxScore() {
		return xScore;
	}

	public void setxScore(Integer xScore) {
		this.xScore = xScore;
	}

	@Override
	public String toString() {
		return "{\\\"id\\\":\\\"" + id + "\\\", \\\"matchsId\\\":\\\"" + matchsId + "\\\", \\\"teamId\\\":\\\"" + teamId
				+ "\\\", \\\"dScore\\\":\\\"" + dScore + "\\\", \\\"xScore\\\":\\\"" + xScore + "\\\"}";
	}
}
