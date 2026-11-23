package cn.edu.hnzj.volleyball.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ApplicationRequest {

    @NotBlank(message = "请选择申请类型")
    private String type;

    @Size(max = 200, message = "申请理由最多 200 个字符")
    private String reason;
}
