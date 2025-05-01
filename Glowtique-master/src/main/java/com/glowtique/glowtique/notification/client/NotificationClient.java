package com.glowtique.glowtique.notification.client;

import com.glowtique.glowtique.notification.client.dto.Notification;
import com.glowtique.glowtique.notification.client.dto.NotificationPreference;
import com.glowtique.glowtique.notification.client.dto.NotificationRequest;
import com.glowtique.glowtique.notification.client.dto.UpsertNotificationPreference;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "notification-svc", url = "${notification-svc.base-url}")
public interface NotificationClient {
    @GetMapping("/preferences")
    ResponseEntity<NotificationPreference> getUserPreference(@RequestParam(name = "userId") UUID userId);

    @PostMapping("/preferences")
    ResponseEntity<Void> upsertNotificationPreference(@RequestBody UpsertNotificationPreference notificationPreference);

    @PostMapping
    ResponseEntity<Void> sendNotification(@RequestBody NotificationRequest notificationRequest);

    @PutMapping("/preferences")
    ResponseEntity<Void> updateNotificationPreference(@RequestParam("userId") UUID userId, @RequestParam("enabled") boolean enabled);

    @PutMapping
    ResponseEntity<Void> retryFailedNotifications(@RequestParam(name = "userId") UUID userId);

    @GetMapping
    ResponseEntity<List<Notification>> getNotificationHistory(@RequestParam(name = "userId")UUID userId);


}
