package cn.edu.hnzj.volleyball.mapper;

import cn.edu.hnzj.volleyball.entity.MatchSet;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MatchSetMapper {

    @Insert("INSERT INTO match_sets (match_id, set_no, left_points, right_points, winner_team_id) " +
            "VALUES (#{matchId}, #{setNo}, #{leftPoints}, #{rightPoints}, #{winnerTeamId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MatchSet set);

    @Select("SELECT * FROM match_sets WHERE match_id = #{matchId} ORDER BY set_no ASC")
    List<MatchSet> findByMatch(Long matchId);

    @Delete("DELETE FROM match_sets WHERE match_id = #{matchId}")
    int deleteByMatch(Long matchId);
}
