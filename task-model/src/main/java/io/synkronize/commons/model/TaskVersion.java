package io.synkronize.commons.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TaskVersion {

    private int versionNumber;
    private boolean isCurrent;
    private VersionStatus status;
    private Instant createdAt;
    private String createdBy;
    private String changeLog;
    private SourceConnector source;
    private List<SinkConnector> sinks = new ArrayList<>();

    public TaskVersion() {
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public VersionStatus getStatus() {
        return status;
    }

    public void setStatus(VersionStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }

    public SourceConnector getSource() {
        return source;
    }

    public void setSource(SourceConnector source) {
        this.source = source;
    }

    public List<SinkConnector> getSinks() {
        return sinks;
    }

    public void setSinks(List<SinkConnector> sinks) {
        this.sinks = sinks == null ? new ArrayList<>() : new ArrayList<>(sinks);
    }
}
