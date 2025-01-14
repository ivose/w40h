package com.example.some.dto.auth;

import lombok.Data;

@Data
public class UserLoginResponseDTO {
    private String token;
    private UserDTO user;
}
