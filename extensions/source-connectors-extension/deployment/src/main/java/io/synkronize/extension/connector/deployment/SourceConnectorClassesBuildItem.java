package io.synkronize.extension.connector.deployment;

import io.quarkus.builder.item.SimpleBuildItem;
import io.synkronize.connector.source.spi.SourceConnector;

import java.util.Map;

public final class SourceConnectorClassesBuildItem extends SimpleBuildItem {

    private final Map<String, Class<? extends SourceConnector>> sourceConnectorClasses;

    public SourceConnectorClassesBuildItem(Map<String, Class<? extends SourceConnector>> sourceConnectorClasses) {
        this.sourceConnectorClasses = sourceConnectorClasses;
    }

    public Map<String, Class<? extends SourceConnector>> getFactories() {
        return sourceConnectorClasses;
    }
}
