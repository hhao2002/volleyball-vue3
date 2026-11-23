package cn.edu.hnzj.volleyball.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PointRequest {

    @NotNull(message = "请指定得分队伍")
    private Long teamId;

    private Long memberId;

    private String type = "ATTACK";
}
