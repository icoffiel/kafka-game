package com.icoffiel.notifications.rest;

import com.icoffiel.notifications.business.NotificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/notifications")
public class NotificationsController {
    private final NotificationService notificationService;

    public NotificationsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/")
    public CompletableFuture<UUID> sendNotification(@RequestBody AddNotificationRequest request) {
        return notificationService.createNotification(request);
    }
}
