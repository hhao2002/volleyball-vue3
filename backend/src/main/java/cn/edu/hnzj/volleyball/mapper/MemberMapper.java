package cn.edu.hnzj.volleyball.mapper;

import cn.edu.hnzj.volleyball.entity.Member;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface MemberMapper {

    @Select("SELECT m.*, t.name AS team_name FROM members m LEFT JOIN teams t ON m.team_id = t.id WHERE m.id = #{id}")
    Member findById(Long id);

    @Select("SELECT * FROM members WHERE team_id = #{teamId} ORDER BY court_position ASC, id ASC")
    List<Member> findByTeam(Long teamId);

    @Select("SELECT * FROM members WHERE team_id IS NULL ORDER BY id ASC")
    List<Member> findAvailable();

    @Select("SELECT m.*, t.name AS team_name FROM members m LEFT JOIN teams t ON m.team_id = t.id ORDER BY m.id ASC")
    List<Member> findAll();

    @Select("<script>SELECT * FROM members WHERE id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<Member> findByIds(@Param("ids") List<Long> ids);

    @Insert("INSERT INTO members (team_id, name, role, number, court_position) " +
            "VALUES (#{teamId}, #{name}, #{role}, #{number}, #{courtPosition})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Member member);

    @Update("UPDATE members SET name = #{name}, role = #{role}, number = #{number} WHERE id = #{id}")
    int update(Member member);

    @Update("UPDATE members SET team_id = #{teamId}, court_position = #{courtPosition} WHERE id = #{id}")
    int assign(@Param("id") Long id, @Param("teamId") Long teamId, @Param("courtPosition") int courtPosition);

    @Update("UPDATE members SET team_id = NULL, court_position = NULL WHERE team_id = #{teamId}")
    int releaseTeam(Long teamId);

    @Delete("DELETE FROM members WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT (SELECT COUNT(*) FROM score_records WHERE member_id = #{id}) + " +
            "(SELECT COUNT(*) FROM vp WHERE member_id = #{id})")
    long countReferences(Long id);

    @Select("SELECT COUNT(*) FROM members WHERE team_id = #{teamId}")
    long countByTeam(Long teamId);

    @Select("SELECT COUNT(*) FROM members")
    long countAll();
}
