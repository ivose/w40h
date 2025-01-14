package com.example.some.dto.follows;

import com.example.some.dto.users.UserResponseDTO;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FollowResponseDTO {
    private Long id;
    private UserResponseDTO follower;
    private UserResponseDTO followee;
    private LocalDateTime createdAt;
}