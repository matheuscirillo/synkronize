package io.synkronize.scheduler.connector.context;

import io.synkronize.connector.source.spi.context.execution.ExecutionFile;
import io.synkronize.connector.source.spi.context.execution.ExecutionFileType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public class ExecutionFileImpl implements ExecutionFile {

    private String message;
    private ExecutionFileType type;
    private Map<String, String> attributes;

    @Override
    public void message(String message) {
        type = ExecutionFileType.TEXT;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public void message(ExecutionFileType type, InputStream inputStream) throws IOException {
        this.type = type;
        // TODO implement way for handling ExecutionFileType.BINARY (files for example)
        // for now, every input is treated as text, despite the type parameter passed being BINARY
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        read(inputStream, outStream);
        this.message = outStream.toString(StandardCharsets.UTF_8);
    }

    @Override
    public void attributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public ExecutionFileType getType() {
        return this.type;
    }

    @Override
    public Map<String, String> getAttributes() {
        return this.attributes != null ? Collections.unmodifiableMap(this.attributes) : null;

    }

    private void read(InputStream inputStream, OutputStream outStream) {
        byte[] buf = new byte[524288];
        try (inputStream; outStream) {
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) != -1) {
                outStream.write(buf, 0, bytesRead);
            }
            outStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
