package io.synkronize.extension.connector.runtime;

import io.synkronize.connector.source.spi.SourceConnector;

import java.util.Map;

public class SourceConnectorRegistry {

    private static Map<String, Class<? extends SourceConnector>> sourceConnectorClasses;

    public static void set(Map<String, Class<? extends SourceConnector>> factories) {
        SourceConnectorRegistry.sourceConnectorClasses = factories;
    }

    public static Class<? extends SourceConnector> get(String name) {
        return sourceConnectorClasses.get(name);
    }

    public static Map<String, Class<? extends SourceConnector>> getSourceConnectorClasses() {
        return sourceConnectorClasses;
    }
}
