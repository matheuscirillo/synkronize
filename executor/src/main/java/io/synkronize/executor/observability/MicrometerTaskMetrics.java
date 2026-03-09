package io.synkronize.executor.observability;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MicrometerTaskMetrics implements TaskMetrics {

    private final MeterRegistry registry;

    public MicrometerTaskMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void incrementExecutions(String taskId, String connectorType) {
        registry.counter(
                "synkronize_executor_executions_total",
                "task_id", taskId,
                "connector_type", connectorType
        ).increment();
    }

    @Override
    public void incrementMessages(String taskId, String connectorType, long count) {
        registry.counter(
                "synkronize_executor_messages_delivered_total",
                "task_id", taskId,
                "connector_type", connectorType
        ).increment(count);
    }

    @Override
    public void incrementError(String taskId, String connectorType, Throwable e) {
        registry.counter(
                "synkronize_executor_errors_total",
                "task_id", taskId,
                "connector_type", connectorType,
                "exception", e.getClass().getSimpleName()
        ).increment();
    }
}
