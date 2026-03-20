package io.synkronize.executor.buffer;

import io.synkronize.executor.model.SynkronizeMessage;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MockBufferReader implements BufferReader {

    private final int TOTAL_MESSAGES = 1;
    private final int TASK_COUNT = 1;
    private final int MESSAGES_PER_TASK = TOTAL_MESSAGES == 0 ? 0 : TOTAL_MESSAGES / TASK_COUNT;
    private final String ENV_ID = "mock-env";

    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public List<SynkronizeMessage> read(Duration duration) {
        List<SynkronizeMessage> messages = new ArrayList<>(TOTAL_MESSAGES);
        long now = System.currentTimeMillis();
        for (int taskNumber = 1; taskNumber <= TASK_COUNT; taskNumber++) {
            String taskId = "67cf1492f7110f0f7779e001";
            for (int messageNumber = 0; messageNumber < MESSAGES_PER_TASK; messageNumber++) {
                long current = sequence.incrementAndGet();
                messages.add(new SynkronizeMessage(
                        "mock-" + current,
                        taskId,
                        ENV_ID,
                        now,
                        SynkronizeMessage.Type.TEXT,
                        new SynkronizeMessage.Content(
                                "<person><name>John Doe</name><age>30</age><email>john.doe@example.com</email><address><street>123 Main St</street><city>New York</city><state>NY</state><zip>10001</zip></address></person>",
                                Map.of()
                        )
                ));
            }
        }

        return messages;
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
