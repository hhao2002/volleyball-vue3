package cn.edu.hnzj.pojo;

import org.springframework.stereotype.Component;

/**
 * @ClassName: ApType
 * @Description: volleyball.ap_type表 申请类型信息
 * @author: HHaoAn
 * @Copyright: https://group.cnblogs.com/learning/
 * @date: 2022-06-27 11:10:20
 */
@Component
public class ApType {
	/**
	 * @Fields id : 自增属性
	 * @date: 2022-06-27 11:11:47
	 */
	private Integer id;
	/**
	 * @Fields description : 申请类型的描述
	 * @date: 2022-06-27 11:11:26
	 */
	private String description;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "ApType [id=" + id + ", description=" + description + "]";
	}
}
