package org.rupp.amsruppapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.rupp.amsruppapi.model.dto.ApiResponse;
import org.rupp.amsruppapi.model.entity.Teacher;
import org.rupp.amsruppapi.service.TeacherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/teachers")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private boolean isUserOrAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||
                        authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Operation(summary = "Get all teachers", description = "Retrieve a list of all teachers")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Teacher>>> getAllTeachers() {
        if (!isUserOrAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<List<Teacher>>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        List<Teacher> teachers = teacherService.findAll();
        ApiResponse<List<Teacher>> response = ApiResponse.<List<Teacher>>builder()
                .success(true)
                .message("Teachers retrieved successfully")
                .payload(teachers)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get teacher by ID", description = "Retrieve a single teacher by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Teacher>> getTeacherById(@PathVariable Long id) {
        if (!isUserOrAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<Teacher>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        Teacher teacher = teacherService.findById(id);
        ApiResponse<Teacher> response = ApiResponse.<Teacher>builder()
                .success(true)
                .message("Teacher retrieved successfully")
                .payload(teacher)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get teacher by employee code", description = "Retrieve a teacher by employee code")
    @GetMapping("/employee-code/{employeeCode}")
    public ResponseEntity<ApiResponse<Teacher>> getTeacherByEmployeeCode(@PathVariable String employeeCode) {
        if (!isUserOrAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<Teacher>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        Teacher teacher = teacherService.findByEmployeeCode(employeeCode);
        ApiResponse<Teacher> response = ApiResponse.<Teacher>builder()
                .success(true)
                .message("Teacher retrieved successfully")
                .payload(teacher)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get teacher by user ID", description = "Retrieve a teacher by user ID")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Teacher>> getTeacherByUserId(@PathVariable Long userId) {
        if (!isUserOrAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<Teacher>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        Teacher teacher = teacherService.findByUserId(userId);
        ApiResponse<Teacher> response = ApiResponse.<Teacher>builder()
                .success(true)
                .message("Teacher retrieved successfully")
                .payload(teacher)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get teachers by status", description = "Retrieve teachers by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Teacher>>> getTeachersByStatus(@PathVariable String status) {
        if (!isUserOrAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<List<Teacher>>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        List<Teacher> teachers = teacherService.findByStatus(status);
        ApiResponse<List<Teacher>> response = ApiResponse.<List<Teacher>>builder()
                .success(true)
                .message("Teachers retrieved successfully")
                .payload(teachers)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a new teacher", description = "Create a new teacher. Accessible by admin only")
    @PostMapping
    public ResponseEntity<ApiResponse<Teacher>> createTeacher(@RequestBody Teacher teacher) {
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<Teacher>builder()
                            .success(false)
                            .message("Access denied: Admin role required")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        Teacher created = teacherService.create(teacher);
        ApiResponse<Teacher> response = ApiResponse.<Teacher>builder()
                .success(true)
                .message("Teacher created successfully")
                .payload(created)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.status(CREATED).body(response);
    }
    //Update teacher by Id
    @Operation(summary = "Update teacher by ID", description = "Update teacher by ID. Accessible by admin only")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Teacher>> updateTeacher(
            @PathVariable Long id,
            @RequestBody Teacher teacher) {

        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<Teacher>builder()
                            .success(false)
                            .message("Access denied: Admin role required")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        Teacher updatedTeacher = teacherService.update(id, teacher);
        ApiResponse<Teacher> response = ApiResponse.<Teacher>builder()
                .success(true)
                .message("Teacher updated successfully")
                .payload(updatedTeacher)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update teacher status", description = "Update teacher status. Accessible by admin only")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Teacher>> updateTeacherStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<Teacher>builder()
                            .success(false)
                            .message("Access denied: Admin role required")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        Teacher updatedTeacher = teacherService.updateStatus(id, status);
        ApiResponse<Teacher> response = ApiResponse.<Teacher>builder()
                .success(true)
                .message("Teacher status updated successfully")
                .payload(updatedTeacher)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete teacher by ID", description = "Delete teacher by ID. Accessible by admin only")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTeacher(@PathVariable Long id) {
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Access denied: Admin role required")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        teacherService.deleteById(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Teacher deleted successfully")
                .payload(null)
                .localDateTime(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}
