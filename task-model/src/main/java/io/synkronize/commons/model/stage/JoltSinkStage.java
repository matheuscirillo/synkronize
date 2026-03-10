package io.synkronize.commons.model.stage;

import io.synkronize.commons.model.SinkPipelineStage;

public class JoltSinkStage extends SinkPipelineStage {

    private final String transformation;

    public JoltSinkStage(int order, String type, boolean enabled, String transformation) {
        super(order, type, enabled);
        this.transformation = transformation;
    }

    public String getTransformation() {
        return transformation;
    }
}
