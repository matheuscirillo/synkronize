package io.synkronize.scheduler.connector.context;

import io.synkronize.connector.source.spi.context.task.Property;

public class PropertyImpl implements Property {

    private final String name;
    private final String value;

    public PropertyImpl(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
