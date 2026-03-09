package io.synkronize.extension.sink.deployment;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.synkronize.connector.sink.spi.SinkConnector;
import io.synkronize.connector.sink.spi.SynkronizeSinkConnector;
import io.synkronize.extension.sink.runtime.SinkConnectorRegistryRecorder;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SinkConnectorExtensionProcessor {

    private final Logger logger = LoggerFactory.getLogger(SinkConnectorExtensionProcessor.class);
    private final Class<SinkConnector> SPI_INTERFACE = SinkConnector.class;

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem("synkronize-sink-connectors-extension");
    }

    @BuildStep
    SinkConnectorImplementationsBuildItem collectSinkConnectorImplementations(
            CombinedIndexBuildItem combinedIndex) {
        IndexView index = combinedIndex.getIndex();
        Collection<ClassInfo> allKnownImplementors = index.getAllKnownImplementations(SPI_INTERFACE);
        allKnownImplementors.forEach(ci -> logger.info(ci.name().toString()));
        return new SinkConnectorImplementationsBuildItem(allKnownImplementors.stream().map(ci -> ci.name().toString()).toList());
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void registerImplementations(
            SinkConnectorImplementationsBuildItem item,
            SinkConnectorRegistryRecorder recorder) throws ClassNotFoundException {
        List<String> impls = item.getImplementationsNames();
        Map<String, Class<? extends SinkConnector>> implementations = new HashMap<>();
        for (String impl : impls) {
            Class<? extends SinkConnector> implClass = (Class<? extends SinkConnector>) Class.forName(impl, false, Thread.currentThread().getContextClassLoader());
            SynkronizeSinkConnector synkronizeSinkConnector = implClass.getAnnotation(SynkronizeSinkConnector.class);
            if (synkronizeSinkConnector != null) {
                implementations.put(synkronizeSinkConnector.value(), implClass);
            }
        }

        recorder.init(implementations);
    }
}
