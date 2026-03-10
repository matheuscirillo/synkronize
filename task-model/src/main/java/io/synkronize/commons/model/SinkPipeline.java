package io.synkronize.commons.model;

import java.util.ArrayList;
import java.util.List;

public class SinkPipeline {

    private List<SinkPipelineStage> stages = new ArrayList<>();

    public SinkPipeline() {
    }

    public List<SinkPipelineStage> getStages() {
        return stages;
    }

    public void setStages(List<SinkPipelineStage> stages) {
        this.stages = stages == null ? new ArrayList<>() : new ArrayList<>(stages);
    }
}
