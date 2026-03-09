package io.synkronize.connector.sink.spi;

import io.synkronize.connector.sink.spi.context.SinkContext;

public interface SinkConnector {

    void onInit(SinkContext sinkContext);

    void execute(String message);

    boolean isClosed();

}
