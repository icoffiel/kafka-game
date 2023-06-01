package com.icoffiel.gradle.models.jdbcsourceconnector;

public record AddConnectorRequest(
        String name,
        Config config
) {
}

