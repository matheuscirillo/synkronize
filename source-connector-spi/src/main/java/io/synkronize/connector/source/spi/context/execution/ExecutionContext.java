package io.synkronize.connector.source.spi.context.execution;

public interface ExecutionContext {

    ExecutionFile create();

    void emptyReceive();

    boolean isEmptyReceive();

    void write(ExecutionFile file);

    int writtenMessagesQuantity();

    void signalError(Throwable error);

    Throwable getError();
}
