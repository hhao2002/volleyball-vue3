package cn.edu.hnzj.volleyball.entity;

import lombok.Data;

@Data
public class MatchSet {

    private Long id;
    private Long matchId;
    private Integer setNo;
    private Integer leftPoints;
    private Integer rightPoints;
    private Long winnerTeamId;
}
