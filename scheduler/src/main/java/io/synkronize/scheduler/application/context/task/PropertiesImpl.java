package io.synkronize.scheduler.application.context.task;

import io.synkronize.connector.source.spi.context.task.Properties;
import io.synkronize.connector.source.spi.context.task.Property;

import java.util.Collections;
import java.util.Map;

public class PropertiesImpl implements Properties {

    private final Map<String, Property> properties;

    public PropertiesImpl(Map<String, Property> properties) {
        this.properties = Collections.unmodifiableMap(properties);
    }

    @Override
    public Property get(String key) {
        return this.properties.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return this.properties.containsKey(key);
    }

    @Override
    public Property getOrDefault(String key, Property defaultValue) {
        return this.properties.getOrDefault(key, defaultValue);
    }
}
