package cn.edu.hnzj.volleyball.mapper;

import cn.edu.hnzj.volleyball.entity.LoginLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginLogMapper {

    @Insert("INSERT INTO login_logs (user_id, username, ip, user_agent, success, message) " +
            "VALUES (#{userId}, #{username}, #{ip}, #{userAgent}, #{success}, #{message})")
    int insert(LoginLog log);

    @Select("<script>" +
            "SELECT * FROM login_logs WHERE 1 = 1 " +
            "<if test='keyword != null and !keyword.trim().isEmpty()'>" +
            " AND (username LIKE CONCAT('%', #{keyword}, '%') OR ip LIKE CONCAT('%', #{keyword}, '%'))</if>" +
            " ORDER BY id DESC LIMIT #{limit} OFFSET #{offset}</script>")
    List<LoginLog> search(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM login_logs WHERE 1 = 1 " +
            "<if test='keyword != null and !keyword.trim().isEmpty()'>" +
            " AND (username LIKE CONCAT('%', #{keyword}, '%') OR ip LIKE CONCAT('%', #{keyword}, '%'))</if>" +
            "</script>")
    long countSearch(@Param("keyword") String keyword);

    @Select("SELECT COUNT(*) FROM login_logs WHERE success = 1 AND login_time >= #{start} AND login_time < #{end}")
    long countSuccessBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("SELECT COUNT(*) FROM login_logs")
    long countAll();
}
