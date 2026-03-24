package io.synkronize.executor.engine;

import io.synkronize.commons.model.SinkConnector;
import io.synkronize.executor.engine.pipeline.PipelineCompiler;
import io.synkronize.executor.model.SynkronizeMessage;
import io.synkronize.executor.sink.InMemorySinkCache;
import io.synkronize.executor.sink.Sink;
import io.synkronize.executor.sink.provider.SinkProvider;
import io.synkronize.sink.stdout.StdoutSinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.Callable;

public class ExecutionHandler implements Callable<Integer> {

    private final String taskId;
    private final String envId;
    private final Deque<SynkronizeMessage> messageQueue;
    private final InMemorySinkCache sinkCache;
    private final SinkProvider sinkProvider;
    private final PipelineCompiler pipelineCompiler;

    private final Logger logger = LoggerFactory.getLogger(ExecutionHandler.class);

    public ExecutionHandler(String taskId,
                            String envId,
                            Deque<SynkronizeMessage> messageQueue,
                            InMemorySinkCache sinkCache,
                            SinkProvider sinkProvider,
                            PipelineCompiler pipelineCompiler) {
        this.taskId = taskId;
        this.envId = envId;
        this.messageQueue = messageQueue;
        this.sinkCache = sinkCache;
        this.sinkProvider = sinkProvider;
        this.pipelineCompiler = pipelineCompiler;
    }

    @Override
    public Integer call() throws Exception {
        int processedMessages = 0;
        SynkronizeMessage message;
        List<Sink> sinks = sinkCache.getSinksByTaskAndEnvId(taskId, envId);
        if (sinks.isEmpty()) {
            List<SinkConnector> sinksFromActiveVersion = sinkProvider.getSinksFromActiveVersion(taskId);
            sinks = sinksFromActiveVersion.stream()
                    .map(sc -> new Sink(taskId,
                            sc.getId(),
                            envId,
                            new StdoutSinkConnector(),
                            pipelineCompiler.compile(sc.getPipeline())))
                    .toList();

            sinkCache.put(taskId, envId, sinks);
        }

        while ((message = messageQueue.poll()) != null) {
            for (Sink sink : sinks) {
                SynkronizeMessage transformedMessage = sink.getPipeline().execute(message);
                sink.getConnector().execute(transformedMessage.content().message());
                processedMessages++;
            }
        }

        return processedMessages;
    }
}
