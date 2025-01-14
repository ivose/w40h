package com.example.some.models.notifications;

import lombok.Data;

@Data
public class NotificationSettingsModel {
    private Long userId;
    private boolean emailNotifications;
    private boolean pushNotifications;
    private boolean followNotifications;
    private boolean commentNotifications;
    private boolean reactionNotifications;
    private boolean messageNotifications;
}