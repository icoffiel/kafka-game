package com.icoffiel.gradle.models.jdbcsinkconnector;

public class Configuration {
    private String connectorName;
    private String topics;
    private String password;
    private String username;
    private String connectionUrl;
    private String fieldsWhitelist;
    private String tableNameFormat;

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getFieldsWhitelist() {
        return fieldsWhitelist;
    }

    public void setFieldsWhitelist(String fieldsWhitelist) {
        this.fieldsWhitelist = fieldsWhitelist;
    }

    public String getTableNameFormat() {
        return tableNameFormat;
    }

    public void setTableNameFormat(String tableNameFormat) {
        this.tableNameFormat = tableNameFormat;
    }
}
