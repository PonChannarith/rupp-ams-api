package org.rupp.amsruppapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.rupp.amsruppapi.model.dto.ApiResponse;
import org.rupp.amsruppapi.model.entity.Subject;
import org.rupp.amsruppapi.service.SubjectService;
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
@RequestMapping("/api/v1/subjects")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;

    //  Check if user has any of the specified roles
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

    //  1. GET ALL SUBJECTS - All roles can read
    @Operation(summary = "Get all subjects", description = "Retrieve a list of all subjects (All roles can access)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Subject>>> getAllSubjects() {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<List<Subject>> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: No valid role");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        List<Subject> subjects = subjectService.findAll();
        ApiResponse<List<Subject>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Subjects retrieved successfully");
        response.setPayload(subjects);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    //  2. GET SUBJECT BY ID - All roles can read
    @Operation(summary = "Get subject by ID", description = "Retrieve a single subject by its ID (All roles can access)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Subject>> getSubjectById(@PathVariable Long id) {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<Subject> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: No valid role");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Subject subject = subjectService.findById(id);
        ApiResponse<Subject> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Subject retrieved successfully");
        response.setPayload(subject);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    //  3. GET SUBJECT BY NAME - All roles can read
    @Operation(summary = "Get subject by name", description = "Retrieve a subject by its name (All roles can access)")
    @GetMapping("/name/{subjectName}")
    public ResponseEntity<ApiResponse<Subject>> getSubjectByName(@PathVariable String subjectName) {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<Subject> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: No valid role");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Subject subject = subjectService.findBySubjectName(subjectName);
        ApiResponse<Subject> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Subject retrieved successfully");
        response.setPayload(subject);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    //  4. GET SUBJECTS BY GROUP LEVEL - All roles can read
    @Operation(summary = "Get subjects by group level", description = "Retrieve subjects by group level (All roles can access)")
    @GetMapping("/group/{groupLevel}")
    public ResponseEntity<ApiResponse<List<Subject>>> getSubjectsByGroupLevel(@PathVariable String groupLevel) {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            ApiResponse<List<Subject>> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: No valid role");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        List<Subject> subjects = subjectService.findByGroupLevel(groupLevel);
        ApiResponse<List<Subject>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Subjects retrieved successfully");
        response.setPayload(subjects);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    //  5. CREATE SUBJECT - Admin only
    @Operation(summary = "Create a new subject", description = "Create a new subject (Admin only)")
    @PostMapping
    public ResponseEntity<ApiResponse<Subject>> createSubject(@RequestBody Subject subject) {
        if (!hasRole("ROLE_ADMIN")) {
            ApiResponse<Subject> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Admin role required");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Subject created = subjectService.create(subject);
        ApiResponse<Subject> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Subject created successfully");
        response.setPayload(created);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.status(CREATED).body(response);
    }

    //  6. UPDATE SUBJECT - Admin only
    @Operation(summary = "Update subject by ID", description = "Update subject by ID (Admin only)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Subject>> updateSubject(
            @PathVariable Long id,
            @RequestBody Subject subject) {

        if (!hasRole("ROLE_ADMIN")) {
            ApiResponse<Subject> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Admin role required");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Subject updatedSubject = subjectService.update(id, subject);
        ApiResponse<Subject> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Subject updated successfully");
        response.setPayload(updatedSubject);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    //  7. DELETE SUBJECT - Admin only
    @Operation(summary = "Delete subject by ID", description = "Delete subject by ID (Admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubject(@PathVariable Long id) {
        if (!hasRole("ROLE_ADMIN")) {
            ApiResponse<Void> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Access denied: Admin role required");
            response.setPayload(null);
            response.setLocalDateTime(LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        subjectService.deleteById(id);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Subject deleted successfully");
        response.setPayload(null);
        response.setLocalDateTime(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}