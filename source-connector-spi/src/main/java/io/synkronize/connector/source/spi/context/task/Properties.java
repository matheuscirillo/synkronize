package io.synkronize.connector.source.spi.context.task;

public interface Properties {

    Property get(String key);

    boolean containsKey(String key);

    Property getOrDefault(String key, Property defaultValue);

}
