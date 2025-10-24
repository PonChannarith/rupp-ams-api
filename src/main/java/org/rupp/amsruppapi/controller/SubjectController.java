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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    @Operation(summary = "Get all subjects", description = "Retrieve a list of all subjects")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Subject>>> getAllSubjects() {
        if (!isUserOrAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<List<Subject>>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        List<Subject> subjects = subjectService.findAll();
        ApiResponse<List<Subject>> response = ApiResponse.<List<Subject>>builder()
                .success(true)
                .message("Subjects retrieved successfully")
                .payload(subjects)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get subject by ID", description = "Retrieve a single subject by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Subject>> getSubjectById(@PathVariable Long id) {
        if (!isUserOrAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<Subject>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        Subject subject = subjectService.findById(id);
        ApiResponse<Subject> response = ApiResponse.<Subject>builder()
                .success(true)
                .message("Subject retrieved successfully")
                .payload(subject)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get subject by name", description = "Retrieve a subject by its name")
    @GetMapping("/name/{subjectName}")
    public ResponseEntity<ApiResponse<Subject>> getSubjectByName(@PathVariable String subjectName) {
        if (!isUserOrAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<Subject>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        Subject subject = subjectService.findBySubjectName(subjectName);
        ApiResponse<Subject> response = ApiResponse.<Subject>builder()
                .success(true)
                .message("Subject retrieved successfully")
                .payload(subject)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get subjects by group level", description = "Retrieve subjects by group level")
    @GetMapping("/group/{groupLevel}")
    public ResponseEntity<ApiResponse<List<Subject>>> getSubjectsByGroupLevel(@PathVariable String groupLevel) {
        if (!isUserOrAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<List<Subject>>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        List<Subject> subjects = subjectService.findByGroupLevel(groupLevel);
        ApiResponse<List<Subject>> response = ApiResponse.<List<Subject>>builder()
                .success(true)
                .message("Subjects retrieved successfully")
                .payload(subjects)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a new subject", description = "Create a new subject. Accessible by admin only")
    @PostMapping
    public ResponseEntity<ApiResponse<Subject>> createSubject(@RequestBody Subject subject) {
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<Subject>builder()
                            .success(false)
                            .message("Access denied: Admin role required")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        Subject created = subjectService.create(subject);
        ApiResponse<Subject> response = ApiResponse.<Subject>builder()
                .success(true)
                .message("Subject created successfully")
                .payload(created)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.status(CREATED).body(response);
    }

    @Operation(summary = "Update subject by ID", description = "Update subject by ID. Accessible by admin only")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Subject>> updateSubject(
            @PathVariable Long id,
            @RequestBody Subject subject) {

        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<Subject>builder()
                            .success(false)
                            .message("Access denied: Admin role required")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        Subject updatedSubject = subjectService.update(id, subject);
        ApiResponse<Subject> response = ApiResponse.<Subject>builder()
                .success(true)
                .message("Subject updated successfully")
                .payload(updatedSubject)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete subject by ID", description = "Delete subject by ID. Accessible by admin only")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubject(@PathVariable Long id) {
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Access denied: Admin role required")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        subjectService.deleteById(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Subject deleted successfully")
                .payload(null)
                .localDateTime(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}
