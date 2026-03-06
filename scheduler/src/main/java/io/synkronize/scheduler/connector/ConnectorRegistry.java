package io.synkronize.scheduler.connector;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectorRegistry {

    private static final Map<String, ConnectorMetadata> metadata
            = new ConcurrentHashMap<>();

    public static ConnectorMetadata get(String envId, String sourceId) {
        return metadata.get(getKey(envId, sourceId));
    }

    public static void put(ConnectorMetadata connectorMetadata) {
        String key = getKey(connectorMetadata.getEnvId(), connectorMetadata.getTaskId());
        metadata.put(key, connectorMetadata);
    }

    public static void remove(String envId, String taskId) {
        metadata.remove(getKey(envId, taskId));
    }

    public static Collection<ConnectorMetadata> listRunningSourceConnectors() {
        return metadata.values();
    }

    private static String getKey(String envId, String taskId) {
        return envId + "/" + taskId;
    }
}
