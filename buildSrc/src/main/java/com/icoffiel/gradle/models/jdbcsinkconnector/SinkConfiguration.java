package com.icoffiel.gradle.models.jdbcsinkconnector;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SinkConfiguration(
        @JsonProperty("connector.class")
        String connectorClass,
        @JsonProperty("topics")
        String topics,
        @JsonProperty("connection.password")
        String connectionPassword,
        @JsonProperty("connection.user")
        String connectionUser,
        @JsonProperty("connection.url")
        String connectionUrl,
        @JsonProperty("fields.whitelist")
        String fieldsWhitelist,
        @JsonProperty("table.name.format")
        String tableNameFormat,
        @JsonProperty("auto.evolve")
        String autoEvolve,
        @JsonProperty("auto.create")
        String autoCreate,
        @JsonProperty("batch.size")
        String batchSize,
        @JsonProperty("insert.mode")
        String insertMode,
        @JsonProperty("max.retries")
        String maxRetries,
        @JsonProperty("pk.mode")
        String pkMode,
        @JsonProperty("pk.fields")
        String pkFields,
        @JsonProperty("retry.backoff.ms")
        String retryBackoffMs,
        @JsonProperty("tasks.max")
        String tasksMax
) {
}
