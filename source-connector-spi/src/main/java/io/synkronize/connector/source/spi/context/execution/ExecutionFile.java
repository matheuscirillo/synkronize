package io.synkronize.connector.source.spi.context.execution;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface ExecutionFile {

    void message(String message);

    void message(ExecutionFileType type, InputStream inputStream) throws IOException;

    void attributes(Map<String, String> attributes);

    ExecutionFileType getType();

    String getMessage();

    Map<String, String> getAttributes();
}
