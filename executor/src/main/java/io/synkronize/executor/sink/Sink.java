package io.synkronize.executor.sink;

import io.synkronize.connector.sink.spi.SinkConnector;
import io.synkronize.executor.engine.pipeline.Pipeline;

public class Sink {

    private String taskId;
    private String sinkId;
    private String envId;
    private SinkConnector connector;
    private Pipeline pipeline;

    public Sink() {
    }

    public Sink(String taskId, String sinkId, String envId, SinkConnector connector, Pipeline pipeline) {
        this.taskId = taskId;
        this.sinkId = sinkId;
        this.envId = envId;
        this.connector = connector;
        this.pipeline = pipeline;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getSinkId() {
        return sinkId;
    }

    public void setSinkId(String sinkId) {
        this.sinkId = sinkId;
    }

    public String getEnvId() {
        return envId;
    }

    public void setEnvId(String envId) {
        this.envId = envId;
    }

    public SinkConnector getConnector() {
        return connector;
    }

    public void setConnector(SinkConnector connector) {
        this.connector = connector;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public String toString() {
        return "Sink{" +
                "taskId='" + taskId + '\'' +
                ", sinkId='" + sinkId + '\'' +
                ", envId='" + envId + '\'' +
                ", connector=" + connector +
                ", pipeline=" + pipeline +
                '}';
    }
}
