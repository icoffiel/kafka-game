package com.icoffiel.notifications.config;

import com.icoffiel.avro.db.GameEntity;
import io.confluent.kafka.streams.serdes.avro.PrimitiveAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.util.Collections;
import java.util.Map;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {
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
}
