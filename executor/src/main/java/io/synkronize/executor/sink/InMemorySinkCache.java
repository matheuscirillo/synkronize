package io.synkronize.executor.sink;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ApplicationScoped
public class InMemorySinkCache {

    private final ConcurrentMap<TaskEnvIdKey, List<Sink>> cacheByTaskAndEnvId = new ConcurrentHashMap<>();
    private final ConcurrentMap<SinkEnvIdKey, Sink> cacheBySinkId = new ConcurrentHashMap<>();

    public void put(String taskId, String envId, List<Sink> sinks) {
        cacheByTaskAndEnvId.put(new TaskEnvIdKey(taskId, envId), sinks);
        sinks.forEach(sink -> cacheBySinkId.put(new SinkEnvIdKey(sink.getSinkId(), envId), sink));
    }

    public List<Sink> getSinksByTaskAndEnvId(String taskId, String envId) {
        return cacheByTaskAndEnvId.getOrDefault(new TaskEnvIdKey(taskId, envId),
                Collections.emptyList());
    }

    public record TaskEnvIdKey(String taskId, String envId) {

    }

    public record SinkEnvIdKey(String sinkId, String envId) {

    }
}
