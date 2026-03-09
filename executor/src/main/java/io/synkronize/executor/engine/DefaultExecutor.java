package io.synkronize.executor.engine;

import io.synkronize.executor.buffer.BufferReader;
import io.synkronize.executor.model.SynkronizeMessage;
import io.synkronize.executor.observability.TaskMetrics;
import io.synkronize.executor.sink.InMemorySinkCache;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@ApplicationScoped
public class DefaultExecutor implements Executor {

    private final Logger logger = LoggerFactory.getLogger(DefaultExecutor.class);

    private final BufferReader bufferReader;
    private final TaskMetrics taskMetrics;
    private final InMemorySinkCache inMemorySinkCache;

    private final ExecutorService concurrentUnderlyingExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final Semaphore semaphore = new Semaphore(50);

    private volatile boolean isRunning;
    private volatile boolean closeCalled;

    public DefaultExecutor(BufferReader bufferReader,
                           TaskMetrics taskMetrics, InMemorySinkCache inMemorySinkCache) {
        this.bufferReader = bufferReader;
        this.taskMetrics = taskMetrics;
        this.inMemorySinkCache = inMemorySinkCache;
    }

    @Override
    public void start() {
        logger.info("DefaultExecutor started");
        this.isRunning = true;
        Throwable err = null;
        while (!closeCalled) {
            try {
                Map<String, Deque<SynkronizeMessage>> messages = bufferReader.read(Duration.ofSeconds(5));
                if (messages.isEmpty()) {
                    continue;
                }

                List<Callable<Integer>> callables = getCallables(messages);
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

    private List<Callable<Integer>> getCallables(Map<String, Deque<SynkronizeMessage>> messages) {
        List<Callable<Integer>> callables = new ArrayList<>();
        for (Map.Entry<String, Deque<SynkronizeMessage>> taskMessages : messages.entrySet()) {
            logger.info("Task {} quantity of messages: {}", taskMessages.getKey(), taskMessages.getValue().size());
            callables.add(() -> {
                semaphore.acquire();
                return new ExecutionHandler(taskMessages.getKey(), "prod",
                        taskMessages.getValue(), inMemorySinkCache)
                        .call();
            });
        }
        return callables;
    }
}
