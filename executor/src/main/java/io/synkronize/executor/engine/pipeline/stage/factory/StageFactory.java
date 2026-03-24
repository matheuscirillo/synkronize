package io.synkronize.executor.engine.pipeline.stage.factory;

import io.synkronize.commons.model.SinkPipelineStage;
import io.synkronize.executor.engine.pipeline.stage.Stage;

public interface StageFactory<R extends Stage<T>, T extends SinkPipelineStage> {

    R newInstance(T sinkPipelineStage);

}
