package io.synkronize.executor.engine.pipeline.stage.factory;

import io.synkronize.commons.model.stage.LogPipelineStage;
import io.synkronize.executor.engine.pipeline.stage.LogStage;
import jakarta.enterprise.context.Dependent;

@Dependent
public class LogStageFactory implements StageFactory<LogStage, LogPipelineStage> {

    @Override
    public LogStage newInstance(LogPipelineStage sinkPipelineStage) {
        return new LogStage(sinkPipelineStage);
    }
}
