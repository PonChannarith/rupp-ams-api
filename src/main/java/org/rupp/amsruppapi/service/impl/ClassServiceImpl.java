package org.rupp.amsruppapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.rupp.amsruppapi.model.entity.Class;
import org.rupp.amsruppapi.repository.ClassRepository;
import org.rupp.amsruppapi.service.ClassService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {
    private final ClassRepository classRepository;

    @Override
    public List<Class> findAll() {
        return classRepository.findAll();
    }

    @Override
    public Class findById(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with ID: " + id));
    }

    @Override
    public Class findByClassName(String className) {
        return classRepository.findByClassName(className)
                .orElseThrow(() -> new RuntimeException("Class not found with name: " + className));
    }

    @Override
    public List<Class> findByGradeLevel(String gradeLevel) {
        return classRepository.findByGradeLevel(gradeLevel);
    }

    @Override
    public List<Class> findByAcademicYear(String academicYear) {
        return classRepository.findByAcademicYear(academicYear);
    }

    @Override
    public List<Class> findByGradeLevelAndAcademicYear(String gradeLevel, String academicYear) {
        return classRepository.findByGradeLevelAndAcademicYear(gradeLevel, academicYear);
    }

    @Override
    public Class create(Class classEntity) {
        // Check if class name already exists
        if (classEntity.getClassName() != null &&
                classRepository.countByClassName(classEntity.getClassName()) > 0) {
            throw new IllegalArgumentException("Class name already exists: " + classEntity.getClassName());
        }

        classRepository.insert(classEntity);
        return classRepository.findById(classEntity.getClassId())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve created class"));
    }

    @Override
    public Class update(Long id, Class classEntity) {
        // Fetch existing class
        Class existing = findById(id);

        // Check if class name is being changed and if it already exists
        if (classEntity.getClassName() != null &&
                !classEntity.getClassName().equals(existing.getClassName()) &&
                classRepository.countByClassNameExcludingId(classEntity.getClassName(), id) > 0) {
            throw new IllegalArgumentException("Class name already exists: " + classEntity.getClassName());
        }

        // Update fields
        existing.setClassName(classEntity.getClassName());
        existing.setGradeLevel(classEntity.getGradeLevel());
        existing.setAcademicYear(classEntity.getAcademicYear());

        int rows = classRepository.update(existing);
        if (rows == 0) {
            throw new RuntimeException("Failed to update class with ID: " + id);
        }

        return classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve updated class"));
    }

    @Override
    public void deleteById(Long id) {
        // Check if class exists
        findById(id);
        classRepository.deleteById(id);
    }
}