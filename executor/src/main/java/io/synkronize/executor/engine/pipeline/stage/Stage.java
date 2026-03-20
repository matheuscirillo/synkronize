package io.synkronize.executor.engine.pipeline.stage;

import io.synkronize.commons.model.SinkPipelineStage;
import io.synkronize.executor.model.SynkronizeMessage;

public abstract class Stage<T extends SinkPipelineStage> {

    public Stage(T sinkPipelineStage) {
    }

    public abstract SynkronizeMessage process(SynkronizeMessage input);

}
