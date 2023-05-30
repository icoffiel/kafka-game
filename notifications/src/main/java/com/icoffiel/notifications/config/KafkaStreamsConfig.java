package com.icoffiel.notifications.config;

import com.icoffiel.avro.db.GameEntity;
import com.icoffiel.notifications.rest.AddNotificationRequest;
import io.confluent.kafka.streams.serdes.avro.PrimitiveAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {
    private final Logger logger = LoggerFactory.getLogger(KafkaStreamsConfig.class);

    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistryUrl;

    @Bean
    public PrimitiveAvroSerde<Long> longAvroSerde() {
        final PrimitiveAvroSerde<Long> serde = new PrimitiveAvroSerde<>();
        Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", schemaRegistryUrl);
        serde.configure(serdeConfig, true);
        return serde;
    }

    @Bean
    public SpecificAvroSerde<GameEntity> gameEntitySerde() {
        final SpecificAvroSerde<GameEntity> serde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", schemaRegistryUrl);
        serde.configure(serdeConfig, false);
        return serde;
    }

    @Bean
    public KStream<Long, String> gamesToEmailsBySystemId(
            StreamsBuilder builder,
            PrimitiveAvroSerde<Long> longAvroSerde,
            SpecificAvroSerde<GameEntity> gameEntitySerde
    ) {
        KTable<Long, List<AddNotificationRequest>> systemsWithNotifications = builder
                .stream(NotificationsTopicsConfig.TOPIC_NAME, Consumed.with(Serdes.UUID(), new JsonSerde<>(AddNotificationRequest.class)))
                .peek((key, value) -> logger.info("Topic {}, key={}, value={}", NotificationsTopicsConfig.TOPIC_NAME, key, value))
                .selectKey((key, value) -> value.id())
                .peek((key, value) -> logger.info("After selecting key, key={}, value={}", key, value))
                .groupByKey(
                        Grouped
                                .<Long, AddNotificationRequest>as("notifications-by-system-id")
                                .withKeySerde(Serdes.Long())
                                .withValueSerde(new JsonSerde<>(AddNotificationRequest.class)))
                .aggregate(
                        ArrayList::new,
                        (key, value, aggregate) -> {
                            logger.info("Recomputing the table for key={}, value={}", key, value);
                            if (!aggregate.contains(value)) {
                                aggregate.add(value);
                            }
                            return aggregate;
                        },
                        Materialized
                                .<Long, List<AddNotificationRequest>>as(Stores.inMemoryKeyValueStore("notification-table"))
                                .withKeySerde(Serdes.Long())
                                .withValueSerde(Serdes.ListSerde(ArrayList.class, new JsonSerde<>(AddNotificationRequest.class)))
                );

        KStream<Long, GameEntity> gamesStream = builder
                .stream(
                        "games_sql_game_entity",
                        Consumed.with(longAvroSerde, gameEntitySerde)
                )
                .selectKey((key, value) -> value.getSystemId());

        return gamesStream
                .join(
                        systemsWithNotifications,
                        (game, notificationsForSystem) -> notificationsForSystem.stream().map(AddNotificationRequest::email).toList(),
                        Joined
                                .<Long, GameEntity, List<AddNotificationRequest>>as("enriched-games")
                                .withKeySerde(Serdes.Long())
                                .withValueSerde(gameEntitySerde)
                                .withOtherValueSerde(Serdes.ListSerde(ArrayList.class, new JsonSerde<>(AddNotificationRequest.class)))
                )
                .peek((key, value) -> logger.info("Should send the following notifications: key={}, value={}",key , value))
                .flatMapValues((key, value) -> value)
                .peek((key, value) -> logger.info("Sending email, systemid={}, email={}", key, value));

    }
}
