package com.icoffiel.gradle;

import com.icoffiel.gradle.tasks.SinkJDBCConnector;
import com.icoffiel.gradle.tasks.SourceJDBCConnector;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class KafkaConnectPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project
                .getTasks()
                .create(
                        "addJDBCSourceConnector",
                        SourceJDBCConnector.class,
                        task -> {
                            task.setGroup("Kafka Connect");
                            task.setDescription("Add JDBC source connector to Kafka Connect");
                        }
                );

        project
                .getTasks()
                .create(
                    "addJDBCSinkConnector",
                        SinkJDBCConnector.class,
                        task -> {
                            task.setGroup("Kafka Connect");
                            task.setDescription("Add JDBC sink connector to Kafka Connect");
                        }
                );
    }
}
