package com.icoffiel.gradle.models.jdbcsinkconnector;

public record AddSinkConnectorRequest(
    String name,
    SinkConfiguration config
) {
}
