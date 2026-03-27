package io.synkronize.source.testsupport;

import io.synkronize.connector.source.spi.context.task.Property;

public final class SimpleProperty implements Property {

    private final String name;
    private final String value;

    public SimpleProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }
}
