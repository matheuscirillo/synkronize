package io.synkronize.extension.connector.runtime;

import io.synkronize.connector.source.spi.SourceConnectorFactory;

import java.util.Map;

public class SourceConnectorRegistry {

    private static Map<String, Class<? extends SourceConnectorFactory>> sourceConnectorFactories;

    public static void set(Map<String, Class<? extends SourceConnectorFactory>> factories) {
        SourceConnectorRegistry.sourceConnectorFactories = factories;
    }

    public static Class<? extends SourceConnectorFactory> get(String name) {
        return sourceConnectorFactories.get(name);
    }

    public static Map<String, Class<? extends SourceConnectorFactory>> getSourceConnectorFactories() {
        return sourceConnectorFactories;
    }
}
