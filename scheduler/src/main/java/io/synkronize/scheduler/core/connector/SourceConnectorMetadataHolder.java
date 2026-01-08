package io.synkronize.scheduler.core.connector;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SourceConnectorMetadataHolder {

    private static final Map<String, SourceConnectorMetadata> metadata
            = new ConcurrentHashMap<>();

    public static SourceConnectorMetadata get(String envId, String sourceId) {
        return metadata.get(getKey(envId, sourceId));
    }

    public static void put(SourceConnectorMetadata sourceConnectorMetadata) {
        String key = getKey(sourceConnectorMetadata.getEnvId(), sourceConnectorMetadata.getTaskId());
        metadata.put(key, sourceConnectorMetadata);
    }

    public static void remove(String envId, String taskId) {
        metadata.remove(getKey(envId, taskId));
    }

    public static Collection<SourceConnectorMetadata> listRunningSourceConnectors() {
        return metadata.values();
    }

    private static String getKey(String envId, String taskId) {
        return envId + "/" + taskId;
    }
}
