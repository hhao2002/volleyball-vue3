package cn.edu.hnzj.pojo;

/**
 * @ClassName: Member
 * @Description: volleyball.member表 的 封装对象
 * @author: HhaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-05-10 09:35:58
 */
public class Member {
	/**
	 * @Fields id : 自增属性
	 * @date: 2022-05-10 09:37:50
	 */
	private int id;
	/**
	 * @Fields teamId : 队伍id
	 * @date: 2022-05-10 09:38:10
	 */
	private int teamId;
	/**
	 * @Fields name : 队名
	 * @date: 2022-05-10 09:38:40
	 */
	private String name;
	/**
	 * @Fields position : 负责职位
	 * @date: 2022-05-10 09:38:48
	 */
	private String position;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTeamId() {
		return teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "Member [id=" + id + ", teamId=" + teamId + ", name=" + name + ", position=" + position + "]";
	}
}
