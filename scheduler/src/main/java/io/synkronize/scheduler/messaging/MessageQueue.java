package io.synkronize.scheduler.messaging;

import io.synkronize.scheduler.model.TaskMessage;

import java.util.concurrent.TimeUnit;

public interface MessageQueue {

    void put(TaskMessage taskMessage) throws InterruptedException;

    TaskMessage poll(long timeout, TimeUnit unit) throws InterruptedException;
}
