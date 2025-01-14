package com.example.some.dto.comments;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentResponseDTO {
    private Long id;
    private Long userId;
    private String username;
    private String content;
    private LocalDateTime createdAt;
}