package org.rupp.amsruppapi.service;

import org.rupp.amsruppapi.model.entity.Class;
import java.util.List;

public interface ClassService {
    List<Class> findAll();
    Class findById(Long id);
    Class findByClassName(String className);
    List<Class> findByGradeLevel(String gradeLevel);
    List<Class> findByAcademicYear(String academicYear);
    List<Class> findByGradeLevelAndAcademicYear(String gradeLevel, String academicYear);
    Class create(Class classEntity);  // This should return your entity Class, not java.lang.Class
    Class update(Long id, Class classEntity);
    void deleteById(Long id);
}