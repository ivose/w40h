package com.example.some.dto.posts;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostResponseDTO {
    private Long id;
    private Long userId;
    private String username;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private int commentsCount;
    private int reactionsCount;
}