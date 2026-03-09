package io.synkronize.executor.buffer;

import io.synkronize.executor.model.SynkronizeMessage;

import java.io.Closeable;
import java.time.Duration;
import java.util.Deque;
import java.util.Map;
import java.util.Queue;

public interface BufferReader extends Closeable {

    Map<String, Deque<SynkronizeMessage>> read(Duration duration) throws InterruptedException;

    void commit(Duration duration);
}
