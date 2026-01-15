package io.synkronize.scheduler.core.port;

public interface TaskMetricsPort {

    void incrementExecutions(String taskId, String connectorType);

    void incrementMessages(String taskId, String connectorType, long count);

    void incrementError(String taskId, String connectorType, Throwable e);

}
