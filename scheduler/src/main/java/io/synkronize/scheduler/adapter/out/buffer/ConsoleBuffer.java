package io.synkronize.scheduler.adapter.out.buffer;

import io.synkronize.scheduler.adapter.out.serializer.JsonSerializer;
import io.synkronize.scheduler.core.buffer.Buffer;
import io.synkronize.scheduler.core.message.SynkronizeMessage;
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
