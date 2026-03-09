package io.synkronize.executor.sink;

import io.synkronize.connector.sink.spi.context.Property;

import java.util.Objects;

public record SinkProperty(String name, String value) implements Property {

    public SinkProperty(String name, String value) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.value = Objects.requireNonNull(value, "value must not be null");
    }
}
