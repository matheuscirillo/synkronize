package io.synkronize.commons.model.stage;

import io.synkronize.commons.model.SinkPipelineStage;

@StageType("xslt")
public class XSLTPipelineStage extends SinkPipelineStage {

    private String transformation;

    public String getTransformation() {
        return transformation;
    }

    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }
}
