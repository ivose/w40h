package com.example.some.controllers;


import com.example.some.dto.SuccessMessageDTO;
import com.example.some.dto.auth.*;
import com.example.some.entities.User;
import com.example.some.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthController {

    @Value("${app.frontend.url}")
    private String frontEndUrl;

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private ModelMapper modelMapper;

    @Operation(summary = "Register new user",
            description = "Creates a new user account with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully created",
                    content = @Content(schema = @Schema(implementation = UpdateProfileDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/signup")
    public ResponseEntity<UpdateProfileDTO> signup(
            @Parameter(description = "User registration details", required = true)
            @RequestBody SignupDto request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setBorn(request.getBorn());
        user.setActive(true);
        user.setAdmin(false);

        User createdUser = authService.createUser(user);
        return ResponseEntity.ok(modelMapper.map(createdUser, UpdateProfileDTO.class));
    }

    @GetMapping("/confirm-email")
    @Operation(summary = "Confirm email address",
            description = "Activates a user account after email confirmation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email successfully confirmed",
                    content = @Content(schema = @Schema(implementation = SuccessMessageDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> confirmEmail(@RequestParam String token) {
        authService.confirmEmail(token);
        //return ResponseEntity.ok(new SuccessMessageDTO("Email confirmed successfully. You can now log in."));
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(this.frontEndUrl + "/login")).build();
    }

    @Operation(summary = "User login",
            description = "Authenticates user and returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = UserLoginRequestDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(
            @Parameter(description = "Login credentials", required = true)
            @RequestBody UserLoginRequestDTO request) {
        UserLoginResponseDTO response = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logged in user's data",
            description = "Returns JWT token and user's data",
            security = {@SecurityRequirement(name = "Bearer Token")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = UserLoginResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/me")  // Changed to GET since we're retrieving data
    public ResponseEntity<UserLoginResponseDTO> me(@AuthenticationPrincipal UserDetails userDetails) {
        UserLoginResponseDTO response = authService.me(userDetails);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Initiate password reset",
            description = "Sends password reset link to user's email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reset email sent successfully",
                    content = @Content(schema = @Schema(implementation = SuccessMessageDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<SuccessMessageDTO> forgotPassword(
            @Parameter(description = "User's email address", required = true)
            @RequestBody PasswordResetRequestDTO request) {
        authService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok(new SuccessMessageDTO("Password reset email sent successfully"));
    }

    @Operation(summary = "Reset password",
            description = "Resets user password using token received in email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully reset",
                    content = @Content(schema = @Schema(implementation = SuccessMessageDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/reset-password")
    public ResponseEntity<SuccessMessageDTO> resetPassword(
            @Parameter(description = "Password reset details with token", required = true)
            @RequestBody PasswordResetConfirmRequestDTO request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(new SuccessMessageDTO("Password has been reset successfully"));
    }

    @Operation(summary = "Change password",
            description = "Changes user password (requires authentication)",
            security = {@SecurityRequirement(name = "Bearer Token")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully changed",
                    content = @Content(schema = @Schema(implementation = SuccessMessageDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid old password"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/change-password")
    public ResponseEntity<SuccessMessageDTO> changePassword(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Old and new password", required = true)
            @RequestBody PasswordChangeDto request) {
        User user = authService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        authService.changePassword(user.getId(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(new SuccessMessageDTO("Password changed successfully"));
    }

    @Operation(summary = "Update user profile",
            description = "Updates authenticated user's profile information",
            security = {@SecurityRequirement(name = "Bearer Token")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile successfully updated",
                    content = @Content(schema = @Schema(implementation = UpdateProfileDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/profile")
    public ResponseEntity<UpdateProfileDTO> updateProfile(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Updated user information", required = true)
            @RequestBody User updatedUser) {
        User user = authService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User updated = authService.updateProfile(user.getId(), updatedUser);
        return ResponseEntity.ok(modelMapper.map(updated, UpdateProfileDTO.class));
    }

}
