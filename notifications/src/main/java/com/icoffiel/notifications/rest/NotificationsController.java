package com.icoffiel.notifications.rest;

import io.confluent.kafka.streams.serdes.avro.PrimitiveAvroSerde;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.slf4j.LoggerFactory.*;

@RestController
@RequestMapping("/notifications")
public class NotificationsController {

    private final Logger logger = getLogger(NotificationsController.class);

    private static final String TOPIC_NAME = "notifications";

    private final KafkaTemplate<UUID, AddNotificationRequest> kafkaTemplate;

    public NotificationsController(KafkaTemplate<UUID, AddNotificationRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/")
    public CompletableFuture<NotificationResult> sendNotification(@RequestBody AddNotificationRequest request) {
        return kafkaTemplate
                .send(TOPIC_NAME, UUID.randomUUID(), request)
                .thenApply(result -> {
                    logger.debug(
                            "key={}, partition={}, offset={}",
                            result.getProducerRecord().key(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset()
                    );
                    return new NotificationResult(result.getProducerRecord().key());
                });
    }

    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder
                .name(TOPIC_NAME)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public KStream<Long, String> sendNotificationStream(StreamsBuilder builder) {
        KStream<UUID, AddNotificationRequest> newNotificationsStream = builder.stream(
                TOPIC_NAME,
                Consumed.with(
                        Serdes.UUID(),
                        new JsonSerde<>(AddNotificationRequest.class)
                )
        );
        KStream<Long, String> newSystemsStream = builder.stream(
                "systems_sql_system_entity",
                Consumed.with(
                        new PrimitiveAvroSerde<>(),
                        Serdes.String()
                )
        );

        KStream<Long, AddNotificationRequest> newKey = newNotificationsStream
                .selectKey((key, value) -> value.id())
                .peek((key, value) -> logger.debug("Set System ID as the new key. key={}, value={}", key, value));

        KStream<Long, String> test = newKey
                .join(
                        newSystemsStream,
                        (addNotificationRequest, value2) -> addNotificationRequest.email(),
                        JoinWindows.ofTimeDifferenceWithNoGrace(Duration.of(10, ChronoUnit.SECONDS))
                );

        test.to("notification_results");

        return test;
    }

}