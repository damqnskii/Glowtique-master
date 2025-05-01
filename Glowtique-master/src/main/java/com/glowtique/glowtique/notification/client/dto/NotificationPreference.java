package com.glowtique.glowtique.notification.client.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationPreference {
    private String type;

    private boolean enabled;

    private String contactInfo;
}
