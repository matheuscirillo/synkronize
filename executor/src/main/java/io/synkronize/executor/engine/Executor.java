package io.synkronize.executor.engine;

import java.io.Closeable;

public interface Executor extends Closeable {

    void start();

    boolean isRunning();

    boolean isClosed();
}
