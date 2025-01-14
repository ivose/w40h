package com.example.some.models.users;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserActivityModel {
    private Long userId;
    private String activityType;
    private String description;
    private LocalDateTime timestamp;
    private String ipAddress;
    private String deviceInfo;
}