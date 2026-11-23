package cn.edu.hnzj.volleyball.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Match {

    public static final String READY = "READY";
    public static final String ONGOING = "ONGOING";
    public static final String FINISHED = "FINISHED";

    private Long id;
    private String name;
    private String sponsor;
    private String rules;
    private String state;
    private Long creatorId;
    private Long leftTeamId;
    private Long rightTeamId;
    private Integer bestOf;
    private Integer pointsPerSet;
    private Integer finalSetPoints;
    private Integer currentSet;
    private Integer leftSets;
    private Integer rightSets;
    private Integer leftPoints;
    private Integer rightPoints;
    private Long winnerTeamId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private Integer eventSeq;
}
