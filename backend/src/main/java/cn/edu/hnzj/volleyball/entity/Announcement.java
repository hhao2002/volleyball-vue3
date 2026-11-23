package cn.edu.hnzj.volleyball.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Announcement {

    public static final String TYPE_MANUAL = "MANUAL";
    public static final String TYPE_MATCH_START = "MATCH_START";
    public static final String TYPE_MATCH_END = "MATCH_END";

    private Long id;
    private String title;
    private String content;
    private String type;
    private Long matchId;
    private Integer pinned;
    private Long createdBy;
    private LocalDateTime createdAt;
}
