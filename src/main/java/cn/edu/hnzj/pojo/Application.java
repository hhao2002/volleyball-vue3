package cn.edu.hnzj.pojo;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName: Application
 * @ApInfo: volleyball.application表 申请信息
 * @author: HhaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-06-27 11:07:19
 */
@Component
public class Application {
	/**
	 * @Fields id : 自增属性
	 * @date: 2022-06-27 11:14:41
	 */
	private Integer id;
	/**
	 * @Fields apTypeId : 申请类型id
	 * @date: 2022-06-28 10:02:34
	 */
	private Integer apTypeId;
	/**
	 * @Fields apUserId : 申请人id
	 * @date: 2022-06-27 11:43:26
	 */
	private Integer apUserId;
	/**
	 * @Fields apUserName : 申请人用户名
	 * @date: 2023-04-24 10:10:23
	 */
	private String apUserName;
	/**
	 * @Fields dealUserName : 处理人用户名
	 * @date: 2023-04-24 10:10:59
	 */
	private String dealUserName;

	/**
	 * @Fields time : 申请发出的时间
	 * @date: 2022-06-27 11:16:13
	 */
	private Timestamp time;
	/**
	 * @Fields state : 申请状态 未处理|处理|通过|拒绝
	 * @date: 2022-06-27 11:16:25
	 */
	@Value("未处理")
	private String state;
	/**
	 * @Fields dealUserId : 处理人id
	 * @date: 2022-06-27 11:43:11
	 */
	private Integer dealUserId;
	/**
	 * @Fields apType : 申请类型
	 * @date: 2022-06-27 11:15:48
	 */
	private ApType apType;

	public String getApUserName() {
		return apUserName;
	}

	public void setApUserName(String apUserName) {
		this.apUserName = apUserName;
	}

	public String getDealUserName() {
		return dealUserName;
	}

	public void setDealUserName(String dealUserName) {
		this.dealUserName = dealUserName;
	}

	public Integer getApUserId() {
		return apUserId;
	}

	public void setApUserId(Integer apUserId) {
		this.apUserId = apUserId;
	}

	public Integer getApTypeId() {
		return apTypeId;
	}

	public void setApTypeId(Integer apTypeId) {
		this.apTypeId = apTypeId;
	}

	public Integer getDealUserId() {
		return dealUserId;
	}

	public void setDealUserId(Integer dealUserId) {
		this.dealUserId = dealUserId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ApType getApType() {
		return apType;
	}

	public void setApType(ApType apType) {
		this.apType = apType;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		if (state == null)
			this.state = "未处理";
		else
			this.state = state;
	}

	@Override
	public String toString() {
		return "{\\\"id\\\":\\\"" + id + "\\\", \\\"apTypeId\\\":\\\"" + apTypeId + "\\\", \\\"apUserId\\\":\\\""
				+ apUserId + "\\\", \\\"apUserName\\\":\\\"" + apUserName + "\\\", \\\"dealUserName\\\":\\\""
				+ dealUserName + "\\\", \\\"time\\\":\\\"" + time + "\\\", \\\"state\\\":\\\"" + state
				+ "\\\", \\\"dealUserId\\\":\\\"" + dealUserId + "\\\", \\\"apType\\\":\\\"" + apType + "\\\"}";
	}
}
