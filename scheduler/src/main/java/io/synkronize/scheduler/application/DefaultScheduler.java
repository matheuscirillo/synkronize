package io.synkronize.scheduler.application;

import io.synkronize.connector.source.spi.SourceConnector;
import io.synkronize.connector.source.spi.context.task.Property;
import io.synkronize.connector.source.spi.context.task.TaskContext;
import io.synkronize.scheduler.application.context.task.PropertiesImpl;
import io.synkronize.scheduler.application.context.task.PropertyImpl;
import io.synkronize.scheduler.application.context.task.TaskContextImpl;
import io.synkronize.scheduler.application.metrics.TaskMetrics;
import io.synkronize.scheduler.core.Scheduler;
import io.synkronize.scheduler.core.SynkronizeTaskQueue;
import io.synkronize.scheduler.core.buffer.Buffer;
import io.synkronize.scheduler.core.connector.SourceConnectorMetadata;
import io.synkronize.scheduler.core.connector.SourceConnectorMetadataHolder;
import io.synkronize.scheduler.core.message.TaskMessage;
import io.synkronize.scheduler.core.message.TaskMessageType;
import io.synkronize.scheduler.core.resolver.SourceConnectorResolver;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

@ApplicationScoped
public class DefaultScheduler implements Scheduler, Closeable {

    private final Logger logger = LoggerFactory.getLogger(DefaultScheduler.class);
    private boolean isRunning;
    private boolean closeCalled;

    private final SynkronizeTaskQueue taskQueue;
    private final SourceConnectorResolver sourceConnectorResolver;
    private final SynkronizeTaskExecutor synkronizeTaskExecutor;
    private final Buffer buffer;
    private final TaskMetrics taskMetrics;

    public DefaultScheduler(SynkronizeTaskQueue taskQueue,
                            SourceConnectorResolver sourceConnectorResolver,
                            SynkronizeTaskExecutor synkronizeTaskExecutor,
                            Buffer buffer, TaskMetrics taskMetrics) {
        this.taskQueue = taskQueue;
        this.sourceConnectorResolver = sourceConnectorResolver;
        this.synkronizeTaskExecutor = synkronizeTaskExecutor;
        this.buffer = buffer;
        this.taskMetrics = taskMetrics;
    }

    @Override
    public void start() {
        logger.info("DefaultScheduler started");
        this.isRunning = true;
        while (!this.closeCalled) {
            try {
                TaskMessage taskMessage = taskQueue.poll(60, SECONDS);
                if (taskMessage != null) {
                    logger.info("TaskMessage {} received", taskMessage);
                    if (taskMessage.messageType() == TaskMessageType.START) {
                        handleStartTask(taskMessage);
                    } else if (taskMessage.messageType() == TaskMessageType.STOP) {
                        handleStopTask(taskMessage);
                    }
                }
            } catch (Throwable e) {
                logger.error("An error occurred", e);
            }
        }
        this.isRunning = false;
        logger.info("DefaultScheduler stopped");
    }

    private void handleStartTask(TaskMessage taskMessage) throws IOException, TimeoutException {
        SourceConnector sourceConnector = sourceConnectorResolver.resolve(taskMessage.sourceType());
        try {
            logger.info("Calling onSchedule() for source connector {}", sourceConnector.getClass().getCanonicalName());
            sourceConnector.onSchedule(createTaskContext(taskMessage));
            ExecutionHandler executionHandler = new ExecutionHandler(sourceConnector,
                    buffer,
                    taskMessage.taskId(),
                    taskMessage.envId(),
                    taskMetrics
            );
            SourceConnectorMetadataHolder.put(new SourceConnectorMetadata(taskMessage.taskId(), taskMessage.envId(), sourceConnector, executionHandler));
            synkronizeTaskExecutor.schedule(executionHandler, () -> SourceConnectorMetadataHolder.remove(taskMessage.envId(), taskMessage.taskId()));
            logger.info("Source connector {} started", taskMessage.sourceType());
        } catch (Exception e) {
            logger.error("An error occurred while trying to start source connector {}. Calling onStop() to clean up any resources that might have been opened", taskMessage.sourceType(), e);
            sourceConnector.onStop();
        }
    }

    private void handleStopTask(TaskMessage taskMessage) {
        SourceConnectorMetadata metadata = SourceConnectorMetadataHolder.get(taskMessage.envId(), taskMessage.taskId());
        ExecutionHandler executionHandler = metadata.getExecutionHandler();
        logger.info("Calling onStop() for source connector {} with execution handler {}", metadata, executionHandler);
        executionHandler.cancel();
    }

    private TaskContext createTaskContext(TaskMessage taskMessage) {
        Map<String, Property> properties = new HashMap<>();
        for (Map.Entry<String, String> cfg : taskMessage.configMap().entrySet()) {
            properties.put(cfg.getKey(), new PropertyImpl(cfg.getKey(), cfg.getValue()));
        }

        return new TaskContextImpl(taskMessage.taskId(), taskMessage.sourceType(), new PropertiesImpl(properties));
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public boolean isClosed() {
        return this.closeCalled;
    }

    @Override
    public void close() {
        this.closeCalled = true;
    }
}
