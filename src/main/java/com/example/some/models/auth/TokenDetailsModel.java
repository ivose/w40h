package com.example.some.models.auth;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TokenDetailsModel {
    private String token;
    private Long userId;
    private String tokenType;
    private LocalDateTime expiryDate;
    private boolean isRevoked;
}