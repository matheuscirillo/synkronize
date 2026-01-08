package io.synkronize.scheduler.core;

public interface Scheduler {

    void start() throws Exception;

    boolean isRunning();

    boolean isClosed();

}
