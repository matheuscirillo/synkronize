package io.synkronize.scheduler;

import io.quarkus.runtime.Startup;
import io.synkronize.scheduler.core.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerInitializer {

    private final Logger logger = LoggerFactory.getLogger(SchedulerInitializer.class);

    private final Scheduler scheduler;

    public SchedulerInitializer(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Startup
    public void onStart() {
        logger.info("Initializing scheduler. Implementation is {}", scheduler.getClass().getCanonicalName());
        Thread schedulerThread = new Thread(() -> {
            try {
                scheduler.start();
            } catch (Exception e) {
                logger.error("Scheduler thrown an error", e);
            }
        }, "Synkronize Scheduler Thread");

        schedulerThread.start();
        logger.info("Scheduler started");
    }
}
