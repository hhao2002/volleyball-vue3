package cn.edu.hnzj.volleyball.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchSummaryVO {

    private Long id;
    private String name;
    private String state;
    private String rules;
    private String sponsor;
    private Long creatorId;
    private String creatorName;
    private Long leftTeamId;
    private String leftTeamName;
    private Long rightTeamId;
    private String rightTeamName;
    private Integer leftSets;
    private Integer rightSets;
    private Integer leftPoints;
    private Integer rightPoints;
    private Integer currentSet;
    private Integer bestOf;
    private Integer pointsPerSet;
    private Integer finalSetPoints;
    private Long winnerTeamId;
    private String winnerTeamName;
    private String mvpName;
    private String svpName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
}
