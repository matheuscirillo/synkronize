package io.synkronize.connector.sink.spi.context;

public interface SinkContext {

    String taskId();

    Properties properties();

}
