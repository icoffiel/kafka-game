package com.icoffiel.notifications.rest;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.PrimitiveAvroSerde;
import org.apache.avro.SchemaBuilder;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationsControllerTest {

    private TopologyTestDriver driver;

    private TestInputTopic<UUID, AddNotificationRequest> notificationInputStream;
    private TestInputTopic<Long, String> newSystemsInputStream;

    private TestOutputTopic<Long, String> notificationsOutput;

    @Mock
    private KafkaTemplate<UUID, AddNotificationRequest> kafkaTemplate;

    private final JsonSerde<AddNotificationRequest> jsonSerde = new JsonSerde<>(AddNotificationRequest.class);
    private final Serde<UUID> uuidSerde = Serdes.UUID();
    private final Serde<Long> longSerde = Serdes.Long();
    private final Serde<String> stringSerde = Serdes.String();

    @BeforeEach
    void setUp() {
        StreamsBuilder builder = new StreamsBuilder();
        NotificationsController controller = new NotificationsController(kafkaTemplate);
        controller.sendNotificationStream(builder);

        SchemaBuilder
                .builder()
                .record("LongKey")
                .fields()
                .name("id").type().longType().noDefault()
                .endRecord();

        PrimitiveAvroSerde<Long> longKeySerde = new PrimitiveAvroSerde<>();
        longKeySerde.configure(Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://notneeded"), false);
        longKeySerde.serializer().configure(Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081"), false);
        longKeySerde.deserializer().configure(Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081"), false);

        Properties properties = new Properties();
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        driver = new TopologyTestDriver(builder.build(), properties);

        notificationInputStream = driver.createInputTopic(
                "notifications",
                uuidSerde.serializer(),
                jsonSerde.serializer()
        );

        newSystemsInputStream = driver.createInputTopic(
                "systems_sql_system_entity",
                longKeySerde.serializer(),
                stringSerde.serializer()
        );

        notificationsOutput = driver.createOutputTopic(
                "notifications-output",
                longSerde.deserializer(),
                stringSerde.deserializer()
        );
    }

    @AfterEach
    void tearDown() {
        driver.close();
    }

    @Test
    void sendNotificationStream() {
        newSystemsInputStream.pipeInput(1L, "TestSystem");
        notificationInputStream.pipeInput(
                UUID.randomUUID(),
                new AddNotificationRequest("TestMessage", 1L, NotificationType.SYSTEM)
        );

        notificationsOutput.readRecordsToList().forEach(System.out::println);
    }
}

//    // Create the mock schema registry client
//    MockSchemaRegistryClient mockSchemaRegistryClient = new MockSchemaRegistryClient();
//
//    // Configure the Avro serde with the mock schema registry client
//    Serde<MyRecord> myRecordSerde = new AvroSerde<>(mockSchemaRegistryClient);
//
//    // Create the input and output topics
//    TestInputTopic<String, MyRecord> inputTopic = testDriver.createInputTopic(INPUT_TOPIC_NAME, new StringSerializer(), myRecordSerde.serializer());
//    TestOutputTopic<String, MyRecord> outputTopic = testDriver.createOutputTopic(OUTPUT_TOPIC_NAME, new StringDeserializer(), myRecordSerde.deserializer());
//
//    // Create a new schema and register it with the mock schema registry client
//    Schema schema = SchemaBuilder.record("MyRecord")
//            .fields()
//            .name("value").type().intType().noDefault()
//            .endRecord();
//        mockSchemaRegistryClient.register("MyRecord", schema);
//
//                // Send a message with the new schema to the input topic
//                MyRecord myRecord = new MyRecord();
//                myRecord.setValue(123);
//                inputTopic.pipeInput("key", myRecord);
//
//                // Verify that the output topic contains the expected message
//                KeyValue<String, MyRecord> output = outputTopic.readKeyValue();
//        assertEquals("key", output.key);
//        assertEquals(myRecord, output.value);