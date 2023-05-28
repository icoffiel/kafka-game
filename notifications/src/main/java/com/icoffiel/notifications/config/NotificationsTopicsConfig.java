package com.icoffiel.notifications.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class NotificationsTopicsConfig {

    public static final String TOPIC_NAME = "notifications";

    // TODO - This isn't being created for some reason
    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder
                .name(TOPIC_NAME)
                .partitions(3)
                .replicas(2)
                .build();
    }
}
