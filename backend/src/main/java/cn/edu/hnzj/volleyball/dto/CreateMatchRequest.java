package cn.edu.hnzj.volleyball.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateMatchRequest {

    @NotBlank(message = "请输入比赛名称")
    private String name;

    private String sponsor;

    private String rules;

    @NotNull(message = "请选择主队")
    private Long leftTeamId;

    @NotNull(message = "请选择客队")
    private Long rightTeamId;

    @Min(value = 1, message = "赛制至少为 1 局")
    @Max(value = 9, message = "赛制最多为 9 局")
    private Integer bestOf = 5;

    @Min(value = 5, message = "每局分数至少 5 分")
    @Max(value = 50, message = "每局分数最多 50 分")
    private Integer pointsPerSet = 25;

    @Min(value = 5, message = "决胜局分数至少 5 分")
    @Max(value = 50, message = "决胜局分数最多 50 分")
    private Integer finalSetPoints = 15;
}
