package io.synkronize.executor.engine;

import io.synkronize.executor.buffer.BufferReader;
import io.synkronize.executor.engine.pipeline.PipelineCompiler;
import io.synkronize.executor.model.SynkronizeMessage;
import io.synkronize.executor.sink.InMemorySinkCache;
import io.synkronize.executor.sink.provider.SinkProvider;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@ApplicationScoped
public class DefaultExecutor implements Executor {

    private final Logger logger = LoggerFactory.getLogger(DefaultExecutor.class);

    private final BufferReader bufferReader;
    private final InMemorySinkCache inMemorySinkCache;
    private final SinkProvider sinkProvider;
    private final PipelineCompiler pipelineCompiler;

    private final ExecutorService concurrentUnderlyingExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final Semaphore semaphore = new Semaphore(50);

    private volatile boolean isRunning;
    private volatile boolean closeCalled;

    public DefaultExecutor(BufferReader bufferReader,
                           InMemorySinkCache inMemorySinkCache,
                           SinkProvider sinkProvider,
                           PipelineCompiler pipelineCompiler) {
        this.bufferReader = bufferReader;
        this.inMemorySinkCache = inMemorySinkCache;
        this.sinkProvider = sinkProvider;
        this.pipelineCompiler = pipelineCompiler;
    }

    @Override
    public void start() {
        logger.info("DefaultExecutor started");
        this.isRunning = true;
        Throwable err = null;
        while (!closeCalled) {
            try {
                Thread.sleep(2000);
                List<SynkronizeMessage> messages = bufferReader.read(Duration.ofSeconds(5));
                if (messages.isEmpty()) {
                    continue;
                }

                Map<TaskEnvKey, Deque<SynkronizeMessage>> groupedMessages = groupMessages(messages);
                List<Callable<Integer>> callables = getCallables(groupedMessages);
                concurrentUnderlyingExecutor.invokeAll(callables);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                err = e;
                break;
            } catch (Throwable e) {
                err = e;
                logger.error("An error occurred while reading or handling a message", e);
            } finally {
                // TODO
                // if err != null MUST be reported
            }
        }

        this.isRunning = false;
        logger.info("DefaultExecutor stopped");
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public boolean isClosed() {
        return closeCalled;
    }

    @Override
    public void close() {
        this.closeCalled = true;
        try {
            bufferReader.close();
        } catch (IOException e) {
            logger.error("An error occurred while trying to close BufferReader", e);
        }
    }

    private Map<TaskEnvKey, Deque<SynkronizeMessage>> groupMessages(List<SynkronizeMessage> messages) {
        Map<TaskEnvKey, Deque<SynkronizeMessage>> groupedMessages = new HashMap<>();
        for (SynkronizeMessage message : messages) {
            TaskEnvKey key = new TaskEnvKey(message.taskId(), message.envId());
            groupedMessages.computeIfAbsent(key, _ -> new ArrayDeque<>()).offer(message);
        }
        return groupedMessages;
    }

    private List<Callable<Integer>> getCallables(Map<TaskEnvKey, Deque<SynkronizeMessage>> messages) {
        List<Callable<Integer>> callables = new ArrayList<>();
        for (Map.Entry<TaskEnvKey, Deque<SynkronizeMessage>> taskMessages : messages.entrySet()) {
            callables.add(() -> {
                try {
                    semaphore.acquire();
                    return new ExecutionHandler(taskMessages.getKey().taskId(),
                            taskMessages.getKey().envId(),
                            taskMessages.getValue(),
                            inMemorySinkCache,
                            sinkProvider,
                            pipelineCompiler)
                            .call();
                } finally {
                    semaphore.release();
                }
            });
        }
        return callables;
    }

    private record TaskEnvKey(String taskId, String envId) {
    }
}
