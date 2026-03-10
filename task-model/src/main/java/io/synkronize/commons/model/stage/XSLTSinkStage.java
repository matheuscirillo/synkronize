package io.synkronize.commons.model.stage;

import io.synkronize.commons.model.SinkPipelineStage;

public class XSLTSinkStage extends SinkPipelineStage {

    private final String transformation;

    public XSLTSinkStage(int order, String type, boolean enabled, String transformation) {
        super(order, type, enabled);
        this.transformation = transformation;
    }

    public String getTransformation() {
        return transformation;
    }
}
