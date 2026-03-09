package io.synkronize.extension.sink.runtime;

import io.quarkus.runtime.annotations.Recorder;
import io.synkronize.connector.sink.spi.SinkConnector;

import java.util.Map;

@Recorder
public class SinkConnectorRegistryRecorder {

    public void init(Map<String, Class<? extends SinkConnector>> factories) {
        SinkConnectorRegistry.set(factories);
    }

}
