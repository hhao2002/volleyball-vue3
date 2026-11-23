package cn.edu.hnzj.volleyball.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class HandleApplicationRequest {

    @Size(max = 200, message = "处理备注最多 200 个字符")
    private String remark;
}
