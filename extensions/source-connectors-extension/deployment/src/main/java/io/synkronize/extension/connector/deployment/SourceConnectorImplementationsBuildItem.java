package io.synkronize.extension.connector.deployment;

import io.quarkus.builder.item.SimpleBuildItem;

import java.util.List;

public final class SourceConnectorImplementationsBuildItem extends SimpleBuildItem {

    private final List<String> implementationsNames;

    public SourceConnectorImplementationsBuildItem(List<String> implementationsNames) {
        this.implementationsNames = implementationsNames;
    }

    public List<String> getImplementationsNames() {
        return implementationsNames;
    }
}
