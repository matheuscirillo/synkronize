package io.synkronize.executor.model;

import java.util.Map;

public record SinkConfiguration(String sinkId,
                                String sinkType,
                                Map<String, String> configMap) {
}
