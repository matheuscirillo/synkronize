package io.synkronize.commons.model;

public abstract class SinkPipelineStage {

    protected int order;
    protected String type;
    protected boolean enabled;

    public SinkPipelineStage() {
    }

    public SinkPipelineStage(int order, String type, boolean enabled) {
        this.order = order;
        this.type = type;
        this.enabled = enabled;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "SinkPipelineStage{" +
                "order=" + order +
                ", type='" + type + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
