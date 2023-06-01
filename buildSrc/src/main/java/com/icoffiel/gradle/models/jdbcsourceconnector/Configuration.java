package com.icoffiel.gradle.models.jdbcsourceconnector;

import org.gradle.api.tasks.Input;

import java.util.HashMap;
import java.util.Map;

public final class Configuration {
    private String connectorName;
    private String username;
    private String password;
    private String tableName;
    private String topicPrefix;
    private String transforms;
    private  Map<String, String> transformsDefinitions =  new HashMap<>();

    @Input
    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    @Input
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Input
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Input
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Input
    public String getTopicPrefix() {
        return topicPrefix;
    }

    public void setTopicPrefix(String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }

    @Input
    public String getTransforms() {
        return transforms;
    }

    public void setTransforms(String transforms) {
        this.transforms = transforms;
    }

    public Map<String, String> getTransformsDefinitions() {
        return transformsDefinitions;
    }

    public void setTransformsDefinitions(Map<String, String> transformsDefinitions) {
        this.transformsDefinitions = transformsDefinitions;
    }

    @Override
    public String toString() {
        return "Configuration[" +
               "name=" + connectorName + ", " +
               "username=" + username + ", " +
               "password=" + password + ", " +
               "tableName=" + tableName + ", " +
               "topicPrefix=" + topicPrefix + ", " +
               "transforms=" + transforms + ", " +
               "transformsDefinitions=" + transformsDefinitions + ']';
    }

}
