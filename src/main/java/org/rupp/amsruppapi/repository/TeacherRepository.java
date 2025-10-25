package org.rupp.amsruppapi.repository;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.LocalDateTimeTypeHandler;
import org.rupp.amsruppapi.model.entity.Teacher;

import java.util.List;

@Mapper
public interface TeacherRepository {
    @Results({
            @Result(property = "teacherId", column = "teacher_id"),
            @Result(property = "employeeCode", column = "employee_code"),
            @Result(property = "hireDate", column = "hire_date"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "createdAt", column = "created_at", typeHandler = LocalDateTimeTypeHandler.class),
            @Result(property = "updatedAt", column = "updated_at", typeHandler = LocalDateTimeTypeHandler.class)
    })
    @Select("SELECT * FROM teachers ORDER BY teacher_id ASC")
    List<Teacher> findAll();

    @Results({
            @Result(property = "teacherId", column = "teacher_id"),
            @Result(property = "employeeCode", column = "employee_code"),
            @Result(property = "hireDate", column = "hire_date"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "createdAt", column = "created_at", typeHandler = LocalDateTimeTypeHandler.class),
            @Result(property = "updatedAt", column = "updated_at", typeHandler = LocalDateTimeTypeHandler.class)
    })
    @Select("SELECT * FROM teachers WHERE teacher_id = #{id}")
    Teacher findById(@Param("id") Long id);

    @Results({
            @Result(property = "teacherId", column = "teacher_id"),
            @Result(property = "employeeCode", column = "employee_code"),
            @Result(property = "hireDate", column = "hire_date"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "createdAt", column = "created_at", typeHandler = LocalDateTimeTypeHandler.class),
            @Result(property = "updatedAt", column = "updated_at", typeHandler = LocalDateTimeTypeHandler.class)
    })
    @Select("SELECT * FROM teachers WHERE employee_code = #{employeeCode}")
    Teacher findByEmployeeCode(@Param("employeeCode") String employeeCode);

    @Results({
            @Result(property = "teacherId", column = "teacher_id"),
            @Result(property = "employeeCode", column = "employee_code"),
            @Result(property = "hireDate", column = "hire_date"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "createdAt", column = "created_at", typeHandler = LocalDateTimeTypeHandler.class),
            @Result(property = "updatedAt", column = "updated_at", typeHandler = LocalDateTimeTypeHandler.class)
    })
    @Select("SELECT * FROM teachers WHERE user_id = #{userId}")
    Teacher findByUserId(@Param("userId") Long userId);

    @Results({
            @Result(property = "teacherId", column = "teacher_id"),
            @Result(property = "employeeCode", column = "employee_code"),
            @Result(property = "hireDate", column = "hire_date"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "createdAt", column = "created_at", typeHandler = LocalDateTimeTypeHandler.class),
            @Result(property = "updatedAt", column = "updated_at", typeHandler = LocalDateTimeTypeHandler.class)
    })
    @Select("SELECT * FROM teachers WHERE status = #{status}")
    List<Teacher> findByStatus(@Param("status") String status);

    @Insert("INSERT INTO teachers (employee_code, hire_date, status, user_id) " +
            "VALUES (#{employeeCode}, #{hireDate}, #{status}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "teacherId")
    int insert(Teacher teacher);

    @Update("UPDATE teachers SET " +
            "employee_code = #{employeeCode}, " +
            "hire_date = #{hireDate}, " +
            "status = #{status}, " +
            "user_id = #{userId}, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE teacher_id = #{teacherId}")
    int update(Teacher teacher);

    @Update("UPDATE teachers SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE teacher_id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Delete("DELETE FROM teachers WHERE teacher_id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM teachers WHERE employee_code = #{employeeCode} AND teacher_id != #{excludeId}")
    int countByEmployeeCodeExcludingId(@Param("employeeCode") String employeeCode, @Param("excludeId") Long excludeId);

    @Select("SELECT COUNT(*) FROM teachers WHERE employee_code = #{employeeCode}")
    int countByEmployeeCode(@Param("employeeCode") String employeeCode);

    @Select("SELECT COUNT(*) FROM teachers WHERE user_id = #{userId} AND teacher_id != #{excludeId}")
    int countByUserIdExcludingId(@Param("userId") Long userId, @Param("excludeId") Long excludeId);

    @Select("SELECT COUNT(*) FROM teachers WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);
}