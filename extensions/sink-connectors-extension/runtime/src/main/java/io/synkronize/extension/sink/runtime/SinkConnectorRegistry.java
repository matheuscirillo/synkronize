package io.synkronize.extension.sink.runtime;

import io.synkronize.connector.sink.spi.SinkConnector;

import java.util.Map;

public class SinkConnectorRegistry {

    private static Map<String, Class<? extends SinkConnector>> sinkConnectorClasses;

    public static void set(Map<String, Class<? extends SinkConnector>> factories) {
        SinkConnectorRegistry.sinkConnectorClasses = factories;
    }

    public static Class<? extends SinkConnector> get(String name) {
        return sinkConnectorClasses.get(name);
    }

    public static Map<String, Class<? extends SinkConnector>> getSinkConnectorClasses() {
        return sinkConnectorClasses;
    }
}
