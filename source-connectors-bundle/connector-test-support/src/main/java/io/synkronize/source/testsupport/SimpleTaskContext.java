package io.synkronize.source.testsupport;

import io.synkronize.connector.source.spi.context.task.Properties;
import io.synkronize.connector.source.spi.context.task.TaskContext;

public final class SimpleTaskContext implements TaskContext {

    private final String taskId;
    private final String sourceType;
    private final Properties properties;

    public SimpleTaskContext(String taskId, String sourceType, Properties properties) {
        this.taskId = taskId;
        this.sourceType = sourceType;
        this.properties = properties;
    }

    @Override
    public String getTaskId() {
        return taskId;
    }

    @Override
    public String getSourceType() {
        return sourceType;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }
}
