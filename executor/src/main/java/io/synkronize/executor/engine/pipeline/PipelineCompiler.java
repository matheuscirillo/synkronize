package io.synkronize.executor.engine.pipeline;

import io.synkronize.commons.model.SinkPipeline;
import io.synkronize.commons.model.SinkPipelineStage;
import io.synkronize.executor.engine.pipeline.stage.RuntimeStageRegistry;
import io.synkronize.executor.engine.pipeline.stage.Stage;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.function.Function;

@ApplicationScoped
public class PipelineCompiler {

    public Pipeline compile(SinkPipeline sinkPipeline) {
        List<SinkPipelineStage> sinkPipelineStages = sinkPipeline.getStages();
        Stage<?>[] stages = sinkPipelineStages.stream()
                .map(s -> {
                    Function<SinkPipelineStage, Stage<?>> stageFactory =
                            RuntimeStageRegistry.getFactoryByStageConfigClass(s.getClass());
                    if (stageFactory == null) {
                        throw new IllegalStateException(
                                "No runtime stage mapped for pipeline stage config '%s'"
                                        .formatted(s.getClass().getName())
                        );
                    }
                    return stageFactory.apply(s);
                })
                .toArray(Stage[]::new);

        return new Pipeline(stages);
    }

}
