package com.example.some.dto.auth;

import lombok.Data;

@Data
public class UserLoginRequestDTO {
    private String username;
    private String password;
}