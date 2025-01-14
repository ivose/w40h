package com.example.some.models.auth;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class UserCredentialsModel {
    private String username;
    private String hashedPassword;
    private String salt;
    private boolean isTemporaryPassword;
    private int failedLoginAttempts;
    private boolean isLocked;
}