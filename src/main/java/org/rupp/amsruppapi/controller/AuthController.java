package org.rupp.amsruppapi.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.rupp.amsruppapi.jwt.JwtService;
import org.rupp.amsruppapi.model.entity.AppUser;
import org.rupp.amsruppapi.model.request.AppUserRequest;
import org.rupp.amsruppapi.model.request.AuthRequest;
import org.rupp.amsruppapi.model.response.ApiResponse;
import org.rupp.amsruppapi.model.response.AuthResponse;
import org.rupp.amsruppapi.service.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auths")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserService appUserService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        try {
            System.out.println("Login request received for: " + request.getEmail());

            authenticate(request.getEmail(), request.getPassword());

            final UserDetails userDetails = appUserService.loadUserByUsername(request.getEmail());
            System.out.println("User details loaded: " + userDetails.getUsername());

            final String token = jwtService.generateToken(userDetails);
            System.out.println("JWT token generated");

            AppUser appUser = appUserService.getUserByEmail(request.getEmail());
            AuthResponse authResponse = new AuthResponse(
                    token,
                    appUser.getEmail(),
                    appUser.getFullName(),
                    appUser.getRoles()
            );

            ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                    .success(true)
                    .message("Login successful")
                    .status(HttpStatus.OK)
                    .payload(authResponse)
                    .localDateTime(LocalDateTime.now())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<AuthResponse> errorResponse = ApiResponse.<AuthResponse>builder()
                    .success(false)
                    .message("Login failed: " + e.getMessage())
                    .status(HttpStatus.UNAUTHORIZED)
                    .localDateTime(LocalDateTime.now())
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

//    @PostMapping("/register")
//    public ResponseEntity<ApiResponse<?>> register(@RequestBody AppUserRequest request) {
//        var result = appUserService.register(request);
//        ApiResponse<Object> response = ApiResponse.builder()
//                .success(true)
//                .message("User registered successfully")
//                .status(HttpStatus.CREATED)
//                .payload(result)
//                .localDateTime(LocalDateTime.now())
//                .build();
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

}
