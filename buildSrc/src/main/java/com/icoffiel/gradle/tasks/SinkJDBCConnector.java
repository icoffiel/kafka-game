package com.icoffiel.gradle.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icoffiel.gradle.models.jdbcsinkconnector.AddSinkConnectorRequest;
import com.icoffiel.gradle.models.jdbcsinkconnector.Configuration;
import com.icoffiel.gradle.models.jdbcsinkconnector.SinkConfiguration;
import okhttp3.*;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SinkJDBCConnector extends DefaultTask {

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

            AddSinkConnectorRequest body = requestBody(
                    configuration.getConnectorName(),
                    configuration.getTopics(),
                    configuration.getPassword(),
                    configuration.getUsername(),
                    configuration.getConnectionUrl(),
                    configuration.getFieldsWhitelist(),
                    configuration.getTableNameFormat()
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

    private AddSinkConnectorRequest requestBody(
            String connectorName,
            String topics,
            String password,
            String user,
            String connectionUrl,
            String fieldsWhitelist,
            String tableNameFormat
    ) {
        return new AddSinkConnectorRequest(
                connectorName,
                new SinkConfiguration(
        "io.confluent.connect.jdbc.JdbcSinkConnector",
                        topics,
                        password,
                        user,
                        connectionUrl,
                        fieldsWhitelist,
                        tableNameFormat,
                        "false",
                        "false",
                        "3000",
                        "upsert",
                        "10",
                        "record_value",
                        "id",
                        "3000",
                        "1"
                )
        );
    }
}
