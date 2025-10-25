package org.rupp.amsruppapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.rupp.amsruppapi.model.dto.ApiResponse;
import org.rupp.amsruppapi.model.entity.AppUser;
import org.rupp.amsruppapi.model.entity.Teacher;
import org.rupp.amsruppapi.model.request.TeacherRequest;
import org.rupp.amsruppapi.model.response.TeacherResponse;
import org.rupp.amsruppapi.repository.AppUserRepository;
import org.rupp.amsruppapi.service.TeacherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/teachers")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;
    private final AppUserRepository appUserRepository;

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

    // Get current user ID
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                AppUser appUser = appUserRepository.getUserByEmail(email);
                if (appUser != null) {
                    return appUser.getUserId();
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting current user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Get current teacher (if the current user is a teacher)
    private Teacher getCurrentTeacher() {
        try {
            Long currentUserId = getCurrentUserId();
            if (currentUserId != null) {
                return teacherService.findByUserId(currentUserId);
            }
        } catch (Exception e) {
            // Current user is not a teacher
        }
        return null;
    }

    // Convert Entity to Response DTO
    private TeacherResponse toResponse(Teacher teacher) {
        return new TeacherResponse(
                teacher.getTeacherId(),
                teacher.getEmployeeCode(),
                teacher.getHireDate(),
                teacher.getStatus(),
                teacher.getUserId(),
                teacher.getCreatedAt(),
                teacher.getUpdatedAt()
        );
    }

    // Convert Request DTO to Entity
    private Teacher toEntity(TeacherRequest request) {
        Teacher teacher = new Teacher();
        teacher.setEmployeeCode(request.getEmployeeCode());
        teacher.setHireDate(request.getHireDate());
        teacher.setStatus(request.getStatus());
        teacher.setUserId(request.getUserId());
        return teacher;
    }

    // 1. GET ALL TEACHERS - Role-based access
    @Operation(summary = "Get all teachers", description = "ADMIN(all), TEACHER(own info only), STUDENT(not allowed)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TeacherResponse>>> getAllTeachers() {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<List<TeacherResponse>> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: No valid role");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        List<Teacher> teachers;

        if (hasRole("ROLE_ADMIN")) {
            // ADMIN: Can see ALL teachers
            teachers = teacherService.findAll();
        } else if (hasRole("ROLE_TEACHER")) {
            // TEACHER: Can see ONLY their own information
            Teacher currentTeacher = getCurrentTeacher();
            if (currentTeacher != null) {
                teachers = List.of(currentTeacher);
            } else {
                ApiResponse<List<TeacherResponse>> response = new ApiResponse<>();
                response.setSuccess(false);
                response.setMessage("Access denied: Teacher profile not found");
                response.setPayload(null);
                response.setLocalDateTime(LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        } else {
            // STUDENT: Cannot access teacher information
            ApiResponse<List<TeacherResponse>> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Students cannot view teacher information");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        List<TeacherResponse> responseList = teachers.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        ApiResponse<List<TeacherResponse>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Teachers retrieved successfully");
        response.setPayload(responseList);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    // 2. GET TEACHER BY ID - Role-based access
    @Operation(summary = "Get teacher by ID", description = "ADMIN(any), TEACHER(own only), STUDENT(not allowed)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherResponse>> getTeacherById(@PathVariable Long id) {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<TeacherResponse> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Teacher teacher = teacherService.findById(id);

        // Role-based access control
        if (hasRole("ROLE_TEACHER")) {
            // TEACHER: Can only access their own information
            Teacher currentTeacher = getCurrentTeacher();
            if (currentTeacher == null || !currentTeacher.getTeacherId().equals(id)) {
                ApiResponse<TeacherResponse> response = new ApiResponse<>();
                response.setSuccess(false);
                response.setMessage("Access denied: You can only view your own information");
                response.setPayload(null);
                response.setLocalDateTime(LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        } else if (hasRole("ROLE_STUDENT")) {
            // STUDENT: Cannot access teacher information
            ApiResponse<TeacherResponse> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Students cannot view teacher information");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        TeacherResponse teacherResponse = toResponse(teacher);
        ApiResponse<TeacherResponse> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Teacher retrieved successfully");
        response.setPayload(teacherResponse);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    // 3. GET TEACHER BY EMPLOYEE CODE - Role-based access
    @Operation(summary = "Get teacher by employee code", description = "ADMIN(any), TEACHER(own only), STUDENT(not allowed)")
    @GetMapping("/employee-code/{employeeCode}")
    public ResponseEntity<ApiResponse<TeacherResponse>> getTeacherByEmployeeCode(@PathVariable String employeeCode) {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<TeacherResponse> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Teacher teacher = teacherService.findByEmployeeCode(employeeCode);

        // Role-based access control
        if (hasRole("ROLE_TEACHER")) {
            // TEACHER: Can only access their own information
            Teacher currentTeacher = getCurrentTeacher();
            if (currentTeacher == null || !currentTeacher.getEmployeeCode().equals(employeeCode)) {
                ApiResponse<TeacherResponse> response = new ApiResponse<>();
                response.setSuccess(false);
                response.setMessage("Access denied: You can only view your own information");
                response.setPayload(null);
                response.setLocalDateTime(LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        } else if (hasRole("ROLE_STUDENT")) {
            // STUDENT: Cannot access teacher information
            ApiResponse<TeacherResponse> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Students cannot view teacher information");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        TeacherResponse teacherResponse = toResponse(teacher);
        ApiResponse<TeacherResponse> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Teacher retrieved successfully");
        response.setPayload(teacherResponse);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    // 4. GET TEACHER BY USER ID - Role-based access
    @Operation(summary = "Get teacher by user ID", description = "ADMIN(any), TEACHER(own only), STUDENT(not allowed)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<TeacherResponse>> getTeacherByUserId(@PathVariable Long userId) {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<TeacherResponse> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // Role-based access control
        if (hasRole("ROLE_TEACHER")) {
            // TEACHER: Can only access their own information
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null || !currentUserId.equals(userId)) {
                ApiResponse<TeacherResponse> response = new ApiResponse<>();
                response.setSuccess(false);
                response.setMessage("Access denied: You can only view your own information");
                response.setPayload(null);
                response.setLocalDateTime(LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        } else if (hasRole("ROLE_STUDENT")) {
            // STUDENT: Cannot access teacher information
            ApiResponse<TeacherResponse> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Students cannot view teacher information");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Teacher teacher = teacherService.findByUserId(userId);
        TeacherResponse teacherResponse = toResponse(teacher);
        ApiResponse<TeacherResponse> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Teacher retrieved successfully");
        response.setPayload(teacherResponse);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    // 5. GET MY TEACHER PROFILE - Teachers can access their own profile
    @Operation(summary = "Get my teacher profile", description = "Get current teacher's own profile")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<TeacherResponse>> getMyTeacherProfile() {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER")) {
            ApiResponse<TeacherResponse> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Admin or Teacher role required");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Teacher currentTeacher = getCurrentTeacher();
        if (currentTeacher == null) {
            ApiResponse<TeacherResponse> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Teacher profile not found for current user");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        TeacherResponse teacherResponse = toResponse(currentTeacher);
        ApiResponse<TeacherResponse> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Teacher profile retrieved successfully");
        response.setPayload(teacherResponse);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    // 6. GET TEACHERS BY STATUS - Admin only
    @Operation(summary = "Get teachers by status", description = "Retrieve teachers by status (Admin only)")
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<TeacherResponse>>> getTeachersByStatus(@PathVariable String status) {
        if (!hasRole("ROLE_ADMIN")) {
            ApiResponse<List<TeacherResponse>> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Admin role required");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        List<Teacher> teachers = teacherService.findByStatus(status);
        List<TeacherResponse> responseList = teachers.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        ApiResponse<List<TeacherResponse>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Teachers retrieved successfully");
        response.setPayload(responseList);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    // 7. CREATE TEACHER - Admin only
    @Operation(summary = "Create a new teacher", description = "Create a new teacher (Admin only)")
    @PostMapping
    public ResponseEntity<ApiResponse<TeacherResponse>> createTeacher(@RequestBody TeacherRequest teacherRequest) {
        if (!hasRole("ROLE_ADMIN")) {
            ApiResponse<TeacherResponse> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Admin role required");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Teacher teacher = toEntity(teacherRequest);
        Teacher created = teacherService.create(teacher);
        TeacherResponse createdResponse = toResponse(created);

        ApiResponse<TeacherResponse> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Teacher created successfully");
        response.setPayload(createdResponse);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.status(CREATED).body(response);
    }

    // 8. UPDATE TEACHER BY ID - Role-based access
    @Operation(summary = "Update teacher by ID", description = "ADMIN(any), TEACHER(own only)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherResponse>> updateTeacher(
            @PathVariable Long id,
            @RequestBody TeacherRequest teacherRequest) {

        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER")) {
            ApiResponse<TeacherResponse> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Admin or Teacher role required");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // Check if user has permission to update this teacher
        Teacher existingTeacher = teacherService.findById(id);
        if (!hasRole("ROLE_ADMIN")) {
            // TEACHER: Can only update their own information
            Teacher currentTeacher = getCurrentTeacher();
            if (currentTeacher == null || !currentTeacher.getTeacherId().equals(id)) {
                ApiResponse<TeacherResponse> response = new ApiResponse<>();
                response.setSuccess(false);
                response.setMessage("Access denied: You can only update your own information");
                response.setPayload(null);
                response.setLocalDateTime(LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }

        Teacher teacher = toEntity(teacherRequest);
        Teacher updatedTeacher = teacherService.update(id, teacher);
        TeacherResponse updatedResponse = toResponse(updatedTeacher);

        ApiResponse<TeacherResponse> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Teacher updated successfully");
        response.setPayload(updatedResponse);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    // 9. UPDATE TEACHER STATUS - Admin only
    @Operation(summary = "Update teacher status", description = "Update teacher status (Admin only)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TeacherResponse>> updateTeacherStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        if (!hasRole("ROLE_ADMIN")) {
            ApiResponse<TeacherResponse> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Admin role required");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Teacher updatedTeacher = teacherService.updateStatus(id, status);
        TeacherResponse updatedResponse = toResponse(updatedTeacher);

        ApiResponse<TeacherResponse> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Teacher status updated successfully");
        response.setPayload(updatedResponse);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    // 10. DELETE TEACHER - Admin only
    @Operation(summary = "Delete teacher by ID", description = "Delete teacher by ID (Admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTeacher(@PathVariable Long id) {
        if (!hasRole("ROLE_ADMIN")) {
            ApiResponse<Void> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Admin role required");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        teacherService.deleteById(id);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Teacher deleted successfully");
        response.setPayload(null);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}