package io.synkronize.extension.sink.deployment;

import io.quarkus.builder.item.SimpleBuildItem;

import java.util.List;

public final class SinkConnectorImplementationsBuildItem extends SimpleBuildItem {

    private final List<String> implementationsNames;

    public SinkConnectorImplementationsBuildItem(List<String> implementationsNames) {
        this.implementationsNames = implementationsNames;
    }

    public List<String> getImplementationsNames() {
        return implementationsNames;
    }
}
