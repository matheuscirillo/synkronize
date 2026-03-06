package io.synkronize.scheduler.connector;

import io.synkronize.connector.source.spi.SourceConnector;
import io.synkronize.scheduler.engine.ExecutionHandler;

import java.time.Instant;

public class ConnectorMetadata {

    private final long startedAt;
    private final String taskId;
    private final String envId;
    private final SourceConnector connector;
    private final ExecutionHandler executionHandler;

    public ConnectorMetadata(String taskId, String envId, SourceConnector connector, ExecutionHandler executionHandler) {
        this.taskId = taskId;
        this.envId = envId;
        this.executionHandler = executionHandler;
        this.startedAt = Instant.now().getEpochSecond();
        this.connector = connector;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getEnvId() {
        return envId;
    }

    public SourceConnector getConnector() {
        return connector;
    }

    public long runningTime() {
        return Instant.now().getEpochSecond() - startedAt;
    }

    public ExecutionHandler getExecutionHandler() {
        return executionHandler;
    }
}
