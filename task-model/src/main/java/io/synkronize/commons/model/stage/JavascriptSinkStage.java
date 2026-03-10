package io.synkronize.commons.model.stage;

import io.synkronize.commons.model.SinkPipelineStage;

public class JavascriptSinkStage extends SinkPipelineStage {

    private final String script;

    public JavascriptSinkStage(int order, String type, boolean enabled, String script) {
        super(order, type, enabled);
        this.script = script;
    }

    public String getScript() {
        return script;
    }
}
