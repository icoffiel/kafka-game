package com.icoffiel.notifications.streams;

import com.icoffiel.avro.db.GameEntity;
import com.icoffiel.notifications.config.NotificationsTopicsConfig;
import com.icoffiel.notifications.rest.AddNotificationRequest;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.PrimitiveAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * See <a href="https://blog.jdriven.com/2019/12/kafka-streams-topologytestdriver-with-avro/">jdriven.com</a> for an
 * overview of the below testing with a mock schema registry
 */
@ExtendWith(MockitoExtension.class)
class NotificationStreamsTest {
    private static final String SCHEMA_REGISTRY_SCOPE = NotificationStreamsTest.class.getName();
    private static final String MOCK_SCHEMA_REGISTRY_URL = "mock://" + SCHEMA_REGISTRY_SCOPE;
    private TopologyTestDriver driver;
    private TestInputTopic<UUID, AddNotificationRequest> notificationsInputTopic;
    private TestInputTopic<Long, GameEntity> gameSqlInputTopic;
    private TestOutputTopic<Long, String> outputTopic;

    @BeforeEach
    void setUp() {
        StreamsBuilder builder = new StreamsBuilder();
        NotificationStreams notificationStreams = new NotificationStreams();

        // Create the Serdes
        PrimitiveAvroSerde<Long> longAvroSerde = longAvroSerde();
        SpecificAvroSerde<GameEntity> gameEntitySerde = gameEntitySerde();

        // Create the topology
        KStream<Long, String> gamesToEmailsStream = notificationStreams.gamesToEmailsBySystemId(
                builder,
                longAvroSerde,
                gameEntitySerde
        );

        // route the stream to a sample output topic since none are defined with the topology setup
        gamesToEmailsStream.to("output_topic", Produced.with(Serdes.Long(), Serdes.String()));

        // Create the test driver with the topology
        Properties config = new Properties();
        config.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, MOCK_SCHEMA_REGISTRY_URL);
        driver = new TopologyTestDriver(builder.build(), config);

        // Create the input topics
        notificationsInputTopic = driver.createInputTopic(
                NotificationsTopicsConfig.TOPIC_NAME,
                Serdes.UUID().serializer(),
                new JsonSerde<>(AddNotificationRequest.class).serializer()
        );

        gameSqlInputTopic = driver.createInputTopic(
            "games_sql_game_entity",
                longAvroSerde.serializer(),
                gameEntitySerde.serializer()
        );

        // Create the output topic
        outputTopic = driver.createOutputTopic(
                "output_topic",
                Serdes.Long().deserializer(),
                Serdes.String().deserializer()
        );
    }

    @AfterEach
    void tearDown() {
        driver.close();
    }

    @Test
    void notification_Success() {
        AddNotificationRequest gameOneNotification = new AddNotificationRequest(
                "test@example.com",
                1L
        );
        notificationsInputTopic.pipeInput(
                UUID.randomUUID(),
                gameOneNotification
        );

        gameSqlInputTopic.pipeInput(
                1L,
                new GameEntity(
                    1L,
                        Instant.now(),
                        "game1 description",
                        Instant.now(),
                        "game1",
                        1,
                        9.99,
                        LocalDate.now(),
                        gameOneNotification.id()
                )
        );

        List<String> emails = outputTopic.readValuesToList();

        assertThat(emails, hasSize(1));
        assertThat(emails, Matchers.hasItem(gameOneNotification.email()));
    }

    @Test
    void multipleEmailForSameSystemNotifications_Success() {
        AddNotificationRequest gameOneNotification = new AddNotificationRequest(
                "test@example.com",
                1L
        );
        notificationsInputTopic.pipeInput(
                UUID.randomUUID(),
                gameOneNotification
        );

        AddNotificationRequest gameTwoNotification = new AddNotificationRequest(
                "test2@example.com",
                1L
        );
        notificationsInputTopic.pipeInput(
                UUID.randomUUID(),
                gameTwoNotification
        );

        gameSqlInputTopic.pipeInput(
                1L,
                new GameEntity(
                        1L,
                        Instant.now(),
                        "game1 description",
                        Instant.now(),
                        "game1",
                        1,
                        9.99,
                        LocalDate.now(),
                        gameOneNotification.id()
                )
        );

        List<String> emails = outputTopic.readValuesToList();

        assertThat(emails, hasSize(2));
        assertThat(emails, hasItem(gameOneNotification.email()));
        assertThat(emails, hasItem(gameTwoNotification.email()));
    }

    @Test
    void emailIsOnlyRegisteredOnceForSameSystemNotifications_Success() {
        AddNotificationRequest gameOneNotification = new AddNotificationRequest(
                "test@example.com",
                1L
        );
        notificationsInputTopic.pipeInput(
                UUID.randomUUID(),
                gameOneNotification
        );

        AddNotificationRequest gameTwoNotification = new AddNotificationRequest(
                "test@example.com",
                1L
        );
        notificationsInputTopic.pipeInput(
                UUID.randomUUID(),
                gameTwoNotification
        );

        gameSqlInputTopic.pipeInput(
                1L,
                new GameEntity(
                        1L,
                        Instant.now(),
                        "game1 description",
                        Instant.now(),
                        "game1",
                        1,
                        9.99,
                        LocalDate.now(),
                        gameOneNotification.id()
                )
        );

        List<String> emails = outputTopic.readValuesToList();

        assertThat(emails, hasSize(1));
        assertThat(emails, hasItem(gameOneNotification.email()));
    }

    @Test
    void nothingSentIfNoNoNotifications_Success() {
        AddNotificationRequest gameOneNotification = new AddNotificationRequest(
                "test@example.com",
                2L
        );
        notificationsInputTopic.pipeInput(
                UUID.randomUUID(),
                gameOneNotification
        );

        AddNotificationRequest gameTwoNotification = new AddNotificationRequest(
                "test@example.com",
                2L
        );
        notificationsInputTopic.pipeInput(
                UUID.randomUUID(),
                gameTwoNotification
        );

        gameSqlInputTopic.pipeInput(
                1L,
                new GameEntity(
                        1L,
                        Instant.now(),
                        "game1 description",
                        Instant.now(),
                        "game1",
                        1,
                        9.99,
                        LocalDate.now(),
                        1L
                )
        );

        List<String> emails = outputTopic.readValuesToList();

        assertThat(emails, empty());
    }

    private PrimitiveAvroSerde<Long> longAvroSerde() {
        final PrimitiveAvroSerde<Long> serde = new PrimitiveAvroSerde<>();
        Map<String, String> serdeConfig = Collections.singletonMap(
                AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                MOCK_SCHEMA_REGISTRY_URL
        );
        serde.configure(serdeConfig, true);
        return serde;
    }

    private SpecificAvroSerde<GameEntity> gameEntitySerde() {
        final SpecificAvroSerde<GameEntity> serde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = Collections.singletonMap(
                AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                MOCK_SCHEMA_REGISTRY_URL
        );
        serde.configure(serdeConfig, false);
        return serde;
    }
}