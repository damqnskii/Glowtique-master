package com.glowtique.glowtique.web;

import com.glowtique.glowtique.notification.client.dto.Notification;
import com.glowtique.glowtique.notification.client.dto.NotificationPreference;
import com.glowtique.glowtique.notification.service.NotificationService;
import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.glowtique.glowtique.user.model.User;

import java.util.List;
import java.util.UUID;

@Controller
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/notifications")
    public ModelAndView getNotificationsPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getUserById(authenticationMetadata.getUserId());

        NotificationPreference notificationPreference = notificationService.getNotificationPreference(authenticationMetadata.getUserId());
        List<Notification> notificationHistory = notificationService.getNotificationHistory(authenticationMetadata.getUserId());


        notificationHistory = notificationHistory.stream().limit(5).toList();

        ModelAndView modelAndView = new ModelAndView("notifications");
        modelAndView.addObject("user", user);
        modelAndView.addObject("notificationPreference", notificationPreference);
        modelAndView.addObject("notificationHistory", notificationHistory);

        return modelAndView;
    }
    @PutMapping("/notifications/user-preference")
    public String updateUserPreference(@RequestParam(name = "enabled") boolean enabled, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        notificationService.updateNotificationPreference(authenticationMetadata.getUserId(), enabled);

        return "redirect:/notifications";
    }
    @PutMapping("/notifications")
    public String retryFailedNotifications(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UUID userId = authenticationMetadata.getUserId();

        notificationService.retryFailed(userId);

        return "redirect:/notifications";
    }


}
