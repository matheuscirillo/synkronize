package io.synkronize.commons.model.stage;

import io.synkronize.commons.model.SinkPipelineStage;
import java.util.Map;

@StageType("log")
public class LogPipelineStage extends SinkPipelineStage {

    private Map<String, Object> config;

    public LogPipelineStage() {
    }

    public LogPipelineStage(int order, String type, boolean enabled) {
        super(order, type, enabled);
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

}
