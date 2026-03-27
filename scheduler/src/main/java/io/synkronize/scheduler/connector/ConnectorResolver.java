package io.synkronize.scheduler.connector;

import io.synkronize.connector.source.spi.SourceConnector;
import io.synkronize.connector.source.spi.context.task.TaskContext;

public interface ConnectorResolver {

    SourceConnector resolve(String sourceType, TaskContext taskContext);

    SourceConnector resolve(String sourceId, String envId);

}
