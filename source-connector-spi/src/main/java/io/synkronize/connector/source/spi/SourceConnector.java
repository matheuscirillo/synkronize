package io.synkronize.connector.source.spi;

import io.synkronize.connector.source.spi.context.execution.ExecutionContext;
import io.synkronize.connector.source.spi.context.task.TaskContext;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface SourceConnector {

    void onSchedule(TaskContext context);

    void onTrigger(ExecutionContext context);

    void onStop() throws IOException, TimeoutException;

    boolean isClosed();

}
