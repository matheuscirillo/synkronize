package io.synkronize.source.testsupport;

import io.synkronize.connector.source.spi.context.execution.ExecutionFile;
import io.synkronize.connector.source.spi.context.execution.ExecutionFileType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class SimpleExecutionFile implements ExecutionFile {

    private String message;
    private Map<String, String> attributes = new HashMap<>();
    private ExecutionFileType type = ExecutionFileType.TEXT;

    @Override
    public void message(String message) {
        this.message = message;
    }

    @Override
    public void message(ExecutionFileType type, InputStream inputStream) throws IOException {
        this.type = type;
        inputStream.readAllBytes();
    }

    @Override
    public void attributes(Map<String, String> attributes) {
        this.attributes = new HashMap<>(attributes);
    }

    @Override
    public ExecutionFileType getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }
}
