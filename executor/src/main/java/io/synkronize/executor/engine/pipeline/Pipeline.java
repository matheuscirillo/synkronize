package io.synkronize.executor.engine.pipeline;

import io.synkronize.executor.model.SynkronizeMessage;
import io.synkronize.executor.engine.pipeline.stage.Stage;

import java.util.ArrayList;

public class Pipeline {

    private final Stage[] stages;

    public Pipeline(Stage[] stages) {
        this.stages = stages;
    }

    public SynkronizeMessage execute(SynkronizeMessage input) {
        SynkronizeMessage current = input;
        for (Stage stage : stages) {
            current = stage.process(current);
        }

        return current;
    }
}
