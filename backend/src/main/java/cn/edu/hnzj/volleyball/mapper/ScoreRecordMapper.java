package cn.edu.hnzj.volleyball.mapper;

import cn.edu.hnzj.volleyball.entity.ScoreRecord;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ScoreRecordMapper {

    @Insert("INSERT INTO score_records (match_id, seq, set_no, team_id, member_id, type, created_by) " +
            "VALUES (#{matchId}, #{seq}, #{setNo}, #{teamId}, #{memberId}, #{type}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ScoreRecord record);

    @Select("SELECT r.*, t.name AS team_name, m.name AS member_name FROM score_records r " +
            "LEFT JOIN teams t ON r.team_id = t.id " +
            "LEFT JOIN members m ON r.member_id = m.id " +
            "WHERE r.match_id = #{matchId} ORDER BY r.seq ASC")
    List<ScoreRecord> findByMatch(Long matchId);

    @Select("SELECT * FROM score_records WHERE match_id = #{matchId} ORDER BY seq DESC LIMIT 1")
    ScoreRecord findLast(Long matchId);

    @Delete("DELETE FROM score_records WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) FROM score_records WHERE match_id = #{matchId}")
    long countByMatch(Long matchId);

    @Delete("DELETE FROM score_records WHERE match_id = #{matchId}")
    int deleteByMatch(Long matchId);
}
