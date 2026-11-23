package cn.edu.hnzj.volleyball.dto;

import cn.edu.hnzj.volleyball.entity.Match;
import cn.edu.hnzj.volleyball.entity.MatchSet;
import cn.edu.hnzj.volleyball.entity.ScoreRecord;
import cn.edu.hnzj.volleyball.entity.Team;
import cn.edu.hnzj.volleyball.entity.Vp;
import lombok.Data;

import java.util.List;

@Data
public class MatchDetailVO {

    private Match match;
    private String creatorName;
    private Team leftTeam;
    private Team rightTeam;
    private List<MatchSet> sets;
    private List<ScoreRecord> events;
    private List<Vp> vps;
    private String mvpName;
    private String svpName;
}
