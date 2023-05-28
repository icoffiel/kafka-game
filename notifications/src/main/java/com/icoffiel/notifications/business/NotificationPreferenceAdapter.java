package com.icoffiel.notifications.business;

import com.icoffiel.notifications.db.NotificationPreferenceEntity;
import com.icoffiel.notifications.rest.AddNotificationRequest;
import com.icoffiel.notifications.rest.NotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class NotificationPreferenceAdapter {
    public NotificationPreferenceEntity toNotificationPreferenceEntity(AddNotificationRequest addNotificationRequest) {
        NotificationPreferenceEntity notificationPreferenceEntity = new NotificationPreferenceEntity();
        notificationPreferenceEntity.setEmail(addNotificationRequest.email());
        notificationPreferenceEntity.setSystemId(addNotificationRequest.id());
        return notificationPreferenceEntity;
    }

    public NotificationResponse toNotificationResponse(NotificationPreferenceEntity savedEntity) {
        return new NotificationResponse(
                savedEntity.getId(),
                savedEntity.getEmail(),
                savedEntity.getSystemId()
        );
    }
}
