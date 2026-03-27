package io.synkronize.extension.connector.runtime;

import io.quarkus.runtime.annotations.Recorder;
import io.synkronize.connector.source.spi.SourceConnectorFactory;

import java.util.Map;

@Recorder
public class SourceConnectorRegistryRecorder {

    public void init(Map<String, Class<? extends SourceConnectorFactory>> factories) {
        SourceConnectorRegistry.set(factories);
    }

}
