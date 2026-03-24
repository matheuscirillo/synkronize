package io.synkronize.executor.engine.pipeline;

import io.synkronize.commons.model.SinkPipeline;
import io.synkronize.commons.model.SinkPipelineStage;
import io.synkronize.executor.engine.pipeline.stage.Stage;
import io.synkronize.executor.engine.pipeline.stage.factory.StageFactory;
import io.synkronize.executor.engine.pipeline.stage.factory.StageFactoryRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;

import java.util.List;

@ApplicationScoped
public class PipelineCompiler {

    private final Instance<Object> instance;

    public PipelineCompiler(Instance<Object> instance) {
        this.instance = instance;
    }

    public Pipeline compile(SinkPipeline sinkPipeline) {
        List<SinkPipelineStage> sinkPipelineStages = sinkPipeline.getStages();
        Stage<?>[] stages = sinkPipelineStages.stream()
                .map(this::compileStage)
                .toArray(Stage[]::new);

        return new Pipeline(stages);
    }

    private <T extends SinkPipelineStage> Stage<T> compileStage(T s) {
        Class<? extends StageFactory<?, ? extends SinkPipelineStage>> stageFactoryClass =
                StageFactoryRegistry.getFactoryBySinkStageClass(s.getClass());
        if (stageFactoryClass == null) {
            throw new IllegalStateException(
                    "No runtime stage mapped for pipeline stage config '%s'"
                            .formatted(s.getClass().getName())
            );
        }
        StageFactory<?, T> factory = instantiateFactory(stageFactoryClass);
        return factory.newInstance(s);
    }

    @SuppressWarnings("unchecked")
    private <T extends SinkPipelineStage> StageFactory<?, T> instantiateFactory(Class<? extends StageFactory<?, ?>> clazz) {
        return (StageFactory<?, T>) instance.select(clazz).get();
    }

}
