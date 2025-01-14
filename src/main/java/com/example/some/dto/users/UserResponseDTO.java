package com.example.some.dto.users;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String fullname;
    private LocalDate born;
    private boolean isActive;
    private LocalDateTime createdAt;
    private int followersCount;
    private int followingCount;
}