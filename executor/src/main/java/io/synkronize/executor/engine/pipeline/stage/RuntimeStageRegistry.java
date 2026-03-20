package io.synkronize.executor.engine.pipeline.stage;

import io.synkronize.commons.model.SinkPipelineStage;
import io.synkronize.commons.model.stage.LogPipelineStage;
import io.synkronize.commons.model.stage.XSLTPipelineStage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class RuntimeStageRegistry {

    private static final Map<Class<? extends SinkPipelineStage>, Function<SinkPipelineStage, Stage<?>>> STAGES = new HashMap<>();

    static {
        STAGES.put(LogPipelineStage.class, stage -> new LogStage((LogPipelineStage) stage));
        STAGES.put(XSLTPipelineStage.class, stage -> new XSLTStage((XSLTPipelineStage) stage));
    }

    public static Function<SinkPipelineStage, Stage<?>> getFactoryByStageConfigClass(
            Class<? extends SinkPipelineStage> clazz) {
        return STAGES.get(clazz);
    }

}
