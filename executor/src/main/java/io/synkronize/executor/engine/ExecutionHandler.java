package io.synkronize.executor.engine;

import io.synkronize.connector.sink.spi.SinkConnector;
import io.synkronize.connector.sink.spi.context.SinkContext;
import io.synkronize.executor.engine.pipeline.Pipeline;
import io.synkronize.executor.engine.pipeline.stage.LogStage;
import io.synkronize.executor.engine.pipeline.stage.Stage;
import io.synkronize.executor.model.SynkronizeMessage;
import io.synkronize.executor.sink.InMemorySinkCache;
import io.synkronize.executor.sink.Sink;
import io.synkronize.executor.sink.SinkContextImpl;
import io.synkronize.executor.sink.SinkProperties;
import io.synkronize.sink.stdout.StdoutSinkConnector;

import java.util.Deque;
import java.util.concurrent.Callable;

public class ExecutionHandler implements Callable<Integer> {

    private static final String MOCK_SINK_ID = "mock-single-sink";

    private final String taskId;
    private final String envId;
    private final Deque<SynkronizeMessage> messageQueue;
    private final InMemorySinkCache sinkCache;

    public ExecutionHandler(String taskId,
                            String envId,
                            Deque<SynkronizeMessage> messageQueue,
                            InMemorySinkCache sinkCache) {
        this.taskId = taskId;
        this.envId = envId;
        this.messageQueue = messageQueue;
        this.sinkCache = sinkCache;
    }

    @Override
    public Integer call() throws Exception {
        // TODO fetch task sinks from datastore and iterate over all configured sinks.
        Sink sink = sinkCache.getOrCreate(taskId, envId, MOCK_SINK_ID, this::createMockedSink);

        int processedMessages = 0;
        SynkronizeMessage message;
        while ((message = messageQueue.poll()) != null) {
            SynkronizeMessage transformedMessage = sink.getPipeline().execute(message);
            sink.getConnector().execute(transformedMessage.content().message());
            processedMessages++;
        }

        return processedMessages;
    }

    private Sink createMockedSink() {
        SinkContext sinkContext = new SinkContextImpl(taskId, SinkProperties.empty());
        SinkConnector connector = new StdoutSinkConnector();
        connector.onInit(sinkContext);
        return new Sink(
                MOCK_SINK_ID,
                envId,
                connector,
                new Pipeline(new Stage[]{new LogStage()})
        );
    }

}
