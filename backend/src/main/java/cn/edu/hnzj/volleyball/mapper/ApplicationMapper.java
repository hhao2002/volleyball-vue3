package cn.edu.hnzj.volleyball.mapper;

import cn.edu.hnzj.volleyball.entity.Application;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

public interface ApplicationMapper {

    @Insert("INSERT INTO applications (user_id, type, reason, status) " +
            "VALUES (#{userId}, #{type}, #{reason}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Application application);

    @Select("SELECT * FROM applications WHERE id = #{id}")
    Application findById(Long id);

    @Select("SELECT a.*, u.username AS applicant_username, u.nickname AS applicant_nickname, " +
            "h.nickname AS handler_name FROM applications a " +
            "LEFT JOIN users u ON a.user_id = u.id " +
            "LEFT JOIN users h ON a.handler_id = h.id " +
            "WHERE a.user_id = #{userId} ORDER BY a.id DESC")
    List<Application> findByUser(Long userId);

    @Select("<script>" +
            "SELECT a.*, u.username AS applicant_username, u.nickname AS applicant_nickname, " +
            "h.nickname AS handler_name FROM applications a " +
            "LEFT JOIN users u ON a.user_id = u.id " +
            "LEFT JOIN users h ON a.handler_id = h.id " +
            "WHERE 1 = 1 " +
            "<if test='status != null and !status.trim().isEmpty()'> AND a.status = #{status}</if>" +
            " ORDER BY a.id DESC LIMIT #{limit} OFFSET #{offset}</script>")
    List<Application> search(@Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM applications WHERE 1 = 1 " +
            "<if test='status != null and !status.trim().isEmpty()'> AND status = #{status}</if>" +
            "</script>")
    long countSearch(@Param("status") String status);

    @Select("SELECT COUNT(*) FROM applications WHERE user_id = #{userId} AND type = #{type} AND status = 'PENDING'")
    long countPending(@Param("userId") Long userId, @Param("type") String type);

    @Select("SELECT COUNT(*) FROM applications WHERE status = 'PENDING'")
    long countPendingAll();

    @Update("UPDATE applications SET status = #{status}, handler_id = #{handlerId}, " +
            "handle_remark = #{remark}, handled_at = #{handledAt} WHERE id = #{id}")
    int updateHandle(@Param("id") Long id, @Param("status") String status,
                     @Param("handlerId") Long handlerId, @Param("remark") String remark,
                     @Param("handledAt") LocalDateTime handledAt);
}
