package com.glowtique.glowtique.notification;

import com.glowtique.glowtique.notification.client.NotificationClient;
import com.glowtique.glowtique.notification.client.dto.Notification;
import com.glowtique.glowtique.notification.client.dto.NotificationRequest;
import com.glowtique.glowtique.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationUTest {
    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private NotificationService notificationService;

    private final UUID userId = UUID.randomUUID();
    private final String emailSubject = "Test Subject";
    private final String emailBody = "Test Body";

    @Test
    void sendNotification_SuccessfulCall_ShouldNotLogError() {

        ResponseEntity<Void> successResponse = ResponseEntity.ok().build();
        when(notificationClient.sendNotification(any(NotificationRequest.class))).thenReturn(successResponse);

        notificationService.sendNotification(userId, emailSubject, emailBody);

        verify(notificationClient, times(1)).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void sendNotification_FailedCall_ShouldLogError() {
        ResponseEntity<Void> failureResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(notificationClient.sendNotification(any(NotificationRequest.class))).thenReturn(failureResponse);

        notificationService.sendNotification(userId, emailSubject, emailBody);

        verify(notificationClient, times(1)).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void sendNotification_ExceptionThrown_ShouldLogWarning() {
        when(notificationClient.sendNotification(any(NotificationRequest.class)))
                .thenThrow(new RuntimeException("Feign Client Exception"));

        notificationService.sendNotification(userId, emailSubject, emailBody);

        verify(notificationClient, times(1)).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void getNotificationHistory_SuccessfulCall_ShouldReturnNotifications() {
        List<Notification> mockNotifications = List.of(
                new Notification("Welcome", LocalDateTime.now(), "send", "email"),
                new Notification("Welcome", LocalDateTime.now().minusDays(2), "send", "email")
        );

        ResponseEntity<List<Notification>> responseEntity = ResponseEntity.ok(mockNotifications);
        when(notificationClient.getNotificationHistory(userId)).thenReturn(responseEntity);

        List<Notification> result = notificationService.getNotificationHistory(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Welcome", result.get(0).getSubject());

        verify(notificationClient, times(1)).getNotificationHistory(userId);
    }

    @Test
    void getNotificationHistory_FailedCall_ShouldReturnNull() {
        when(notificationClient.getNotificationHistory(userId)).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        List<Notification> result = notificationService.getNotificationHistory(userId);

        assertNull(result);

        verify(notificationClient, times(1)).getNotificationHistory(userId);
    }

    @Test
    void updateNotificationPreference_ExceptionThrown_ShouldLogWarning() {
        doThrow(new RuntimeException("Feign Client Exception")).when(notificationClient).updateNotificationPreference(userId, true);

        notificationService.updateNotificationPreference(userId, true);

        verify(notificationClient, times(1)).updateNotificationPreference(userId, true);
    }






}
