package io.synkronize.executor.engine.pipeline.stage;

import io.synkronize.executor.model.SynkronizeMessage;

public interface Stage {

    SynkronizeMessage process(SynkronizeMessage input);

}
