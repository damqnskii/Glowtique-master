package com.glowtique.glowtique.notification.client.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Notification {
    private String subject;

    private LocalDateTime createdOn;

    private String body;

    private String status;

    private String type;
}
