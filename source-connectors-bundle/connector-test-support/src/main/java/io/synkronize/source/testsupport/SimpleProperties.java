package io.synkronize.source.testsupport;

import io.synkronize.connector.source.spi.context.task.Properties;
import io.synkronize.connector.source.spi.context.task.Property;

import java.util.HashMap;
import java.util.Map;

public final class SimpleProperties implements Properties {

    private final Map<String, Property> entries = new HashMap<>();

    public SimpleProperties put(String key, String value) {
        entries.put(key, new SimpleProperty(key, value));
        return this;
    }

    @Override
    public Property get(String key) {
        return entries.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return entries.containsKey(key);
    }

    @Override
    public Property getOrDefault(String key, Property defaultValue) {
        return entries.getOrDefault(key, defaultValue);
    }
}
