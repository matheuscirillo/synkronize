package io.synkronize.commons.model;

public abstract class SinkPipelineStage {

    protected final int order;
    protected final String type;
    protected final boolean enabled;

    public SinkPipelineStage(int order, String type, boolean enabled) {
        this.order = order;
        this.type = type;
        this.enabled = enabled;
    }

    public int getOrder() {
        return order;
    }

    public String getType() {
        return type;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
