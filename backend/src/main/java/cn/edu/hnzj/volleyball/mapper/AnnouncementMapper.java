package cn.edu.hnzj.volleyball.mapper;

import cn.edu.hnzj.volleyball.entity.Announcement;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AnnouncementMapper {

    @Select("SELECT * FROM announcements WHERE id = #{id}")
    Announcement findById(Long id);

    @Select("SELECT * FROM announcements ORDER BY pinned DESC, id DESC")
    List<Announcement> findAll();

    @Select("SELECT * FROM announcements ORDER BY pinned DESC, id DESC LIMIT #{limit}")
    List<Announcement> findLatest(int limit);

    @Insert("INSERT INTO announcements (title, content, type, match_id, pinned, created_by) " +
            "VALUES (#{title}, #{content}, #{type}, #{matchId}, #{pinned}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Announcement announcement);

    @Update("UPDATE announcements SET title = #{title}, content = #{content}, pinned = #{pinned} WHERE id = #{id}")
    int update(Announcement announcement);

    @Delete("DELETE FROM announcements WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) FROM announcements")
    long countAll();

    @Delete("DELETE FROM announcements WHERE match_id = #{matchId} AND type IN ('MATCH_START', 'MATCH_END')")
    int deleteAutoByMatch(Long matchId);
}
