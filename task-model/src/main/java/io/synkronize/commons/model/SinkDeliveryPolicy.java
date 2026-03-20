package io.synkronize.commons.model;

public class SinkDeliveryPolicy {

    private int maxAttempts;
    private long backoffMs;
    private long timeoutMs;

    public SinkDeliveryPolicy() {
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public long getBackoffMs() {
        return backoffMs;
    }

    public void setBackoffMs(long backoffMs) {
        this.backoffMs = backoffMs;
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    @Override
    public String toString() {
        return "SinkDeliveryPolicy{" +
                "maxAttempts=" + maxAttempts +
                ", backoffMs=" + backoffMs +
                ", timeoutMs=" + timeoutMs +
                '}';
    }
}
