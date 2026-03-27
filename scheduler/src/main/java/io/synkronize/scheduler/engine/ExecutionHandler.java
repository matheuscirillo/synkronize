package io.synkronize.scheduler.engine;

import io.synkronize.connector.source.spi.SourceConnector;
import io.synkronize.connector.source.spi.SynkronizeConnector;
import io.synkronize.scheduler.connector.context.ExecutionContextImpl;
import io.synkronize.scheduler.messaging.buffer.Buffer;
import io.synkronize.scheduler.observability.TaskMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class ExecutionHandler implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(ExecutionHandler.class);

    private Duration delay;
    private boolean firstRun = true;
    private boolean isCancelled = false;

    private final SourceConnector connector;
    private final Buffer buffer;
    private final String taskId;
    private final String envId;
    private final TaskMetrics taskMetrics;

    private final String connectorType;
    private final Duration defaultDelay;

    public ExecutionHandler(SourceConnector connector, Buffer buffer, String taskId, String envId, TaskMetrics taskMetrics) {
        this.connector = connector;
        this.buffer = buffer;
        this.taskId = taskId;
        this.envId = envId;
        this.taskMetrics = taskMetrics;

        this.connectorType = connector.getClass().getAnnotation(SynkronizeConnector.class).type();
        this.defaultDelay = Duration.ofMillis(10);
        this.delay = defaultDelay;
    }

    @Override
    public void run() {
        Throwable error;
        boolean mustDelayNextExecution = false;
        if (isCancelled) {
            logger.info("Task {} has been cancelled. SourceConnector state: {}", taskId, connector.isClosed() ? "closed" : "open");
            return;
        }

        logger.trace("Running execution handler for {}", taskId);
        ExecutionContextImpl context = new ExecutionContextImpl(buffer, taskId, envId);
        try {
            connector.onTrigger(context);
            error = context.getError();
        } catch (Throwable e) {
            mustDelayNextExecution = true;
            logger.error("Exception occurred onTrigger() of source connector", e);
            error = context.getError() == null ? e : context.getError();
        }

        this.firstRun = false;

        incrementMetrics(context, error);

        if (!isCancelled) {
            if (context.isEmptyReceive()) {
                mustDelayNextExecution = true;
                logger.debug("Empty receive signaled for task {}. Execution will be delayed", taskId);
            } else {
                this.delay = defaultDelay;
            }
        } else {
            logger.info("Task {} has been cancelled. SourceConnector state: {}", taskId, connector.isClosed() ? "closed" : "open");
            mustDelayNextExecution = false;
        }

        if (mustDelayNextExecution) {
            delayNextExecution();
            logger.debug("Task {} - new delay in milliseconds is {}", taskId, this.delay.toMillis());
        }
    }

    private void incrementMetrics(ExecutionContextImpl context, Throwable error) {
        this.taskMetrics.incrementExecutions(taskId, connectorType);
        this.taskMetrics.incrementMessages(taskId, connectorType, context.writtenMessagesQuantity());
        if (error != null) {
            this.taskMetrics.incrementError(taskId, connectorType, error);
        }
    }

    public void cancel() {
        logger.info("Cancelling execution handler for {}", taskId);
        this.isCancelled = true;
        try {
            this.connector.onStop();
        } catch (IOException | TimeoutException e) {
            // needs improvement
            logger.error("Exception occurred onStop() of source connector", e);
            // although it is rare for an underlying resource to remain opened if an
            // IOException is thrown during it's closing, it is still better to check
            // if the connector has really been closed by calling connector#isClosed
            // this is a TODO since it needs a complete improvement of this code
        }
        logger.info("Canceled execution handler for {}", taskId);
    }

    public String getEnvId() {
        return envId;
    }

    public Duration getDelay() {
        if (firstRun)
            return Duration.ZERO;

        return delay;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    private void delayNextExecution() {
        // TODO dynamically add delays based on some task configuration?
        // as of now, it adds 5 seconds of delay to the next execution
        this.delay = Duration.ofSeconds(5L + defaultDelay.getSeconds());
    }

}
