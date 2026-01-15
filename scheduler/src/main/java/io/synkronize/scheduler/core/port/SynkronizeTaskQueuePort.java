package io.synkronize.scheduler.core.port;

import io.synkronize.scheduler.core.message.TaskMessage;

import java.util.concurrent.TimeUnit;

public interface SynkronizeTaskQueuePort {

    void put(TaskMessage taskMessage) throws InterruptedException;

    TaskMessage poll(long timeout, TimeUnit unit) throws InterruptedException;
}
