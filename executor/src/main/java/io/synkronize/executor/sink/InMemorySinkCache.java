package io.synkronize.executor.sink;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

@ApplicationScoped
public class InMemorySinkCache {

    private final ConcurrentMap<SinkCacheKey, Sink> cache = new ConcurrentHashMap<>();

    public Optional<Sink> get(String taskId, String envId, String sinkId) {
        SinkCacheKey key = SinkCacheKey.of(taskId, envId, sinkId);
        return Optional.ofNullable(cache.get(key));
    }

    public Sink getOrCreate(String taskId, String envId, String sinkId, Supplier<Sink> sinkSupplier) {
        Objects.requireNonNull(sinkSupplier, "sinkSupplier must not be null");
        SinkCacheKey key = SinkCacheKey.of(taskId, envId, sinkId);
        return cache.computeIfAbsent(key, ignored -> Objects.requireNonNull(sinkSupplier.get(),
                "sinkSupplier returned null"));
    }

    public void put(String taskId, String envId, String sinkId, Sink sink) {
        Objects.requireNonNull(sink, "sink must not be null");
        SinkCacheKey key = SinkCacheKey.of(taskId, envId, sinkId);
        cache.put(key, sink);
    }

    public Optional<Sink> evict(String taskId, String envId, String sinkId) {
        SinkCacheKey key = SinkCacheKey.of(taskId, envId, sinkId);
        return Optional.ofNullable(cache.remove(key));
    }

    public int size() {
        return cache.size();
    }

    public void clear() {
        cache.clear();
    }

    private record SinkCacheKey(String taskId, String envId, String sinkId) {
        private SinkCacheKey {
            Objects.requireNonNull(taskId, "taskId must not be null");
            Objects.requireNonNull(envId, "envId must not be null");
            Objects.requireNonNull(sinkId, "sinkId must not be null");
        }

        private static SinkCacheKey of(String taskId, String envId, String sinkId) {
            return new SinkCacheKey(taskId, envId, sinkId);
        }
    }
}
