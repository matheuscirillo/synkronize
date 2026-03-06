package io.synkronize.scheduler.messaging;

import io.synkronize.scheduler.model.TaskMessage;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class InMemoryMessageQueue implements MessageQueue {

    private final BlockingQueue<TaskMessage> queue = new LinkedBlockingQueue<>();

    @Override
    public void put(TaskMessage taskMessage) throws InterruptedException {
        queue.put(taskMessage);
    }

    @Override
    public TaskMessage poll(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }
}
