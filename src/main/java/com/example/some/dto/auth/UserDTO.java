package com.example.some.dto.auth;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String fullName;
    private String username;
    private LocalDate born;
    private boolean isActive;
    private boolean isAdmin;
}
