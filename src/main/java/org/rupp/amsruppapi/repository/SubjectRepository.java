package org.rupp.amsruppapi.repository;

import org.apache.ibatis.annotations.*;
import org.rupp.amsruppapi.model.entity.Subject;

import java.util.List;

@Mapper
public interface SubjectRepository {
    @Results({
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "subjectName", column = "subject_name"),
            @Result(property = "subjectDescription", column = "subject_description"),
            @Result(property = "groupLevel", column = "group_level"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("SELECT * FROM subjects ORDER BY subject_id ASC")
    List<Subject> findAll();

    @Results({
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "subjectName", column = "subject_name"),
            @Result(property = "subjectDescription", column = "subject_description"),
            @Result(property = "groupLevel", column = "group_level"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("SELECT * FROM subjects WHERE subject_id = #{id}")
    Subject findById(@Param("id") Long id);

    @Results({
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "subjectName", column = "subject_name"),
            @Result(property = "subjectDescription", column = "subject_description"),
            @Result(property = "groupLevel", column = "group_level")
    })
    @Select("SELECT * FROM subjects WHERE subject_name = #{subjectName}")
    Subject findBySubjectName(@Param("subjectName") String subjectName);

    @Results({
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "subjectName", column = "subject_name"),
            @Result(property = "subjectDescription", column = "subject_description"),
            @Result(property = "groupLevel", column = "group_level")
    })
    @Select("SELECT * FROM subjects WHERE group_level = #{groupLevel}")
    List<Subject> findByGroupLevel(@Param("groupLevel") String groupLevel);

    @Insert("INSERT INTO subjects (subject_name, subject_description, group_level) " +
            "VALUES (#{subjectName}, #{subjectDescription}, #{groupLevel})")
    @Options(useGeneratedKeys = true, keyProperty = "subjectId")
    int insert(Subject subject);

    @Update("UPDATE subjects SET " +
            "subject_name = #{subjectName}, " +
            "subject_description = #{subjectDescription}, " +
            "group_level = #{groupLevel}, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE subject_id = #{subjectId}")
    int update(Subject subject);

    @Delete("DELETE FROM subjects WHERE subject_id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM subjects WHERE subject_name = #{subjectName} AND subject_id != #{excludeId}")
    int countBySubjectNameExcludingId(@Param("subjectName") String subjectName, @Param("excludeId") Long excludeId);

    @Select("SELECT COUNT(*) FROM subjects WHERE subject_name = #{subjectName}")
    int countBySubjectName(@Param("subjectName") String subjectName);
}
