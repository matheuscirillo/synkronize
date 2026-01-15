package io.synkronize.scheduler.adapter.out.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.synkronize.scheduler.core.port.TaskMetricsPort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MicrometerTaskMetricsAdapter implements TaskMetricsPort {

    private final MeterRegistry registry;

    public MicrometerTaskMetricsAdapter(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void incrementExecutions(String taskId, String connectorType) {
        registry.counter(
                "synkronize_task_executions_total",
                "task_id", taskId,
                "connector_type", connectorType
        ).increment();
    }

    @Override
    public void incrementMessages(String taskId, String connectorType, long count) {
        registry.counter(
                "synkronize_task_messages_consumed_total",
                "task_id", taskId,
                "connector_type", connectorType
        ).increment(count);
    }

    @Override
    public void incrementError(String taskId, String connectorType, Throwable e) {
        registry.counter(
                "synkronize_task_errors_total",
                "task_id", taskId,
                "connector_type", connectorType,
                "exception", e.getClass().getSimpleName()
        ).increment();
    }

}
