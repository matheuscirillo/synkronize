package io.synkronize.scheduler.infrastructure.observability.system;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.synkronize.scheduler.infrastructure.observability.system.SystemStats.Measurement.*;

public class SystemStats {

    private final Map<Measurement, Function<Long, Long>> conversionMap = buildConversionMap();

    private long[] previousTicks;

    private final CentralProcessor cpu;

    public SystemStats() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        cpu = hal.getProcessor();

        previousTicks = cpu.getSystemCpuLoadTicks();
    }

    public SystemStats(CentralProcessor cpu) {
        this.cpu = cpu;
        previousTicks = cpu.getSystemCpuLoadTicks();
    }

    public long getAvailableMemory(Measurement measurement) {
        return conversionMap.get(measurement)
                .apply(Runtime.getRuntime().freeMemory() +
                        (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory()));
    }

    public long getUsedMemory(Measurement measurement) {
        return conversionMap.get(measurement)
                .apply(Runtime.getRuntime().totalMemory());
    }

    public double getCpuUsage() {
        long[] currentTicks = cpu.getSystemCpuLoadTicks();
        double totalCpuUsage = cpu.getSystemCpuLoadBetweenTicks(previousTicks) * 100;
        previousTicks = currentTicks;

        return totalCpuUsage;
    }

    private Map<Measurement, Function<Long, Long>> buildConversionMap() {
        Map<Measurement, Function<Long, Long>> map = new HashMap<>();
        map.put(BYTES, bytes -> bytes);
        map.put(MB, bytes -> bytes / (1024 * 1024));
        map.put(GB, bytes -> bytes / (1024 * 1024 * 1024));

        return map;
    }

    public enum Measurement {
        BYTES,
        MB,
        GB;
    }
}
