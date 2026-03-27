package io.synkronize.extension.connector.deployment;

import io.quarkus.builder.item.SimpleBuildItem;
import io.synkronize.connector.source.spi.SourceConnectorFactory;

import java.util.Map;

public final class SourceConnectorClassesBuildItem extends SimpleBuildItem {

    private final Map<String, Class<? extends SourceConnectorFactory>> sourceConnectorClasses;

    public SourceConnectorClassesBuildItem(Map<String, Class<? extends SourceConnectorFactory>> sourceConnectorClasses) {
        this.sourceConnectorClasses = sourceConnectorClasses;
    }

    public Map<String, Class<? extends SourceConnectorFactory>> getFactories() {
        return sourceConnectorClasses;
    }
}
