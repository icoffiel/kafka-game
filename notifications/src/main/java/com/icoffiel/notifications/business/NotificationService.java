package com.icoffiel.notifications.business;

import com.icoffiel.notifications.config.NotificationsTopicsConfig;
import com.icoffiel.notifications.rest.AddNotificationRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationService {

    private final KafkaTemplate<UUID, AddNotificationRequest> kafkaTemplate;

    public NotificationService(KafkaTemplate<UUID, AddNotificationRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<UUID> createNotification(AddNotificationRequest addNotificationRequest) {
        return kafkaTemplate
                .send(
                        NotificationsTopicsConfig.TOPIC_NAME,
                        UUID.randomUUID(),
                        addNotificationRequest
                )
                .thenApply(notificationResponse -> notificationResponse.getProducerRecord().key());

    }
}
