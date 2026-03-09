package io.synkronize.executor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.properties.IfBuildProperty;
import io.synkronize.executor.buffer.BufferReader;
import io.synkronize.executor.buffer.KafkaBufferReader;
import io.synkronize.executor.buffer.MockBufferReader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class BufferReaderConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(BufferReaderConfiguration.class);

    @ConfigProperty(name = "synkronize.buffer.kafka.topic")
    String topic;

    @Produces
    @ApplicationScoped
    @IfBuildProperty(name = "synkronize.buffer.type", stringValue = "kafka")
    public BufferReader kafkaBufferReader(KafkaConsumer<byte[], byte[]> kafkaConsumer, ObjectMapper objectMapper) {
        logger.info("KafkaBufferReader registered (topic={})", topic);
        return new KafkaBufferReader(kafkaConsumer, objectMapper, topic);
    }

    @Produces
    @ApplicationScoped
    @IfBuildProperty(name = "synkronize.buffer.type", stringValue = "mock")
    public BufferReader mockBufferReader() {
        logger.info("MockBufferReader registered");
        return new MockBufferReader();
    }
}
