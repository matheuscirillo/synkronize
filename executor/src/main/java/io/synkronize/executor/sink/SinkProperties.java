package io.synkronize.executor.sink;

import io.synkronize.connector.sink.spi.context.Properties;
import io.synkronize.connector.sink.spi.context.Property;

import java.util.Map;

public final class SinkProperties implements Properties {

    private static final SinkProperties EMPTY = new SinkProperties(Map.of());

    private final Map<String, Property> properties;

    private SinkProperties(Map<String, Property> properties) {
        this.properties = Map.copyOf(properties);
    }

    public static SinkProperties empty() {
        return EMPTY;
    }

    @Override
    public Property get(String key) {
        return properties.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    @Override
    public Property getOrDefault(String key, Property defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }
}
