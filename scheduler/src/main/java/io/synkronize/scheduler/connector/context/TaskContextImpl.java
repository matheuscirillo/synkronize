package io.synkronize.scheduler.connector.context;

import io.synkronize.connector.source.spi.context.task.Properties;
import io.synkronize.connector.source.spi.context.task.TaskContext;

public class TaskContextImpl implements TaskContext {

    private final String taskId;
    private final String sourceType;
    private final Properties properties;

    public TaskContextImpl(String taskId, String sourceType, Properties properties) {
        this.taskId = taskId;
        this.sourceType = sourceType;
        this.properties = properties;
    }

    @Override
    public String getTaskId() {
        return this.taskId;
    }

    @Override
    public String getSourceType() {
        return this.sourceType;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }
}
