package io.synkronize.extension.stage.runtime;

import io.quarkus.runtime.annotations.Recorder;
import io.synkronize.commons.model.SinkPipelineStage;

import java.util.Map;

@Recorder
public class StageTypeRegistryRecorder {

    public void init(Map<String, Class<? extends SinkPipelineStage>> stageClasses) {
        StageTypeRegistry.set(stageClasses);
    }
}
