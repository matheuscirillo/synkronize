package io.synkronize.connector.source.spi.context.task;

public interface TaskContext {

    String getTaskId();

    String getSourceType();

    Properties getProperties();

}
