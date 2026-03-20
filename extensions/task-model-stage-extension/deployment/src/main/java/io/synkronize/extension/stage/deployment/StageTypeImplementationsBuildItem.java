package io.synkronize.extension.stage.deployment;

import io.quarkus.builder.item.SimpleBuildItem;

import java.util.Map;

public final class StageTypeImplementationsBuildItem extends SimpleBuildItem {

    private final Map<String, String> implementationsByType;

    public StageTypeImplementationsBuildItem(Map<String, String> implementationsByType) {
        this.implementationsByType = implementationsByType;
    }

    public Map<String, String> getImplementationsByType() {
        return implementationsByType;
    }
}
