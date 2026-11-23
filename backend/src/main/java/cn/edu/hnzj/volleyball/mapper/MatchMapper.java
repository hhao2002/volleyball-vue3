package cn.edu.hnzj.volleyball.mapper;

import cn.edu.hnzj.volleyball.entity.Match;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchMapper {

    @Select("SELECT * FROM matches WHERE id = #{id}")
    Match findById(Long id);

    @Select("SELECT * FROM matches ORDER BY id DESC")
    List<Match> findAll();

    @Select("SELECT * FROM matches WHERE creator_id = #{creatorId} ORDER BY id DESC")
    List<Match> findByCreator(Long creatorId);

    @Select("SELECT * FROM matches WHERE state = #{state} ORDER BY id DESC")
    List<Match> findByState(String state);

    @Select("SELECT * FROM matches WHERE state = 'FINISHED' ORDER BY end_time DESC, id DESC LIMIT #{limit}")
    List<Match> findFinished(int limit);

    @Insert("INSERT INTO matches (name, sponsor, rules, state, creator_id, left_team_id, right_team_id, " +
            "best_of, points_per_set, final_set_points, current_set, left_sets, right_sets, left_points, " +
            "right_points, event_seq) VALUES (#{name}, #{sponsor}, #{rules}, #{state}, #{creatorId}, " +
            "#{leftTeamId}, #{rightTeamId}, #{bestOf}, #{pointsPerSet}, #{finalSetPoints}, 1, 0, 0, 0, 0, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Match match);

    @Update("UPDATE matches SET state = 'ONGOING', start_time = #{startTime} WHERE id = #{id}")
    int start(@Param("id") Long id, @Param("startTime") LocalDateTime startTime);

    @Update("UPDATE matches SET current_set = #{currentSet}, left_sets = #{leftSets}, right_sets = #{rightSets}, " +
            "left_points = #{leftPoints}, right_points = #{rightPoints}, event_seq = #{eventSeq} WHERE id = #{id}")
    int updateLive(@Param("id") Long id, @Param("currentSet") int currentSet,
                   @Param("leftSets") int leftSets, @Param("rightSets") int rightSets,
                   @Param("leftPoints") int leftPoints, @Param("rightPoints") int rightPoints,
                   @Param("eventSeq") int eventSeq);

    @Update("UPDATE matches SET state = 'FINISHED', winner_team_id = #{winnerTeamId}, end_time = #{endTime}, " +
            "current_set = #{currentSet}, left_sets = #{leftSets}, right_sets = #{rightSets}, " +
            "left_points = #{leftPoints}, right_points = #{rightPoints}, event_seq = #{eventSeq} WHERE id = #{id}")
    int finish(@Param("id") Long id, @Param("winnerTeamId") Long winnerTeamId,
               @Param("endTime") LocalDateTime endTime, @Param("currentSet") int currentSet,
               @Param("leftSets") int leftSets, @Param("rightSets") int rightSets,
               @Param("leftPoints") int leftPoints, @Param("rightPoints") int rightPoints,
               @Param("eventSeq") int eventSeq);

    @Delete("DELETE FROM matches WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) FROM matches")
    long countAll();

    @Select("SELECT COUNT(*) FROM matches WHERE state = #{state}")
    long countByState(String state);

    @Select("SELECT COUNT(*) FROM matches WHERE creator_id = #{creatorId} AND state = 'ONGOING'")
    long countOngoingByCreator(Long creatorId);

    @Select("SELECT COUNT(*) FROM matches WHERE state = 'ONGOING' " +
            "AND (left_team_id = #{teamId} OR right_team_id = #{teamId})")
    long countOngoingByTeam(Long teamId);
}
