package org.rupp.amsruppapi.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.rupp.amsruppapi.model.dto.ApiResponse;
import org.rupp.amsruppapi.model.entity.AppUserProfile;
import org.rupp.amsruppapi.service.AppUserProfileService;
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
@RequestMapping("/api/v1/user_profiles")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AppUserProfileController {
    private final AppUserProfileService appUserProfileService;

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

    @Operation(summary = "Get all user profiles", description = "Retrieve a list of all user profiles (Admin only)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AppUserProfile>>> getAllUserProfiles() {
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<List<AppUserProfile>>builder()
                            .success(false)
                            .message("Access denied: Admin role required")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        List<AppUserProfile> profiles = appUserProfileService.findAll();
        ApiResponse<List<AppUserProfile>> response = ApiResponse.<List<AppUserProfile>>builder()
                .success(true)
                .message("User profiles retrieved successfully")
                .payload(profiles)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get user profile by ID", description = "Retrieve a user profile by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppUserProfile>> getUserProfileById(@PathVariable Long id) {
        if (!isUserOrAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<AppUserProfile>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        AppUserProfile profile = appUserProfileService.findById(id);
        ApiResponse<AppUserProfile> response = ApiResponse.<AppUserProfile>builder()
                .success(true)
                .message("User profile retrieved successfully")
                .payload(profile)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get user profile by user ID", description = "Retrieve a user profile by app user ID")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<AppUserProfile>> getUserProfileByUserId(@PathVariable Long userId) {
        if (!isUserOrAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<AppUserProfile>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        AppUserProfile profile = appUserProfileService.findByAppUserId(userId);
        ApiResponse<AppUserProfile> response = ApiResponse.<AppUserProfile>builder()
                .success(true)
                .message("User profile retrieved successfully")
                .payload(profile)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a new user profile", description = "Create a new user profile")
    @PostMapping
    public ResponseEntity<ApiResponse<AppUserProfile>> createUserProfile(@RequestBody AppUserProfile appUserProfile) {
        if (!isUserOrAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<AppUserProfile>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        AppUserProfile created = appUserProfileService.create(appUserProfile);
        ApiResponse<AppUserProfile> response = ApiResponse.<AppUserProfile>builder()
                .success(true)
                .message("User profile created successfully")
                .payload(created)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.status(CREATED).body(response);
    }

    @Operation(summary = "Update user profile by ID", description = "Update user profile by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AppUserProfile>> updateUserProfile(
            @PathVariable Long id,
            @RequestBody AppUserProfile appUserProfile) {

        if (!isUserOrAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<AppUserProfile>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        AppUserProfile updatedProfile = appUserProfileService.update(id, appUserProfile);
        ApiResponse<AppUserProfile> response = ApiResponse.<AppUserProfile>builder()
                .success(true)
                .message("User profile updated successfully")
                .payload(updatedProfile)
                .localDateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete user profile by ID", description = "Delete user profile by ID (Admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserProfile(@PathVariable Long id) {
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Access denied: Admin role required")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        appUserProfileService.deleteById(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("User profile deleted successfully")
                .payload(null)
                .localDateTime(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}
