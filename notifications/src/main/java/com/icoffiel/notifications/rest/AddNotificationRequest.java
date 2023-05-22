package com.icoffiel.notifications.rest;

public record AddNotificationRequest(
        String email,
        Long id,
        NotificationType type
) { }
