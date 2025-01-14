package com.example.some.models.auth;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserSessionModel {
    private Long userId;
    private String sessionToken;
    private LocalDateTime lastActivity;
    private String ipAddress;
    private String userAgent;
    private boolean isValid;
}