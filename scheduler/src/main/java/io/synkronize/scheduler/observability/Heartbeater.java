package io.synkronize.scheduler.observability;

import io.quarkus.runtime.Startup;
import io.synkronize.scheduler.utils.net.AddressResolver;
import io.synkronize.scheduler.utils.system.SystemStats;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Heartbeater {

    private final Logger logger = LoggerFactory.getLogger(Heartbeater.class);


    private final ScheduledExecutorService heartbeatScheduler;

    @Inject
    public Heartbeater(AddressResolver addressResolver) {
        this(Executors.newScheduledThreadPool(1,
                r -> new Thread(r, "Synkronize Heartbeater")));
    }

    public Heartbeater(ScheduledExecutorService heartbeatScheduler) {
        this.heartbeatScheduler = heartbeatScheduler;
    }

    @Startup
    public void start() {
        SystemStats systemStats = new SystemStats();
        heartbeatScheduler.scheduleAtFixedRate(() -> this.beat(systemStats),
                0, 30, TimeUnit.SECONDS);
    }

    private void beat(SystemStats systemStats) {
        double cpuUsage = systemStats.getCpuUsage();
        long availableMemory = systemStats.getAvailableMemory(SystemStats.Measurement.MB);
        logger.info("CPU Usage: {}%, Free memory: {}MB, Used memory: {}MB",
                "%.2f".formatted(cpuUsage),
                availableMemory,
                systemStats.getUsedMemory(SystemStats.Measurement.MB));
    }
}
