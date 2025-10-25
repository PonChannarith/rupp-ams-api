package org.rupp.amsruppapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.rupp.amsruppapi.model.dto.ApiResponse;
import org.rupp.amsruppapi.model.entity.Class;
import org.rupp.amsruppapi.service.ClassService;
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
@RequestMapping("/api/v1/classes")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ClassController {
    private final ClassService classService;

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

    //  GET ALL CLASSES - All roles can read
    @Operation(summary = "Get all classes", description = "Retrieve a list of all classes (All roles can access)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Class>>> getAllClasses() {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<List<Class>> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: No valid role");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        List<Class> classes = classService.findAll();
        ApiResponse<List<Class>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Classes retrieved successfully");
        response.setPayload(classes);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    // GET CLASS BY ID - All roles can read
    @Operation(summary = "Get class by ID", description = "Retrieve a single class by its ID (All roles can access)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Class>> getClassById(@PathVariable Long id) {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<Class> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: No valid role");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Class classEntity = classService.findById(id);
        ApiResponse<Class> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Class retrieved successfully");
        response.setPayload(classEntity);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    //  GET CLASS BY NAME - All roles can read
    @Operation(summary = "Get class by name", description = "Retrieve a class by its name (All roles can access)")
    @GetMapping("/name/{className}")
    public ResponseEntity<ApiResponse<Class>> getClassByName(@PathVariable String className) {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<Class> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: No valid role");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Class classEntity = classService.findByClassName(className);
        ApiResponse<Class> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Class retrieved successfully");
        response.setPayload(classEntity);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    //  GET CLASSES BY GRADE LEVEL - All roles can read
    @Operation(summary = "Get classes by grade level", description = "Retrieve classes by grade level (All roles can access)")
    @GetMapping("/grade/{gradeLevel}")
    public ResponseEntity<ApiResponse<List<Class>>> getClassesByGradeLevel(@PathVariable String gradeLevel) {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<List<Class>> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: No valid role");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        List<Class> classes = classService.findByGradeLevel(gradeLevel);
        ApiResponse<List<Class>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Classes retrieved successfully");
        response.setPayload(classes);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    //  GET CLASSES BY ACADEMIC YEAR - All roles can read
    @Operation(summary = "Get classes by academic year", description = "Retrieve classes by academic year (All roles can access)")
    @GetMapping("/year/{academicYear}")
    public ResponseEntity<ApiResponse<List<Class>>> getClassesByAcademicYear(@PathVariable String academicYear) {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<List<Class>> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: No valid role");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        List<Class> classes = classService.findByAcademicYear(academicYear);
        ApiResponse<List<Class>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Classes retrieved successfully");
        response.setPayload(classes);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    //  GET CLASSES BY GRADE LEVEL AND ACADEMIC YEAR - All roles can read
    @Operation(summary = "Get classes by grade level and academic year", description = "Retrieve classes by grade level and academic year (All roles can access)")
    @GetMapping("/grade/{gradeLevel}/year/{academicYear}")
    public ResponseEntity<ApiResponse<List<Class>>> getClassesByGradeLevelAndAcademicYear(
            @PathVariable String gradeLevel,
            @PathVariable String academicYear) {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<List<Class>> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: No valid role");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        List<Class> classes = classService.findByGradeLevelAndAcademicYear(gradeLevel, academicYear);
        ApiResponse<List<Class>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Classes retrieved successfully");
        response.setPayload(classes);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    //  CREATE CLASS - Admin only
    @Operation(summary = "Create a new class", description = "Create a new class (Admin only)")
    @PostMapping
    public ResponseEntity<ApiResponse<Class>> createClass(@RequestBody Class classEntity) {
        if (!hasRole("ROLE_ADMIN")) {
            ApiResponse<Class> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Admin role required");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Class created = classService.create(classEntity);
        ApiResponse<Class> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Class created successfully");
        response.setPayload(created);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.status(CREATED).body(response);
    }

    //  UPDATE CLASS - Admin only
    @Operation(summary = "Update class by ID", description = "Update class by ID (Admin only)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Class>> updateClass(
            @PathVariable Long id,
            @RequestBody Class classEntity) {
        if (!hasRole("ROLE_ADMIN")) {
            ApiResponse<Class> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Admin role required");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Class updatedClass = classService.update(id, classEntity);
        ApiResponse<Class> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Class updated successfully");
        response.setPayload(updatedClass);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    //  DELETE CLASS - Admin only
    @Operation(summary = "Delete class by ID", description = "Delete class by ID (Admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClass(@PathVariable Long id) {
        if (!hasRole("ROLE_ADMIN")) {
            ApiResponse<Void> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Admin role required");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        classService.deleteById(id);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Class deleted successfully");
        response.setPayload(null);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}