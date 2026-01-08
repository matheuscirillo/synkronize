package io.synkronize.scheduler.application.exception;

public class SourceConnectorNotFound extends RuntimeException {

    public SourceConnectorNotFound(String message) {
        super(message);
    }
}
