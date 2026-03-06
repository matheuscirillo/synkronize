package io.synkronize.scheduler.connector.exception;

public class ConnectorClassNotFoundException extends RuntimeException {

    public ConnectorClassNotFoundException(String message) {
        super(message);
    }
}
