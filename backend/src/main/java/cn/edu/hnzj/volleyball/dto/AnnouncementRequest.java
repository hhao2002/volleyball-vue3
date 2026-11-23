package cn.edu.hnzj.volleyball.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AnnouncementRequest {

    @NotBlank(message = "请输入公告标题")
    @Size(max = 100, message = "标题最多 100 个字符")
    private String title;

    private String content;

    private Boolean pinned = false;
}
