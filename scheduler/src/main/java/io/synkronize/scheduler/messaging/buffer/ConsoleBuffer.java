package io.synkronize.scheduler.messaging.buffer;

import io.synkronize.scheduler.model.SynkronizeMessage;
import io.synkronize.scheduler.messaging.serializer.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ConsoleBuffer implements Buffer {

    private final Logger logger = LoggerFactory.getLogger(ConsoleBuffer.class);

    private final JsonSerializer jsonSerializer;

    public ConsoleBuffer(JsonSerializer jsonSerializer) {
        this.jsonSerializer = jsonSerializer;
    }

    @Override
    public void write(SynkronizeMessage message) {
        logger.info(jsonSerializer.serialize(message));
    }

    @Override
    public void close() throws IOException {
    }
}
