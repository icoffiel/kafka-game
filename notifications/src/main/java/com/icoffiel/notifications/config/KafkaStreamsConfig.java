package com.icoffiel.notifications.config;

import com.icoffiel.avro.db.GameEntity;
import com.icoffiel.notifications.business.NotificationService;
import com.icoffiel.notifications.rest.NotificationResponse;
import io.confluent.kafka.streams.serdes.avro.PrimitiveAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {
    private final Logger logger = LoggerFactory.getLogger(KafkaStreamsConfig.class);

    private final NotificationService notificationService;

    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistryUrl;

    public KafkaStreamsConfig(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

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
        return builder
                .stream(
                        "games_sql_game_entity",
                        Consumed.with(longAvroSerde, gameEntitySerde)
                )
                .peek((key, value) -> logger.info("game stream peek: key={} value={}", key, value))

                // Rekey by our system id
                .selectKey((key, value) -> value.getSystemId())

                // map values to the email addresses that they should notify
                .flatMapValues(gameEntity -> notificationService
                        .getNotifications(gameEntity.getSystemId()).stream()
                        .map(NotificationResponse::email)
                        .collect(Collectors.toList())
                )
                .peek((key, value) -> logger.info("email stream peek: key={} value={}", key, value));

    }
}
