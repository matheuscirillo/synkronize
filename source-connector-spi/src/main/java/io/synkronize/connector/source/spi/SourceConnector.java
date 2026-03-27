package io.synkronize.connector.source.spi;

import io.synkronize.connector.source.spi.context.execution.ExecutionContext;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface SourceConnector {

    void onTrigger(ExecutionContext context);

    void onStop() throws IOException, TimeoutException;

    boolean isClosed();

}
