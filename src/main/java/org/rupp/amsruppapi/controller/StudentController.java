package org.rupp.amsruppapi.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.rupp.amsruppapi.model.dto.ApiResponse;
import org.rupp.amsruppapi.model.entity.Student;
import org.rupp.amsruppapi.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/students")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    // Check if user has any of the specified roles
    private boolean hasAnyRole(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> List.of(roles).contains(role));
    }

    private boolean hasRole(String role) {
        return hasAnyRole(role);
    }

    // Helper method to get current user ID (you need to implement this based on your JWT)
    private Long getCurrentUserId() {
        // This should be extracted from JWT token
        // For now, return null - you'll need to implement this based on your authentication setup
        return null;
    }

    // 1. GET ALL STUDENTS - Admin and Teacher can read all, Student can only read their own
    @Operation(summary = "Get all students", description = "Retrieve a list of all students (Admin and Teacher can access all, Student can only access their own)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Student>>> getAllStudents() {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<List<Student>> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: No valid role");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        List<Student> students;
        if (hasRole("ROLE_STUDENT")) {
            // Students can only access their own profile
            Long currentUserId = getCurrentUserId();
            if (currentUserId != null) {
                try {
                    Student student = studentService.findByUserId(currentUserId);
                    students = List.of(student);
                } catch (RuntimeException e) {
                    students = List.of();
                }
            } else {
                students = List.of();
            }
        } else {
            // Admin and Teacher can access all students
            students = studentService.findAll();
        }

        ApiResponse<List<Student>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Students retrieved successfully");
        response.setPayload(students);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    //  GET STUDENT BY ID - Admin and Teacher can read any, Student can only read their own
    @Operation(summary = "Get student by ID", description = "Retrieve a single student by its ID (Admin and Teacher can access any, Student can only access their own)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> getStudentById(@PathVariable Long id) {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<Student> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: No valid role");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Student student = studentService.findById(id);

        // Students can only access their own profile
        if (hasRole("ROLE_STUDENT")) {
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null || !currentUserId.equals(student.getUserId())) {
                ApiResponse<Student> response = new ApiResponse<>();
                response.setSuccess(false);
                response.setMessage("Access denied: You can only view your own profile");
                response.setPayload(null);
                response.setLocalDateTime(LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }

        ApiResponse<Student> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Student retrieved successfully");
        response.setPayload(student);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    //  GET STUDENT BY STUDENT NUMBER - Admin and Teacher can read any, Student can only read their own
    @Operation(summary = "Get student by student number", description = "Retrieve a student by student number (Admin and Teacher can access any, Student can only access their own)")
    @GetMapping("/number/{studentNo}")
    public ResponseEntity<ApiResponse<Student>> getStudentByStudentNo(@PathVariable String studentNo) {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<Student> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: No valid role");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Student student = studentService.findByStudentNo(studentNo);

        // Students can only access their own profile
        if (hasRole("ROLE_STUDENT")) {
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null || !currentUserId.equals(student.getUserId())) {
                ApiResponse<Student> response = new ApiResponse<>();
                response.setSuccess(false);
                response.setMessage("Access denied: You can only view your own profile");
                response.setPayload(null);
                response.setLocalDateTime(LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }

        ApiResponse<Student> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Student retrieved successfully");
        response.setPayload(student);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    //  GET STUDENT BY USER ID - Admin and Teacher can read any, Student can only read their own
    @Operation(summary = "Get student by user ID", description = "Retrieve a student by user ID (Admin and Teacher can access any, Student can only access their own)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Student>> getStudentByUserId(@PathVariable Long userId) {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<Student> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: No valid role");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // Students can only access their own profile
        if (hasRole("ROLE_STUDENT")) {
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null || !currentUserId.equals(userId)) {
                ApiResponse<Student> response = new ApiResponse<>();
                response.setSuccess(false);
                response.setMessage("Access denied: You can only view your own profile");
                response.setPayload(null);
                response.setLocalDateTime(LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }

        Student student = studentService.findByUserId(userId);
        ApiResponse<Student> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Student retrieved successfully");
        response.setPayload(student);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    // CREATE STUDENT - Admin only
    @Operation(summary = "Create a new student", description = "Create a new student (Admin only)")
    @PostMapping
    public ResponseEntity<ApiResponse<Student>> createStudent(@RequestBody Student student) {
        if (!hasRole("ROLE_ADMIN")) {
            ApiResponse<Student> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Admin role required");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Student created = studentService.create(student);
        ApiResponse<Student> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Student created successfully");
        response.setPayload(created);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.status(CREATED).body(response);
    }

    //  UPDATE STUDENT - Admin only (Students can update their own profile through user profile)
    @Operation(summary = "Update student by ID", description = "Update student by ID (Admin only)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> updateStudent(
            @PathVariable Long id,
            @RequestBody Student student) {
        if (!hasRole("ROLE_ADMIN")) {
            ApiResponse<Student> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Admin role required");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Student updatedStudent = studentService.update(id, student);
        ApiResponse<Student> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Student updated successfully");
        response.setPayload(updatedStudent);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    //  DELETE STUDENT - Admin only
    @Operation(summary = "Delete student by ID", description = "Delete student by ID (Admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        if (!hasRole("ROLE_ADMIN")) {
            ApiResponse<Void> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Admin role required");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        studentService.deleteById(id);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Student deleted successfully");
        response.setPayload(null);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}