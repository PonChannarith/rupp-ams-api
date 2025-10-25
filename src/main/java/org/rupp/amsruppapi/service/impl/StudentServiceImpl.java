package org.rupp.amsruppapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.rupp.amsruppapi.model.entity.Student;
import org.rupp.amsruppapi.repository.StudentRepository;
import org.rupp.amsruppapi.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));
    }

    @Override
    public Student findByStudentNo(String studentNo) {
        return studentRepository.findByStudentNo(studentNo)
                .orElseThrow(() -> new RuntimeException("Student not found with student number: " + studentNo));
    }

    @Override
    public Student findByStudentCardId(String studentCardId) {
        return studentRepository.findByStudentCardId(studentCardId)
                .orElseThrow(() -> new RuntimeException("Student not found with card ID: " + studentCardId));
    }

    @Override
    public Student findByUserId(Long userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student not found with user ID: " + userId));
    }

    @Override
    public Student create(Student student) {
        // Check if student number already exists
        if (student.getStudentNo() != null &&
                studentRepository.countByStudentNo(student.getStudentNo()) > 0) {
            throw new IllegalArgumentException("Student number already exists: " + student.getStudentNo());
        }

        // Check if student card ID already exists
        if (student.getStudentCardId() != null &&
                studentRepository.countByStudentCardId(student.getStudentCardId()) > 0) {
            throw new IllegalArgumentException("Student card ID already exists: " + student.getStudentCardId());
        }

        // Check if user ID is already assigned to another student
        if (student.getUserId() != null &&
                studentRepository.countByUserId(student.getUserId()) > 0) {
            throw new IllegalArgumentException("User ID is already assigned to another student: " + student.getUserId());
        }

        studentRepository.insert(student);
        return studentRepository.findById(student.getStudentId())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve created student"));
    }

    @Override
    public Student update(Long id, Student student) {
        // Fetch existing student
        Student existing = findById(id);

        // Check if student number is being changed and if it already exists
        if (student.getStudentNo() != null &&
                !student.getStudentNo().equals(existing.getStudentNo()) &&
                studentRepository.countByStudentNoExcludingId(student.getStudentNo(), id) > 0) {
            throw new IllegalArgumentException("Student number already exists: " + student.getStudentNo());
        }

        // Check if student card ID is being changed and if it already exists
        if (student.getStudentCardId() != null &&
                !student.getStudentCardId().equals(existing.getStudentCardId()) &&
                studentRepository.countByStudentCardIdExcludingId(student.getStudentCardId(), id) > 0) {
            throw new IllegalArgumentException("Student card ID already exists: " + student.getStudentCardId());
        }

        // Check if user ID is being changed and if it already exists
        if (student.getUserId() != null &&
                !student.getUserId().equals(existing.getUserId()) &&
                studentRepository.countByUserIdExcludingId(student.getUserId(), id) > 0) {
            throw new IllegalArgumentException("User ID is already assigned to another student: " + student.getUserId());
        }

        // Update fields
        existing.setStudentNo(student.getStudentNo());
        existing.setStudentCardId(student.getStudentCardId());
        existing.setKhmerName(student.getKhmerName());
        existing.setEnglishName(student.getEnglishName());
        existing.setGender(student.getGender());
        existing.setPhoneNumber(student.getPhoneNumber());
        existing.setDateOfBirth(student.getDateOfBirth());
        existing.setUserId(student.getUserId());

        int rows = studentRepository.update(existing);
        if (rows == 0) {
            throw new RuntimeException("Failed to update student with ID: " + id);
        }

        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve updated student"));
    }

    @Override
    public void deleteById(Long id) {
        // Check if student exists
        findById(id);
        studentRepository.deleteById(id);
    }
}