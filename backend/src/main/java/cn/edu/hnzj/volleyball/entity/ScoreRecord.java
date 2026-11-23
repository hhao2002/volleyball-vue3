package cn.edu.hnzj.volleyball.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScoreRecord {

    public static final String TYPE_ATTACK = "ATTACK";
    public static final String TYPE_OUT = "OUT";

    private Long id;
    private Long matchId;
    private Integer seq;
    private Integer setNo;
    private Long teamId;
    private Long memberId;
    private String type;
    private Long createdBy;
    private LocalDateTime createdAt;

    private String teamName;
    private String memberName;
}
