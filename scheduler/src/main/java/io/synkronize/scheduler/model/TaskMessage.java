package io.synkronize.scheduler.model;

import java.util.Map;

public record TaskMessage(String envId, String taskId, String sourceType, Map<String, String> configMap, TaskMessageType messageType) {
}
