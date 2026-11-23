package cn.edu.hnzj.volleyball.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class MemberRequest {

    @NotBlank(message = "请输入队员姓名")
    @Size(max = 30, message = "姓名最多 30 个字符")
    private String name;

    @Size(max = 10, message = "位置最多 10 个字符")
    private String role = "主攻";

    @Min(value = 0, message = "号码不能为负数")
    @Max(value = 99, message = "号码最大为 99")
    private Integer number;
}
