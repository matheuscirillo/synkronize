package io.synkronize.scheduler.core;


import io.synkronize.scheduler.core.message.TaskMessage;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class SynkronizeTaskQueue {

    private final BlockingQueue<TaskMessage> queue = new LinkedBlockingQueue<>();

    public void put(TaskMessage taskMessage) throws InterruptedException {
        queue.put(taskMessage);
    }

    public TaskMessage poll(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }
}
