package io.synkronize.source.kafka;

import io.synkronize.source.testsupport.SimpleProperties;
import io.synkronize.source.testsupport.SimpleTaskContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KafkaSourceConnectorFactoryTest {

    private final KafkaSourceConnectorFactory factory = new KafkaSourceConnectorFactory();

    @Test
    void createThrowsWhenBootstrapServersMissing() {
        var ctx = ctx(props().put("groupId", "g").put("topic", "t"));
        assertThrows(IllegalArgumentException.class, () -> factory.create(ctx));
    }

    @Test
    void createThrowsWhenGroupIdMissing() {
        var ctx = ctx(props().put("bootstrapServers", "localhost:9092").put("topic", "t"));
        assertThrows(IllegalArgumentException.class, () -> factory.create(ctx));
    }

    @Test
    void createThrowsWhenTopicMissing() {
        var ctx = ctx(props().put("bootstrapServers", "localhost:9092").put("groupId", "g"));
        assertThrows(IllegalArgumentException.class, () -> factory.create(ctx));
    }

    @Test
    void createReturnsConnectorWhenPropertiesValid() {
        var ctx = ctx(props()
                .put("bootstrapServers", "localhost:9092")
                .put("groupId", "g1")
                .put("topic", "orders"));
        KafkaSourceConnector connector = factory.create(ctx);
        assertNotNull(connector);
        try {
            connector.onStop();
        } catch (Exception ignored) {
        }
    }

    private static SimpleTaskContext ctx(SimpleProperties p) {
        return new SimpleTaskContext("t1", "apache/kafka", p);
    }

    private static SimpleProperties props() {
        return new SimpleProperties();
    }
}
