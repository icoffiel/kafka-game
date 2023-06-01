package com.icoffiel.gradle.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icoffiel.gradle.models.jdbcsourceconnector.AddConnectorRequest;
import com.icoffiel.gradle.models.jdbcsourceconnector.Config;
import com.icoffiel.gradle.models.jdbcsourceconnector.Configuration;
import okhttp3.*;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SourceJDBCConnector extends DefaultTask {
    private final List<Configuration> configurations = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final OkHttpClient client = new OkHttpClient();

    public void configuration(Action<Configuration> configurationAction) {
        Configuration configuration = new Configuration();
        configurationAction.execute(configuration);
        configurations.add(configuration);
    }

    @TaskAction
    public void run() {

        configurations.forEach(configuration -> {

            System.out.println("Creating connector: " + configuration.getConnectorName());

            AddConnectorRequest body = sourceRequestBody(
                    configuration.getConnectorName(),
                    configuration.getUsername(),
                    configuration.getPassword(),
                    configuration.getTableName(),
                    configuration.getTopicPrefix(),
                    configuration.getTransforms(),
                    configuration.getTransformsDefinitions()
            );

            String bodyString;
            try {
                bodyString = mapper.writeValueAsString(body);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            Request request = new Request.Builder()
                    .url("http://localhost:8083/connectors")
                    .post(RequestBody.create(bodyString, MediaType.get("application/json")))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                System.out.println(response.body().string());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private AddConnectorRequest sourceRequestBody(
            String connectorName,
            String username,
            String password,
            String tableName,
            String topicPrefix,
            String transforms,
            Map<String, String> transformsDefinitions
    ) {
        // TODO -  all these properties could probably be exposed instead of hardcoded but given default values
        return new AddConnectorRequest(
                connectorName,
                new Config(
                        "io.confluent.connect.jdbc.JdbcSourceConnector",
                        "jdbc:postgresql://postgres:5432/" + username,
                        username,
                        password,
                        tableName,
                        "timestamp+incrementing",
                        "id",
                        "modified_date",
                        "1",
                        topicPrefix,
                        transforms,
                        transformsDefinitions
                )
        );
    }
}
