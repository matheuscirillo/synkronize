package io.synkronize.scheduler.engine;

import io.synkronize.connector.source.spi.SourceConnector;
import io.synkronize.connector.source.spi.context.task.Property;
import io.synkronize.connector.source.spi.context.task.TaskContext;
import io.synkronize.scheduler.connector.ConnectorMetadata;
import io.synkronize.scheduler.connector.ConnectorRegistry;
import io.synkronize.scheduler.connector.context.PropertiesImpl;
import io.synkronize.scheduler.connector.context.PropertyImpl;
import io.synkronize.scheduler.connector.context.TaskContextImpl;
import io.synkronize.scheduler.messaging.buffer.Buffer;
import io.synkronize.scheduler.model.TaskMessage;
import io.synkronize.scheduler.model.TaskMessageType;
import io.synkronize.scheduler.messaging.MessageQueue;
import io.synkronize.scheduler.observability.TaskMetrics;
import io.synkronize.scheduler.connector.ConnectorResolver;
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

    private final MessageQueue taskQueue;
    private final ConnectorResolver connectorResolver;
    private final TaskExecutor taskExecutor;
    private final Buffer buffer;
    private final TaskMetrics taskMetrics;

    public DefaultScheduler(MessageQueue taskQueue,
                            ConnectorResolver connectorResolver,
                            TaskExecutor taskExecutor,
                            Buffer buffer, TaskMetrics taskMetrics) {
        this.taskQueue = taskQueue;
        this.connectorResolver = connectorResolver;
        this.taskExecutor = taskExecutor;
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
        SourceConnector sourceConnector = connectorResolver.resolve(taskMessage.sourceType());
        try {
            logger.info("Calling onSchedule() for source connector {}", sourceConnector.getClass().getCanonicalName());
            sourceConnector.onSchedule(createTaskContext(taskMessage));
            ExecutionHandler executionHandler = new ExecutionHandler(sourceConnector,
                    buffer,
                    taskMessage.taskId(),
                    taskMessage.envId(),
                    taskMetrics
            );
            ConnectorRegistry.put(new ConnectorMetadata(taskMessage.taskId(), taskMessage.envId(), sourceConnector, executionHandler));
            taskExecutor.schedule(executionHandler, () -> ConnectorRegistry.remove(taskMessage.envId(), taskMessage.taskId()));
            logger.info("Source connector {} started", taskMessage.sourceType());
        } catch (Exception e) {
            logger.error("An error occurred while trying to start source connector {}. Calling onStop() to clean up any resources that might have been opened", taskMessage.sourceType(), e);
            sourceConnector.onStop();
        }
    }

    private void handleStopTask(TaskMessage taskMessage) {
        ConnectorMetadata metadata = ConnectorRegistry.get(taskMessage.envId(), taskMessage.taskId());
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
