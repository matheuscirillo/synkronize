package io.synkronize.executor.sink;

import io.synkronize.connector.sink.spi.context.Properties;
import io.synkronize.connector.sink.spi.context.SinkContext;

import java.util.Objects;

public record SinkContextImpl(String taskId, Properties properties) implements SinkContext {

    public SinkContextImpl(String taskId, Properties properties) {
        this.taskId = Objects.requireNonNull(taskId, "taskId must not be null");
        this.properties = properties == null ? SinkProperties.empty() : properties;
    }
}
