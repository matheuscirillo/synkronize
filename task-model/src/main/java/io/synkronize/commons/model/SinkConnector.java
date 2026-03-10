package io.synkronize.commons.model;

import java.util.HashMap;
import java.util.Map;

public class SinkConnector {

    private String id;
    private String name;
    private String type;
    private String description;
    private boolean enabled;
    private Map<String, String> properties = new HashMap<>();
    private SinkDeliveryPolicy deliveryPolicy;
    private SinkPipeline pipeline;

    public SinkConnector() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties == null ? new HashMap<>() : new HashMap<>(properties);
    }

    public SinkDeliveryPolicy getDeliveryPolicy() {
        return deliveryPolicy;
    }

    public void setDeliveryPolicy(SinkDeliveryPolicy deliveryPolicy) {
        this.deliveryPolicy = deliveryPolicy;
    }

    public SinkPipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(SinkPipeline pipeline) {
        this.pipeline = pipeline;
    }
}
