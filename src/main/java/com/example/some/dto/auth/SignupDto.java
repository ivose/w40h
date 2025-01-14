package com.example.some.dto.auth;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SignupDto {
    private String email;
    private String password;
    private String fullName;
    private String username;
    private LocalDate born;
}
