package org.rupp.amsruppapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.rupp.amsruppapi.model.dto.ApiResponse;
import org.rupp.amsruppapi.model.entity.AppUser;
import org.rupp.amsruppapi.model.entity.AppUserProfile;
import org.rupp.amsruppapi.repository.AppUserRepository;
import org.rupp.amsruppapi.service.AppUserProfileService;
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
@RequestMapping("/api/v1/user-profiles")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AppUserProfileController {

    private final AppUserProfileService appUserProfileService;
    private final AppUserRepository appUserRepository; // Add this dependency

    // ✅ Check if user has any of the specified roles
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

    // ✅ CORRECT implementation using your AppUserRepository
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName(); // This returns the email from JWT

                // Use your existing repository method to get user by email
                AppUser appUser = appUserRepository.getUserByEmail(email);
                if (appUser != null) {
                    return appUser.getUserId();
                }
            }
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error getting current user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ✅ 1. GET ALL PROFILES - Role-based access
    @Operation(summary = "Get all user profiles", description = "ADMIN(all), TEACHER(teachers+students), STUDENT(own only)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AppUserProfile>>> getAllUserProfiles() {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<List<AppUserProfile>>builder()
                            .success(false)
                            .message("Access denied: No valid role")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        List<AppUserProfile> profiles;

        if (hasRole("ROLE_ADMIN")) {
            // ADMIN: Can see ALL user profiles
            profiles = appUserProfileService.findAll();
        } else if (hasRole("ROLE_TEACHER")) {
            // TEACHER: Can see ONLY teacher and student profiles (not admin profiles)
            profiles = appUserProfileService.findAll().stream()
                    .filter(profile -> {
                        // Filter out admin profiles (card_id starts with "ADMIN")
                        return profile.getCardId() == null ||
                                !profile.getCardId().startsWith("ADMIN");
                    })
                    .collect(Collectors.toList());
        } else {
            // STUDENT: Can see ONLY their OWN profile
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.<List<AppUserProfile>>builder()
                                .success(false)
                                .message("Access denied: Cannot identify user")
                                .payload(null)
                                .localDateTime(LocalDateTime.now())
                                .build());
            }
            try {
                AppUserProfile profile = appUserProfileService.findByAppUserId(currentUserId);
                profiles = List.of(profile);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<List<AppUserProfile>>builder()
                                .success(false)
                                .message("Profile not found for current user")
                                .payload(null)
                                .localDateTime(LocalDateTime.now())
                                .build());
            }
        }

        return ResponseEntity.ok(ApiResponse.<List<AppUserProfile>>builder()
                .success(true)
                .message("User profiles retrieved successfully")
                .payload(profiles)
                .localDateTime(LocalDateTime.now())
                .build());
    }

    // ✅ 2. GET PROFILE BY ID - Role-based access
    @Operation(summary = "Get user profile by ID", description = "ADMIN(any), TEACHER(teachers+students), STUDENT(own only)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppUserProfile>> getUserProfileById(@PathVariable Long id) {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<AppUserProfile>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        AppUserProfile profile = appUserProfileService.findById(id);

        // Role-based access control
        if (hasRole("ROLE_TEACHER")) {
            // TEACHER: Can only access teacher/student profiles, not admin profiles
            if (profile.getCardId() != null && profile.getCardId().startsWith("ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.<AppUserProfile>builder()
                                .success(false)
                                .message("Access denied: Teachers cannot view admin profiles")
                                .payload(null)
                                .localDateTime(LocalDateTime.now())
                                .build());
            }
        } else if (hasRole("ROLE_STUDENT")) {
            // STUDENT: Can only access their own profile
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null || !currentUserId.equals(profile.getAppUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.<AppUserProfile>builder()
                                .success(false)
                                .message("Access denied: You can only view your own profile")
                                .payload(null)
                                .localDateTime(LocalDateTime.now())
                                .build());
            }
        }

        return ResponseEntity.ok(ApiResponse.<AppUserProfile>builder()
                .success(true)
                .message("User profile retrieved successfully")
                .payload(profile)
                .localDateTime(LocalDateTime.now())
                .build());
    }

    // ✅ 3. GET PROFILE BY USER ID - Role-based access
    @Operation(summary = "Get user profile by user ID", description = "ADMIN(any), TEACHER(teachers+students), STUDENT(own only)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<AppUserProfile>> getUserProfileByUserId(@PathVariable Long userId) {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<AppUserProfile>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        // Role-based access control
        if (hasRole("ROLE_TEACHER")) {
            // TEACHER: Check if the requested user is a teacher or student
            AppUserProfile targetProfile = appUserProfileService.findByAppUserId(userId);
            if (targetProfile.getCardId() != null && targetProfile.getCardId().startsWith("ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.<AppUserProfile>builder()
                                .success(false)
                                .message("Access denied: Teachers cannot view admin profiles")
                                .payload(null)
                                .localDateTime(LocalDateTime.now())
                                .build());
            }
        } else if (hasRole("ROLE_STUDENT")) {
            // STUDENT: Can only access their own profile
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null || !currentUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.<AppUserProfile>builder()
                                .success(false)
                                .message("Access denied: You can only view your own profile")
                                .payload(null)
                                .localDateTime(LocalDateTime.now())
                                .build());
            }
        }

        AppUserProfile profile = appUserProfileService.findByAppUserId(userId);
        return ResponseEntity.ok(ApiResponse.<AppUserProfile>builder()
                .success(true)
                .message("User profile retrieved successfully")
                .payload(profile)
                .localDateTime(LocalDateTime.now())
                .build());
    }

    // ✅ 4. GET MY PROFILE - All roles can access their own profile
    @Operation(summary = "Get my profile", description = "Get current user's own profile (all roles)")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AppUserProfile>> getMyProfile() {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<AppUserProfile>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<AppUserProfile>builder()
                            .success(false)
                            .message("Cannot identify current user")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        try {
            AppUserProfile profile = appUserProfileService.findByAppUserId(currentUserId);
            return ResponseEntity.ok(ApiResponse.<AppUserProfile>builder()
                    .success(true)
                    .message("User profile retrieved successfully")
                    .payload(profile)
                    .localDateTime(LocalDateTime.now())
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<AppUserProfile>builder()
                            .success(false)
                            .message("Profile not found for current user")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }
    }

    // ✅ 5. CREATE PROFILE - Role-based access
    @Operation(summary = "Create a new user profile", description = "ADMIN(any), TEACHER(own only), STUDENT(own only)")
    @PostMapping
    public ResponseEntity<ApiResponse<AppUserProfile>> createUserProfile(@RequestBody AppUserProfile appUserProfile) {
        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<AppUserProfile>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        // Role-based access control for creation
        if (!hasRole("ROLE_ADMIN")) {
            // TEACHER and STUDENT: Can only create their own profile
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null ||
                    appUserProfile.getAppUserId() == null ||
                    !currentUserId.equals(appUserProfile.getAppUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.<AppUserProfile>builder()
                                .success(false)
                                .message("Access denied: You can only create your own profile")
                                .payload(null)
                                .localDateTime(LocalDateTime.now())
                                .build());
            }
        }

        AppUserProfile created = appUserProfileService.create(appUserProfile);
        return ResponseEntity.status(CREATED).body(ApiResponse.<AppUserProfile>builder()
                .success(true)
                .message("User profile created successfully")
                .payload(created)
                .localDateTime(LocalDateTime.now())
                .build());
    }

    // ✅ 6. UPDATE PROFILE - Role-based access
    @Operation(summary = "Update user profile by ID", description = "ADMIN(any), TEACHER(own only), STUDENT(own only)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AppUserProfile>> updateUserProfile(
            @PathVariable Long id,
            @RequestBody AppUserProfile appUserProfile) {

        if (!hasAnyRole("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<AppUserProfile>builder()
                            .success(false)
                            .message("Access denied")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        // Check if user has permission to update this profile
        AppUserProfile existingProfile = appUserProfileService.findById(id);
        if (!hasRole("ROLE_ADMIN")) {
            // TEACHER and STUDENT: Can only update their own profile
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null || !currentUserId.equals(existingProfile.getAppUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.<AppUserProfile>builder()
                                .success(false)
                                .message("Access denied: You can only update your own profile")
                                .payload(null)
                                .localDateTime(LocalDateTime.now())
                                .build());
            }
        }

        AppUserProfile updatedProfile = appUserProfileService.update(id, appUserProfile);
        return ResponseEntity.ok(ApiResponse.<AppUserProfile>builder()
                .success(true)
                .message("User profile updated successfully")
                .payload(updatedProfile)
                .localDateTime(LocalDateTime.now())
                .build());
    }

    // ✅ 7. DELETE PROFILE - Admin only
    @Operation(summary = "Delete user profile by ID", description = "Delete user profile by ID (Admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserProfile(@PathVariable Long id) {
        if (!hasRole("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Access denied: Admin role required")
                            .payload(null)
                            .localDateTime(LocalDateTime.now())
                            .build());
        }

        appUserProfileService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("User profile deleted successfully")
                .payload(null)
                .localDateTime(LocalDateTime.now())
                .build());
    }
}