package cn.edu.hnzj.volleyball.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RoleUpdateRequest {

    @NotBlank(message = "请选择角色")
    private String role;
}
