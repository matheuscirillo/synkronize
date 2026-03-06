package io.synkronize.scheduler.messaging.buffer;

import io.synkronize.scheduler.model.SynkronizeMessage;

import java.io.Closeable;
import java.io.IOException;

public interface Buffer extends Closeable {

    void write(SynkronizeMessage message) throws IOException;

}
