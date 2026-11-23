package cn.edu.hnzj.volleyball.mapper;

import cn.edu.hnzj.volleyball.entity.Team;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface TeamMapper {

    @Select("SELECT * FROM teams WHERE id = #{id}")
    Team findById(Long id);

    @Select("SELECT * FROM teams ORDER BY id ASC")
    List<Team> findAll();

    @Select("<script>SELECT * FROM teams WHERE id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<Team> findByIds(@Param("ids") List<Long> ids);

    @Insert("INSERT INTO teams (name, coach, color, created_by) VALUES (#{name}, #{coach}, #{color}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Team team);

    @Update("UPDATE teams SET name = #{name}, coach = #{coach}, color = #{color} WHERE id = #{id}")
    int update(Team team);

    @Delete("DELETE FROM teams WHERE id = #{id}")
    int deleteById(Long id);

    @Select("<script>SELECT COUNT(*) FROM teams WHERE name = #{name}" +
            "<if test='excludeId != null'> AND id != #{excludeId}</if></script>")
    long countByName(@Param("name") String name, @Param("excludeId") Long excludeId);

    @Select("SELECT COUNT(*) FROM matches WHERE (left_team_id = #{teamId} OR right_team_id = #{teamId})")
    long countMatches(Long teamId);

    @Select("SELECT COUNT(*) FROM matches WHERE state = 'ONGOING' " +
            "AND (left_team_id = #{teamId} OR right_team_id = #{teamId})")
    long countOngoingMatches(Long teamId);
}
