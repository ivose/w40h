package com.example.some.dto.users;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserSearchDTO {
    private Long id;
    private String username;
    private String fullname;
    private LocalDate born;
    private boolean isActive;
    private LocalDateTime createdAt;
    private int followersCount;
    private int followingCount;

    public UserSearchDTO(Long id, String username, String fullname, LocalDate born,
                         boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.born = born;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.followersCount = 0;  // These will be populated by service layer
        this.followingCount = 0;
    }
}