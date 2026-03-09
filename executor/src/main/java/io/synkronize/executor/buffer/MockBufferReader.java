package io.synkronize.executor.buffer;

import io.synkronize.executor.model.SynkronizeMessage;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MockBufferReader implements BufferReader {

    private static final int TOTAL_MESSAGES = 100;
    private static final int TASK_COUNT = 5;
    private static final int MESSAGES_PER_TASK = TOTAL_MESSAGES / TASK_COUNT;
    private static final String ENV_ID = "mock-env";

    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public Map<String, Deque<SynkronizeMessage>> read(Duration duration) {
        Map<String, Deque<SynkronizeMessage>> messagesByTask = new HashMap<>(TASK_COUNT);
        long now = System.currentTimeMillis();
        try {
            Thread.sleep(duration.minusMillis(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        for (int taskNumber = 1; taskNumber <= TASK_COUNT; taskNumber++) {
            String taskId = "task-" + taskNumber;
            Deque<SynkronizeMessage> taskMessages = new ArrayDeque<>(MESSAGES_PER_TASK);
            for (int messageNumber = 0; messageNumber < MESSAGES_PER_TASK; messageNumber++) {
                long current = sequence.incrementAndGet();
                taskMessages.add(new SynkronizeMessage(
                        "mock-" + current,
                        taskId,
                        ENV_ID,
                        now,
                        SynkronizeMessage.Type.TEXT,
                        new SynkronizeMessage.Content(
                                "This is the message " + (messageNumber + 1) + " from task " + taskId,
                                Map.of()
                        )
                ));
            }
            messagesByTask.put(taskId, taskMessages);
        }

        return messagesByTask;
    }

    @Override
    public void commit(Duration duration) {
        // No-op for mock reader.
    }

    @Override
    public void close() throws IOException {
        // No-op for mock reader.
    }
}
