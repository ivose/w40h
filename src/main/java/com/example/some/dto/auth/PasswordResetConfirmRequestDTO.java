package com.example.some.dto.auth;

import lombok.Data;

@Data
public class PasswordResetConfirmRequestDTO {
    private String newPassword;
    private String token;
}
