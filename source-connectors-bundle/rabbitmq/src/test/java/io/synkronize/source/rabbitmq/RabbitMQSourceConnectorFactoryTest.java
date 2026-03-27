package io.synkronize.source.rabbitmq;

import io.synkronize.source.testsupport.SimpleProperties;
import io.synkronize.source.testsupport.SimpleTaskContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RabbitMQSourceConnectorFactoryTest {

    private final RabbitMQSourceConnectorFactory factory = new RabbitMQSourceConnectorFactory();

    @Test
    void createThrowsWhenUsernameMissing() {
        assertThrows(RuntimeException.class,
                () -> factory.create(ctx(rabbitPropsWithoutUsername())));
    }

    @Test
    void createThrowsWhenPasswordMissing() {
        assertThrows(RuntimeException.class,
                () -> factory.create(ctx(rabbitPropsWithoutPassword())));
    }

    @Test
    void createThrowsWhenVirtualHostMissing() {
        assertThrows(RuntimeException.class,
                () -> factory.create(ctx(rabbitPropsWithoutVirtualHost())));
    }

    @Test
    void createThrowsWhenHostnameMissing() {
        assertThrows(RuntimeException.class,
                () -> factory.create(ctx(rabbitPropsWithoutHostname())));
    }

    @Test
    void createThrowsWhenPortMissing() {
        assertThrows(RuntimeException.class,
                () -> factory.create(ctx(rabbitPropsWithoutPort())));
    }

    @Test
    void createThrowsWhenQueueNameMissing() {
        assertThrows(RuntimeException.class,
                () -> factory.create(ctx(rabbitPropsWithoutQueueName())));
    }

    private static SimpleTaskContext ctx(SimpleProperties p) {
        return new SimpleTaskContext("t1", "amqp/rabbitmq", p);
    }

    private static SimpleProperties rabbitPropsWithoutUsername() {
        return new SimpleProperties()
                .put("password", "p")
                .put("virtualHost", "/")
                .put("hostname", "localhost")
                .put("port", "5672")
                .put("queueName", "q");
    }

    private static SimpleProperties rabbitPropsWithoutPassword() {
        return new SimpleProperties()
                .put("username", "u")
                .put("virtualHost", "/")
                .put("hostname", "localhost")
                .put("port", "5672")
                .put("queueName", "q");
    }

    private static SimpleProperties rabbitPropsWithoutVirtualHost() {
        return new SimpleProperties()
                .put("username", "u")
                .put("password", "p")
                .put("hostname", "localhost")
                .put("port", "5672")
                .put("queueName", "q");
    }

    private static SimpleProperties rabbitPropsWithoutHostname() {
        return new SimpleProperties()
                .put("username", "u")
                .put("password", "p")
                .put("virtualHost", "/")
                .put("port", "5672")
                .put("queueName", "q");
    }

    private static SimpleProperties rabbitPropsWithoutPort() {
        return new SimpleProperties()
                .put("username", "u")
                .put("password", "p")
                .put("virtualHost", "/")
                .put("hostname", "localhost")
                .put("queueName", "q");
    }

    private static SimpleProperties rabbitPropsWithoutQueueName() {
        return new SimpleProperties()
                .put("username", "u")
                .put("password", "p")
                .put("virtualHost", "/")
                .put("hostname", "localhost")
                .put("port", "5672");
    }
}
