package io.synkronize.scheduler.config;

import io.quarkus.arc.properties.IfBuildProperty;
import io.synkronize.scheduler.messaging.buffer.Buffer;
import io.synkronize.scheduler.messaging.buffer.ConsoleBuffer;
import io.synkronize.scheduler.messaging.buffer.KafkaBuffer;
import io.synkronize.scheduler.messaging.serializer.JsonSerializer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class BufferConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(BufferConfiguration.class);

    @Inject
    JsonSerializer jsonSerializer;

    @Inject
    KafkaProducer<byte[], byte[]> kafkaProducer;

    @ConfigProperty(name = "synkronize.buffer.kafka.topic")
    String kafkaTopic;

    @Produces
    @ApplicationScoped
    @IfBuildProperty(name = "synkronize.buffer.type", stringValue = "console")
    public Buffer consoleBuffer() {
        logger.info("ConsoleBuffer registered");
        return new ConsoleBuffer(jsonSerializer);
    }

    @Produces
    @ApplicationScoped
    @IfBuildProperty(name = "synkronize.buffer.type", stringValue = "kafka")
    public Buffer kafkaBuffer() {
        logger.info("KafkaBuffer registered (topic={})", kafkaTopic);
        return new KafkaBuffer(kafkaProducer, jsonSerializer, kafkaTopic);
    }
}
