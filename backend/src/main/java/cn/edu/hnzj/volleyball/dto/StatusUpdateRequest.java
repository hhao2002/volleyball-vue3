package cn.edu.hnzj.volleyball.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class StatusUpdateRequest {

    @NotBlank(message = "请选择状态")
    private String status;
}
