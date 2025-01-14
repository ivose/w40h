package com.example.some.models.posts;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostEngagementModel {
    private Long postId;
    private Long userId;
    private String engagementType;
    private LocalDateTime timestamp;
    private Long timeSpentSeconds;
    private boolean isFirstView;
}