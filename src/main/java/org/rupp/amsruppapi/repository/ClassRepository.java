package org.rupp.amsruppapi.repository;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.LocalDateTimeTypeHandler;
import org.rupp.amsruppapi.model.entity.Class;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ClassRepository {

    @Results(id = "classResultMap", value = {
            @Result(property = "classId", column = "class_id"),
            @Result(property = "className", column = "class_name"),
            @Result(property = "gradeLevel", column = "grade_level"),
            @Result(property = "academicYear", column = "academic_year"),
            @Result(property = "createdAt", column = "created_at", typeHandler = LocalDateTimeTypeHandler.class),
            @Result(property = "lastModifiedAt", column = "last_modified_at", typeHandler = LocalDateTimeTypeHandler.class)
    })
    @Select("SELECT * FROM classes ORDER BY class_id ASC")
    List<Class> findAll();

    @ResultMap("classResultMap")
    @Select("SELECT * FROM classes WHERE class_id = #{id}")
    Optional<Class> findById(@Param("id") Long id);

    @ResultMap("classResultMap")
    @Select("SELECT * FROM classes WHERE class_name = #{className}")
    Optional<Class> findByClassName(@Param("className") String className);

    @ResultMap("classResultMap")
    @Select("SELECT * FROM classes WHERE grade_level = #{gradeLevel}")
    List<Class> findByGradeLevel(@Param("gradeLevel") String gradeLevel);

    @ResultMap("classResultMap")
    @Select("SELECT * FROM classes WHERE academic_year = #{academicYear}")
    List<Class> findByAcademicYear(@Param("academicYear") String academicYear);

    @ResultMap("classResultMap")
    @Select("SELECT * FROM classes WHERE grade_level = #{gradeLevel} AND academic_year = #{academicYear}")
    List<Class> findByGradeLevelAndAcademicYear(@Param("gradeLevel") String gradeLevel,
                                                @Param("academicYear") String academicYear);

    @Insert("INSERT INTO classes (class_name, grade_level, academic_year, created_at, last_modified_at) " +
            "VALUES (#{className}, #{gradeLevel}, #{academicYear}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "classId")
    int insert(Class classEntity);

    @Update("UPDATE classes SET " +
            "class_name = #{className}, " +
            "grade_level = #{gradeLevel}, " +
            "academic_year = #{academicYear}, " +
            "last_modified_at = CURRENT_TIMESTAMP " +
            "WHERE class_id = #{classId}")
    int update(Class classEntity);

    @Delete("DELETE FROM classes WHERE class_id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM classes WHERE class_name = #{className} AND class_id != #{excludeId}")
    int countByClassNameExcludingId(@Param("className") String className, @Param("excludeId") Long excludeId);

    @Select("SELECT COUNT(*) FROM classes WHERE class_name = #{className}")
    int countByClassName(@Param("className") String className);
}
