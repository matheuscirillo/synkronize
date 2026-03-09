package io.synkronize.executor.engine.pipeline.stage;

import io.synkronize.executor.model.SynkronizeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogStage implements Stage {

    private final Logger log = LoggerFactory.getLogger(LogStage.class);

    @Override
    public SynkronizeMessage process(SynkronizeMessage input) {
        log.info(input.content().message());

        return input;
    }
}
