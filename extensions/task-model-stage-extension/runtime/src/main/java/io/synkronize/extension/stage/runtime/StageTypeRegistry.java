package io.synkronize.extension.stage.runtime;

import io.synkronize.commons.model.SinkPipelineStage;

import java.util.Map;

public final class StageTypeRegistry {

    private static Map<String, Class<? extends SinkPipelineStage>> sinkStageClasses = Map.of();

    private StageTypeRegistry() {
    }

    public static void set(Map<String, Class<? extends SinkPipelineStage>> stageClasses) {
        sinkStageClasses = Map.copyOf(stageClasses);
    }

    public static Map<String, Class<? extends SinkPipelineStage>> getStageClasses() {
        return sinkStageClasses;
    }
}
