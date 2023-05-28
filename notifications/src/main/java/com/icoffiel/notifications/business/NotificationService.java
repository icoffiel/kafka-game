package com.icoffiel.notifications.business;

import com.icoffiel.notifications.db.NotificationPreferenceEntity;
import com.icoffiel.notifications.db.NotificationPreferenceRepository;
import com.icoffiel.notifications.rest.AddNotificationRequest;
import com.icoffiel.notifications.rest.NotificationResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final NotificationPreferenceAdapter notificationPreferenceAdapter;

    public NotificationService(
            NotificationPreferenceRepository notificationPreferenceRepository,
            NotificationPreferenceAdapter notificationPreferenceAdapter
    ) {
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.notificationPreferenceAdapter = notificationPreferenceAdapter;
    }

    public NotificationResponse createNotification(AddNotificationRequest addNotificationRequest) {
        NotificationPreferenceEntity entity = notificationPreferenceAdapter.toNotificationPreferenceEntity(addNotificationRequest);
        NotificationPreferenceEntity savedEntity = notificationPreferenceRepository.save(entity);
        return notificationPreferenceAdapter.toNotificationResponse(savedEntity);

    }

    public List<NotificationResponse> getNotifications(Long systemId) {
        return notificationPreferenceRepository
                .findAllBySystemId(systemId).stream()
                .map(notificationPreferenceAdapter::toNotificationResponse)
                .collect(Collectors.toList());
    }
}
