package io.synkronize.scheduler.application.context.execution;

import io.synkronize.connector.source.spi.context.execution.ExecutionContext;
import io.synkronize.connector.source.spi.context.execution.ExecutionFile;
import io.synkronize.scheduler.core.buffer.Buffer;
import io.synkronize.scheduler.core.message.SynkronizeMessage;

import java.io.IOException;

public class ExecutionContextImpl implements ExecutionContext {

    private boolean isEmptyReceive = false;
    private Throwable error = null;

    private final String taskId;
    private final String envId;
    private final Buffer buffer;
    private int writtenMessagesQuantity = 0;

    public ExecutionContextImpl(Buffer buffer, String taskId, String envId) {
        this.taskId = taskId;
        this.envId = envId;
        this.buffer = buffer;
    }

    @Override
    public ExecutionFile create() {
        checkNotEmpty();
        return new ExecutionFileImpl();
    }

    @Override
    public void emptyReceive() {
        this.isEmptyReceive = true;
    }

    @Override
    public void write(ExecutionFile file) {
        checkNotEmpty();
        // TODO create a way for handling BINARY
        SynkronizeMessage.Type type = SynkronizeMessage.Type.TEXT;
        try {
            buffer.write(new SynkronizeMessage(taskId, envId, type, file));
            ++writtenMessagesQuantity;
        } catch (IOException e) {
            // this is a severe error and must be reported somewhere,
            // because it prevents the message from being written to the buffer, which is critical
            // TODO
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public int writtenMessagesQuantity() {
        return this.writtenMessagesQuantity;
    }

    @Override
    public void signalError(Throwable error) {
        this.error = error;
    }

    @Override
    public Throwable getError() {
        return this.error;
    }

    @Override
    public boolean isEmptyReceive() {
        return this.isEmptyReceive;
    }

    private void checkNotEmpty() {
        if (isEmptyReceive)
            throw new IllegalStateException("Execution file already signaled as empty receive");
    }
}
