package io.synkronize.connector.sink.spi.context;

public interface Properties {

    Property get(String key);

    boolean containsKey(String key);

    Property getOrDefault(String key, Property defaultValue);

}
