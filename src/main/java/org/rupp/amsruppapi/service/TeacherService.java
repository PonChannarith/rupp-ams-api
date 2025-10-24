package org.rupp.amsruppapi.service;

import org.rupp.amsruppapi.model.entity.Teacher;

import java.util.List;

public interface TeacherService {
    List<Teacher> findAll();
    Teacher findById(Long id);
    Teacher findByEmployeeCode(String employeeCode);
    Teacher findByUserId(Long userId);
    List<Teacher> findByStatus(String status);
    Teacher create(Teacher teacher);
    Teacher update(Long id, Teacher teacher);
    Teacher updateStatus(Long id, String status);
    void deleteById(Long id);
}
