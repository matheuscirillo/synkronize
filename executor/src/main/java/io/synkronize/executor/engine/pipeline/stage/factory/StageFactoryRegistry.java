package io.synkronize.executor.engine.pipeline.stage.factory;

import io.synkronize.commons.model.SinkPipelineStage;
import io.synkronize.commons.model.stage.LogPipelineStage;
import io.synkronize.commons.model.stage.XSLTPipelineStage;

import java.util.HashMap;
import java.util.Map;

public final class StageFactoryRegistry {

    private static final Map<Class<? extends SinkPipelineStage>, Class<? extends StageFactory<?, ? extends SinkPipelineStage>>> STAGES = new HashMap<>();

    static {
        STAGES.put(LogPipelineStage.class, LogStageFactory.class);
        STAGES.put(XSLTPipelineStage.class, XSLTStageFactory.class);
    }

    public static Class<? extends StageFactory<?, ? extends SinkPipelineStage>> getFactoryBySinkStageClass(
            Class<? extends SinkPipelineStage> clazz) {
        return STAGES.get(clazz);
    }

}
