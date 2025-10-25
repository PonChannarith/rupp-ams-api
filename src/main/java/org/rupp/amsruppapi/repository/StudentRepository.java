package org.rupp.amsruppapi.repository;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.LocalDateTimeTypeHandler;
import org.rupp.amsruppapi.model.entity.Student;

import java.util.List;
import java.util.Optional;

@Mapper
public interface StudentRepository {
    @Results(id = "studentResultMap", value = {
            @Result(property = "studentId", column = "student_id"),
            @Result(property = "studentNo", column = "student_no"),
            @Result(property = "studentCardId", column = "student_card_id"),
            @Result(property = "khmerName", column = "khmer_name"),
            @Result(property = "englishName", column = "english_name"),
            @Result(property = "gender", column = "gender"),
            @Result(property = "phoneNumber", column = "phone_number"),
            @Result(property = "dateOfBirth", column = "date_of_birth"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "createdAt", column = "created_at", typeHandler = LocalDateTimeTypeHandler.class),
            @Result(property = "updatedAt", column = "updated_at", typeHandler = LocalDateTimeTypeHandler.class)
    })
    @Select("SELECT * FROM students ORDER BY student_id ASC")
    List<Student> findAll();

    @ResultMap("studentResultMap")
    @Select("SELECT * FROM students WHERE student_id = #{id}")
    Optional<Student> findById(@Param("id") Long id);

    @ResultMap("studentResultMap")
    @Select("SELECT * FROM students WHERE student_no = #{studentNo}")
    Optional<Student> findByStudentNo(@Param("studentNo") String studentNo);

    @ResultMap("studentResultMap")
    @Select("SELECT * FROM students WHERE student_card_id = #{studentCardId}")
    Optional<Student> findByStudentCardId(@Param("studentCardId") String studentCardId);

    @ResultMap("studentResultMap")
    @Select("SELECT * FROM students WHERE user_id = #{userId}")
    Optional<Student> findByUserId(@Param("userId") Long userId);

    @ResultMap("studentResultMap")
    @Select("SELECT s.*, u.full_name, u.email " +
            "FROM students s " +
            "JOIN app_users u ON s.user_id = u.user_id " +
            "WHERE s.student_id = #{id}")
    Optional<Student> findByIdWithUser(@Param("id") Long id);

    @Insert("INSERT INTO students (student_no, student_card_id, khmer_name, english_name, gender, phone_number, date_of_birth, user_id) " +
            "VALUES (#{studentNo}, #{studentCardId}, #{khmerName}, #{englishName}, #{gender}, #{phoneNumber}, #{dateOfBirth}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "studentId")
    int insert(Student student);

    @Update("UPDATE students SET " +
            "student_no = #{studentNo}, " +
            "student_card_id = #{studentCardId}, " +
            "khmer_name = #{khmerName}, " +
            "english_name = #{englishName}, " +
            "gender = #{gender}, " +
            "phone_number = #{phoneNumber}, " +
            "date_of_birth = #{dateOfBirth}, " +
            "user_id = #{userId} " +
            "WHERE student_id = #{studentId}")
    int update(Student student);

    @Delete("DELETE FROM students WHERE student_id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM students WHERE student_no = #{studentNo} AND student_id != #{excludeId}")
    int countByStudentNoExcludingId(@Param("studentNo") String studentNo, @Param("excludeId") Long excludeId);

    @Select("SELECT COUNT(*) FROM students WHERE student_no = #{studentNo}")
    int countByStudentNo(@Param("studentNo") String studentNo);

    @Select("SELECT COUNT(*) FROM students WHERE student_card_id = #{studentCardId} AND student_id != #{excludeId}")
    int countByStudentCardIdExcludingId(@Param("studentCardId") String studentCardId, @Param("excludeId") Long excludeId);

    @Select("SELECT COUNT(*) FROM students WHERE student_card_id = #{studentCardId}")
    int countByStudentCardId(@Param("studentCardId") String studentCardId);

    @Select("SELECT COUNT(*) FROM students WHERE user_id = #{userId} AND student_id != #{excludeId}")
    int countByUserIdExcludingId(@Param("userId") Long userId, @Param("excludeId") Long excludeId);

    @Select("SELECT COUNT(*) FROM students WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);
}
