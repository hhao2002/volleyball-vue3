package cn.edu.hnzj.volleyball.mapper;

import cn.edu.hnzj.volleyball.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

public interface UserMapper {

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Select("<script>SELECT * FROM users WHERE id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<User> findByIds(@Param("ids") List<Long> ids);

    @Insert("INSERT INTO users (username, password, nickname, email, role, status) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{email}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE users SET last_login_at = #{time} WHERE id = #{id}")
    int updateLastLogin(@Param("id") Long id, @Param("time") LocalDateTime time);

    @Update("UPDATE users SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Update("UPDATE users SET nickname = #{nickname}, email = #{email} WHERE id = #{id}")
    int updateProfile(@Param("id") Long id, @Param("nickname") String nickname, @Param("email") String email);

    @Update("UPDATE users SET role = #{role} WHERE id = #{id}")
    int updateRole(@Param("id") Long id, @Param("role") String role);

    @Update("UPDATE users SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Select("<script>" +
            "SELECT * FROM users WHERE 1 = 1 " +
            "<if test='keyword != null and !keyword.trim().isEmpty()'>" +
            " AND (username LIKE CONCAT('%', #{keyword}, '%') OR nickname LIKE CONCAT('%', #{keyword}, '%')" +
            " OR email LIKE CONCAT('%', #{keyword}, '%'))</if>" +
            "<if test='role != null and !role.trim().isEmpty()'> AND role = #{role}</if>" +
            " ORDER BY id ASC LIMIT #{limit} OFFSET #{offset}</script>")
    List<User> search(@Param("keyword") String keyword, @Param("role") String role,
                      @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM users WHERE 1 = 1 " +
            "<if test='keyword != null and !keyword.trim().isEmpty()'>" +
            " AND (username LIKE CONCAT('%', #{keyword}, '%') OR nickname LIKE CONCAT('%', #{keyword}, '%')" +
            " OR email LIKE CONCAT('%', #{keyword}, '%'))</if>" +
            "<if test='role != null and !role.trim().isEmpty()'> AND role = #{role}</if>" +
            "</script>")
    long countSearch(@Param("keyword") String keyword, @Param("role") String role);

    @Select("SELECT COUNT(*) FROM users")
    long countAll();

    @Select("SELECT COUNT(*) FROM users WHERE role = #{role}")
    long countByRole(String role);

    @Select("SELECT COUNT(*) FROM users WHERE status = 'ACTIVE'")
    long countActive();
}
