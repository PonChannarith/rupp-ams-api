package org.rupp.amsruppapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.rupp.amsruppapi.model.entity.Teacher;
import org.rupp.amsruppapi.repository.TeacherRepository;
import org.rupp.amsruppapi.service.TeacherService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;

    @Override
    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    @Override
    public Teacher findById(Long id) {
        Teacher teacher = teacherRepository.findById(id);
        if (teacher == null) {
            throw new RuntimeException("Teacher not found with ID: " + id);
        }
        return teacher;
    }

    @Override
    public Teacher findByEmployeeCode(String employeeCode) {
        Teacher teacher = teacherRepository.findByEmployeeCode(employeeCode);
        if (teacher == null) {
            throw new RuntimeException("Teacher not found with employee code: " + employeeCode);
        }
        return teacher;
    }

    @Override
    public Teacher findByUserId(Long userId) {
        Teacher teacher = teacherRepository.findByUserId(userId);
        if (teacher == null) {
            throw new RuntimeException("Teacher not found with user ID: " + userId);
        }
        return teacher;
    }

    @Override
    public List<Teacher> findByStatus(String status) {
        return teacherRepository.findByStatus(status);
    }

    @Override
    public Teacher create(Teacher teacher) {
        // Check if employee code already exists
        if (teacher.getEmployeeCode() != null &&
                teacherRepository.countByEmployeeCode(teacher.getEmployeeCode()) > 0) {
            throw new RuntimeException("Employee code already exists: " + teacher.getEmployeeCode());
        }

        // Check if user already has a teacher profile
        if (teacher.getUserId() != null &&
                teacherRepository.countByUserId(teacher.getUserId()) > 0) {
            throw new RuntimeException("Teacher profile already exists for user ID: " + teacher.getUserId());
        }

        // Set default status if not provided
        if (teacher.getStatus() == null) {
            teacher.setStatus("active");
        }

        teacherRepository.insert(teacher);
        return teacherRepository.findById(teacher.getTeacherId());
    }

    @Override
    public Teacher update(Long id, Teacher teacher) {
        // Fetch existing teacher
        Teacher existing = teacherRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("Teacher not found with ID: " + id);
        }

        // Check if employee code is being changed and if it already exists
        if (teacher.getEmployeeCode() != null &&
                !teacher.getEmployeeCode().equals(existing.getEmployeeCode()) &&
                teacherRepository.countByEmployeeCodeExcludingId(teacher.getEmployeeCode(), id) > 0) {
            throw new RuntimeException("Employee code already exists: " + teacher.getEmployeeCode());
        }

        // Check if user ID is being changed and if it already exists
        if (teacher.getUserId() != null &&
                !teacher.getUserId().equals(existing.getUserId()) &&
                teacherRepository.countByUserIdExcludingId(teacher.getUserId(), id) > 0) {
            throw new RuntimeException("Teacher profile already exists for user ID: " + teacher.getUserId());
        }

        // Update fields
        existing.setEmployeeCode(teacher.getEmployeeCode());
        existing.setHireDate(teacher.getHireDate());
        existing.setStatus(teacher.getStatus());
        existing.setUserId(teacher.getUserId());

        int rows = teacherRepository.update(existing);
        if (rows == 0) {
            throw new RuntimeException("Failed to update teacher with ID: " + id);
        }

        return teacherRepository.findById(id);
    }

    @Override
    public Teacher updateStatus(Long id, String status) {
        Teacher existing = teacherRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("Teacher not found with ID: " + id);
        }

        if (!isValidStatus(status)) {
            throw new RuntimeException("Invalid status: " + status);
        }

        int rows = teacherRepository.updateStatus(id, status);
        if (rows == 0) {
            throw new RuntimeException("Failed to update teacher status with ID: " + id);
        }

        return teacherRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        Teacher existing = teacherRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("Teacher not found with ID: " + id);
        }
        teacherRepository.deleteById(id);
    }

    private boolean isValidStatus(String status) {
        return status != null &&
                (status.equals("active") || status.equals("inactive") || status.equals("suspended"));
    }
}
