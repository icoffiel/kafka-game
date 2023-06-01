package com.icoffiel.gradle.models.jdbcsourceconnector;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record Config(
        @JsonProperty("connector.class")
        String connectorClass,
        @JsonProperty("connection.url")
        String connectionUrl,
        @JsonProperty("connection.user")
        String connectionUser,
        @JsonProperty("connection.password")
        String connectionPassword,
        @JsonProperty("table.whitelist")
        String tableWhitelist,
        @JsonProperty("mode")
        String mode,
        @JsonProperty("incrementing.column.name")
        String incrementingColumnName,
        @JsonProperty("timestamp.column.name")
        String timestampColumnName,
        @JsonProperty("tasks.max")
        String tasksMax,
        @JsonProperty("topic.prefix")
        String topicPrefix,
        @JsonProperty("transforms")
        String transforms,
        @JsonAnyGetter
        @JsonAnySetter
        Map<String, String> transformsDefinitions
) {
}
