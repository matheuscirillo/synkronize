package io.synkronize.scheduler.core.resolver;

import io.synkronize.connector.source.spi.SourceConnector;

public interface SourceConnectorResolver {

    SourceConnector resolve(String sourceType);

    SourceConnector resolve(String sourceId, String envId);

}
