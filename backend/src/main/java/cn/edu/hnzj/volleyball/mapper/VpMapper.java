package cn.edu.hnzj.volleyball.mapper;

import cn.edu.hnzj.volleyball.entity.Vp;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface VpMapper {

    @Insert("INSERT INTO vp (match_id, member_id, team_id, type, points, max_streak, streak_count, evaluate) " +
            "VALUES (#{matchId}, #{memberId}, #{teamId}, #{type}, #{points}, #{maxStreak}, #{streakCount}, #{evaluate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Vp vp);

    @Select("SELECT v.*, m.name AS member_name, t.name AS team_name FROM vp v " +
            "LEFT JOIN members m ON v.member_id = m.id " +
            "LEFT JOIN teams t ON v.team_id = t.id " +
            "WHERE v.match_id = #{matchId} ORDER BY v.type ASC")
    List<Vp> findByMatch(Long matchId);

    @Delete("DELETE FROM vp WHERE match_id = #{matchId}")
    int deleteByMatch(Long matchId);
}
