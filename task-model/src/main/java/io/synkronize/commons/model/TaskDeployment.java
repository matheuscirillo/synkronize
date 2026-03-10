package io.synkronize.commons.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class TaskDeployment {

    private String envId;
    private int versionNumber;
    private DeploymentStatus status;
    private Instant deployedAt;
    private String deployedBy;
    private Map<String, DeploymentProperty> properties = new HashMap<>();

    public TaskDeployment() {
    }

    public String getEnvId() {
        return envId;
    }

    public void setEnvId(String envId) {
        this.envId = envId;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public DeploymentStatus getStatus() {
        return status;
    }

    public void setStatus(DeploymentStatus status) {
        this.status = status;
    }

    public Instant getDeployedAt() {
        return deployedAt;
    }

    public void setDeployedAt(Instant deployedAt) {
        this.deployedAt = deployedAt;
    }

    public String getDeployedBy() {
        return deployedBy;
    }

    public void setDeployedBy(String deployedBy) {
        this.deployedBy = deployedBy;
    }

    public Map<String, DeploymentProperty> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, DeploymentProperty> properties) {
        this.properties = properties == null ? new HashMap<>() : new HashMap<>(properties);
    }
}
