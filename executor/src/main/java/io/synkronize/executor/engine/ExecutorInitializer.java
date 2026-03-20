package io.synkronize.executor.engine;

import io.quarkus.runtime.Startup;
import io.synkronize.executor.sink.provider.SinkProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorInitializer {

    private final Logger logger = LoggerFactory.getLogger(ExecutorInitializer.class);

    private final Executor executor;

    public ExecutorInitializer(Executor executor) {
        this.executor = executor;
    }

    @Startup
    public void onStart() {
        logger.info("Initializing executor. Implementation is {}", executor.getClass().getCanonicalName());
        Thread executorThread = new Thread(() -> {
            try {
                executor.start();
            } catch (Exception e) {
                logger.error("Executor thrown an error", e);
            }
        }, "Synkronize Executor Thread");

        executorThread.start();
        logger.info("Executor started");
    }
}
