package cn.edu.hnzj.volleyball.entity;

import lombok.Data;

@Data
public class Vp {

    public static final String MVP = "MVP";
    public static final String SVP = "SVP";

    private Long id;
    private Long matchId;
    private Long memberId;
    private Long teamId;
    private String type;
    private Integer points;
    private Integer maxStreak;
    private Integer streakCount;
    private String evaluate;

    private String memberName;
    private String teamName;
}
