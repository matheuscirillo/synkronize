package io.synkronize.executor.sink;

import io.synkronize.connector.sink.spi.SinkConnector;
import io.synkronize.executor.engine.pipeline.Pipeline;

public class Sink {

    private final String sinkId;
    private final String envId;
    private final SinkConnector connector;
    private final Pipeline pipeline;

    public Sink(String sinkId, String envId, SinkConnector connector, Pipeline pipeline) {
        this.sinkId = sinkId;
        this.envId = envId;
        this.connector = connector;
        this.pipeline = pipeline;
    }

    public String getSinkId() {
        return sinkId;
    }

    public String getEnvId() {
        return envId;
    }

    public SinkConnector getConnector() {
        return connector;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }
}
