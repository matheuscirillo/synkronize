package io.synkronize.scheduler.core.buffer;

import io.synkronize.scheduler.core.message.SynkronizeMessage;

import java.io.Closeable;
import java.io.IOException;

public interface Buffer extends Closeable {

    void write(SynkronizeMessage message) throws IOException;

}
