package io.synkronize.scheduler.application.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TaskMetrics {

    private final MeterRegistry registry;

    public TaskMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void incrementExecutions(String taskId, String connectorType) {
        registry.counter(
                "synkronize_task_executions_total",
                "task_id", taskId,
                "connector_type", connectorType
        ).increment();
    }

    public void incrementMessages(String taskId, String connectorType, long count) {
        registry.counter(
                "synkronize_task_messages_consumed_total",
                "task_id", taskId,
                "connector_type", connectorType
        ).increment(count);
    }

    public void incrementError(String taskId, String connectorType, Throwable e) {
        registry.counter(
                "synkronize_task_errors_total",
                "task_id", taskId,
                "connector_type", connectorType,
                "exception", e.getClass().getSimpleName()
        ).increment();
    }

}
