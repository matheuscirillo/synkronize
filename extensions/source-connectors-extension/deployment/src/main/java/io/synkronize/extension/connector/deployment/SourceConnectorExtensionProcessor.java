package io.synkronize.extension.connector.deployment;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.synkronize.connector.source.spi.SourceConnector;
import io.synkronize.connector.source.spi.SourceConnectorFactory;
import io.synkronize.connector.source.spi.SynkronizeConnector;
import io.synkronize.extension.connector.runtime.SourceConnectorRegistryRecorder;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceConnectorExtensionProcessor {

    private final Logger logger = LoggerFactory.getLogger(SourceConnectorExtensionProcessor.class);
    private final Class<SourceConnector> SPI_INTERFACE = SourceConnector.class;

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem("synkronize-source-connectors-extension");
    }

    @BuildStep
    SourceConnectorImplementationsBuildItem collectSourceConnectorImplementations(
            CombinedIndexBuildItem combinedIndex) {
        IndexView index = combinedIndex.getIndex();
        Collection<ClassInfo> allKnownImplementors = index.getAllKnownImplementations(SPI_INTERFACE);
        allKnownImplementors.forEach(ci -> logger.info(ci.name().toString()));
        return new SourceConnectorImplementationsBuildItem(allKnownImplementors.stream().map(ci -> ci.name().toString()).toList());
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void registerImplementations(
            SourceConnectorImplementationsBuildItem item,
            SourceConnectorRegistryRecorder recorder) throws ClassNotFoundException {
        List<String> impls = item.getImplementationsNames();
        Map<String, Class<? extends SourceConnectorFactory>> implementations = new HashMap<>();
        for (String impl : impls) {
            Class<?> implClass = Class.forName(impl, false, Thread.currentThread().getContextClassLoader());
            SynkronizeConnector synkronizeConnector = implClass.getAnnotation(SynkronizeConnector.class);
            if (synkronizeConnector != null) {
                implementations.put(synkronizeConnector.type(), synkronizeConnector.factoryClass());
            }
        }

        recorder.init(implementations);
    }
}