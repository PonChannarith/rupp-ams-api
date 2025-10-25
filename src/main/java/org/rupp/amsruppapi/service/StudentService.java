package org.rupp.amsruppapi.service;

import lombok.RequiredArgsConstructor;
import org.rupp.amsruppapi.model.entity.Student;
import org.springframework.stereotype.Service;

import java.util.List;


public interface StudentService {
    List<Student> findAll();
    Student findById(Long id);
    Student findByStudentNo(String studentNo);
    Student findByStudentCardId(String studentCardId);
    Student findByUserId(Long userId);
    Student create(Student student);
    Student update(Long id, Student student);
    void deleteById(Long id);
}
