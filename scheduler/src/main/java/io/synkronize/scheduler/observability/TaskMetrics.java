package io.synkronize.scheduler.observability;

public interface TaskMetrics {

    void incrementExecutions(String taskId, String connectorType);

    void incrementMessages(String taskId, String connectorType, long count);

    void incrementError(String taskId, String connectorType, Throwable e);

}
