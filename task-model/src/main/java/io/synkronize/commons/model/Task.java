package io.synkronize.commons.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Task {

    private String id;
    private String taskId;
    private String name;
    private String description;
    private List<String> tags = new ArrayList<>();
    private TaskStatus status;
    private long revision;
    private Instant createdAt;
    private Instant updatedAt;
    private List<TaskVersion> versions = new ArrayList<>();
    private List<TaskDeployment> deployments = new ArrayList<>();

    public Task() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags == null ? new ArrayList<>() : new ArrayList<>(tags);
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<TaskVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<TaskVersion> versions) {
        this.versions = versions == null ? new ArrayList<>() : new ArrayList<>(versions);
    }

    public List<TaskDeployment> getDeployments() {
        return deployments;
    }

    public void setDeployments(List<TaskDeployment> deployments) {
        this.deployments = deployments == null ? new ArrayList<>() : new ArrayList<>(deployments);
    }
}
