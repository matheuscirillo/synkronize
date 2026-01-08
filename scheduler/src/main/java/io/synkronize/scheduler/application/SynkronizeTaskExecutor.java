package io.synkronize.scheduler.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class SynkronizeTaskExecutor {

    private final ScheduledExecutorService scheduledExecutorService;

    @Inject
    public SynkronizeTaskExecutor(@ConfigProperty(name = "synkronize.scheduler.thread-count", defaultValue = "10") Integer threadCount) {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(threadCount);
    }

    public SynkronizeTaskExecutor(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public void schedule(ExecutionHandler executionHandler, Runnable onInterrupt) {
        scheduledExecutorService.schedule(() -> {
            try {
                if (!executionHandler.isCancelled()) {
                    executionHandler.run();
                }
            } finally {
                if (!executionHandler.isCancelled()) {
                    schedule(executionHandler, onInterrupt);
                } else {
                    onInterrupt.run();
                }
            }
        }, executionHandler.getDelay().toMillis(), TimeUnit.MILLISECONDS);
    }

    private String uniqueId(String envId, String sourceId) {
        return envId + "/" + sourceId;
    }
}
