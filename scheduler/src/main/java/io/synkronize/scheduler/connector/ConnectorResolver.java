package io.synkronize.scheduler.connector;

import io.synkronize.connector.source.spi.SourceConnector;

public interface ConnectorResolver {

    SourceConnector resolve(String sourceType);

    SourceConnector resolve(String sourceId, String envId);

}
