package io.synkronize.executor.buffer;

import io.synkronize.executor.model.SynkronizeMessage;

import java.io.Closeable;
import java.time.Duration;
import java.util.List;

public interface BufferReader extends Closeable {

    List<SynkronizeMessage> read(Duration duration) throws InterruptedException;

    void commit(Duration duration);
}
