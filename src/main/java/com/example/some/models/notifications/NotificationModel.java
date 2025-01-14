package com.example.some.models.notifications;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationModel {
    private Long userId;
    private String type;
    private String message;
    private String link;
    private boolean isRead;
    private LocalDateTime createdAt;
    private Object payload;
}