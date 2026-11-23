package cn.edu.hnzj.volleyball.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class TeamSaveRequest {

    @NotBlank(message = "请输入队伍名称")
    @Size(max = 30, message = "队伍名称最多 30 个字符")
    private String name;

    @Size(max = 30, message = "教练名称最多 30 个字符")
    private String coach;

    private String color;

    @NotNull(message = "请选择 6 名队员")
    @Size(min = 6, max = 6, message = "队伍必须有且仅有 6 名队员")
    private List<Long> memberIds;
}
