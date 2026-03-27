package io.synkronize.connector.source.spi;

import io.synkronize.connector.source.spi.context.task.TaskContext;

public interface SourceConnectorFactory<T extends SourceConnector> {

    T create(TaskContext taskContext);

}
