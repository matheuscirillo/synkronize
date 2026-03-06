package io.synkronize.scheduler.engine;

public interface Scheduler {

    void start() throws Exception;

    boolean isRunning();

    boolean isClosed();

}
