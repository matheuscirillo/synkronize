package io.synkronize.executor.engine.pipeline.stage;

import io.synkronize.commons.model.stage.LogPipelineStage;
import io.synkronize.executor.model.SynkronizeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogStage extends Stage<LogPipelineStage> {

    private final Logger log = LoggerFactory.getLogger(LogStage.class);

    public LogStage(LogPipelineStage sinkPipelineStage) {
        super(sinkPipelineStage);
    }

    @Override
    public SynkronizeMessage process(SynkronizeMessage input) {
        log.info(input.content().message());
        return input;
    }
}
