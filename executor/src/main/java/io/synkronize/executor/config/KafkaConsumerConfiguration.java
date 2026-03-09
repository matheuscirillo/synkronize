package io.synkronize.executor.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

@ApplicationScoped
public class KafkaConsumerConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerConfiguration.class);

    @ConfigProperty(name = "synkronize.buffer.kafka.bootstrap-servers")
    String bootstrapServers;

    @ConfigProperty(name = "synkronize.buffer.kafka.group-id")
    String groupId;

    @Produces
    @ApplicationScoped
    public KafkaConsumer<byte[], byte[]> kafkaConsumer() {
        logger.info("Creating KafkaConsumer (bootstrapServers={}, groupId={})", bootstrapServers, groupId);
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new KafkaConsumer<>(props);
    }
}
