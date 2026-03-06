package io.synkronize.scheduler.model;

import io.synkronize.connector.source.spi.context.execution.ExecutionFile;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record SynkronizeMessage(String uniqueId,
                                String taskId,
                                String envId,
                                long timestamp,
                                Type type,
                                Content content) {

    public SynkronizeMessage(String taskId, String envId, Type type, ExecutionFile content) {
        this(UUID.randomUUID().toString(),
                taskId, envId,
                Instant.now().toEpochMilli(),
                type,
                new Content(content.getMessage(), content.getAttributes()));
    }

    public enum Type {
        TEXT,
        BINARY
    }

    public record Content(String message, Map<String, String> attributes) {

    }

}
