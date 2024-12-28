package com.example.some.controller;


import com.example.some.dto.*;
import com.example.some.entity.User;
import com.example.some.service.UserService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Operation(summary = "Register new user",
            description = "Creates a new user account with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully created",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(
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
        user.setClient(false);

        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(modelMapper.map(createdUser, UserDto.class));
    }

    @Operation(summary = "User login",
            description = "Authenticates user and returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = LoginRespDto.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginRespDto> login(
            @Parameter(description = "Login credentials", required = true)
            @RequestBody LoginReqDto request) {
        LoginRespDto response = userService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logged in user's data",
            description = "Returns JWT token and user's data",
            security = {@SecurityRequirement(name = "Bearer Token")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = LoginRespDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/me")  // Changed to GET since we're retrieving data
    public ResponseEntity<LoginRespDto> me(@AuthenticationPrincipal UserDetails userDetails) {
        LoginRespDto response = userService.me(userDetails);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Initiate password reset",
            description = "Sends password reset link to user's email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reset email sent successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Parameter(description = "User's email address", required = true)
            @RequestBody PasswordForgotDto request) {
        userService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reset password",
            description = "Resets user password using token received in email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully reset"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Parameter(description = "Password reset details with token", required = true)
            @RequestBody PasswordResetDto request) {
        userService.resetPassword(request.getToken(), request.getPassword());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change password",
            description = "Changes user password (requires authentication)",
            security = {@SecurityRequirement(name = "Bearer Token")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully changed"),
            @ApiResponse(responseCode = "400", description = "Invalid old password"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Old and new password", required = true)
            @RequestBody PasswordChangeDto request) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        userService.changePassword(user.getId(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update user profile",
            description = "Updates authenticated user's profile information",
            security = {@SecurityRequirement(name = "Bearer Token")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile successfully updated",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Updated user information", required = true)
            @RequestBody User updatedUser) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User updated = userService.updateProfile(user.getId(), updatedUser);
        return ResponseEntity.ok(modelMapper.map(updated, UserDto.class));
    }

}
