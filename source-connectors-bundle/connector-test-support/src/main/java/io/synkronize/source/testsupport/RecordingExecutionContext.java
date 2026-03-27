package io.synkronize.source.testsupport;

import io.synkronize.connector.source.spi.context.execution.ExecutionContext;
import io.synkronize.connector.source.spi.context.execution.ExecutionFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RecordingExecutionContext implements ExecutionContext {

    private final List<SimpleExecutionFile> written = new ArrayList<>();
    private boolean emptyReceive;
    private Throwable error;

    @Override
    public ExecutionFile create() {
        return new SimpleExecutionFile();
    }

    @Override
    public void emptyReceive() {
        this.emptyReceive = true;
    }

    @Override
    public boolean isEmptyReceive() {
        return emptyReceive;
    }

    @Override
    public void write(ExecutionFile file) {
        if (file instanceof SimpleExecutionFile simple) {
            written.add(simple);
        } else {
            throw new IllegalArgumentException("Expected SimpleExecutionFile");
        }
    }

    @Override
    public int writtenMessagesQuantity() {
        return written.size();
    }

    @Override
    public void signalError(Throwable error) {
        this.error = error;
    }

    @Override
    public Throwable getError() {
        return error;
    }

    public List<SimpleExecutionFile> getWrittenFiles() {
        return Collections.unmodifiableList(written);
    }
}
