package io.synkronize.connector.source.spi.context.execution;

public record ExecutionContextError(String message, Throwable throwable) {
}
