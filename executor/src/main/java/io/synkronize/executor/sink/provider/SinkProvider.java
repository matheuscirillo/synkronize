package io.synkronize.executor.sink.provider;

import io.synkronize.commons.model.SinkConnector;
import io.synkronize.executor.sink.Sink;

import java.util.List;
import java.util.Optional;

public interface SinkProvider {

    List<SinkConnector> getSinksFromActiveVersion(String taskId);

    Optional<Sink> findSink(String taskId, String version, String sinkId);

}
