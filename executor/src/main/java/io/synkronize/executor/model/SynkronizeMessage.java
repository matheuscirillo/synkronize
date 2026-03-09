package io.synkronize.executor.model;

import java.util.Map;

public record SynkronizeMessage(String uniqueId,
                                String taskId,
                                String envId,
                                long timestamp,
                                Type type,
                                Content content) {

    public enum Type {
        TEXT,
        BINARY
    }

    public record Content(String message, Map<String, String> attributes) {

    }
}
