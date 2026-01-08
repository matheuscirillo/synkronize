package io.synkronize.scheduler.infrastructure.configuration;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

@ApplicationScoped
public class KafkaProducerConfiguration {

    private static final Logger logger =
            LoggerFactory.getLogger(KafkaProducerConfiguration.class);

    @ConfigProperty(name = "synkronize.buffer.kafka.bootstrap-servers")
    String bootstrapServers;

    @ConfigProperty(name = "synkronize.buffer.kafka.client-id", defaultValue = "synkronize")
    String clientId;

    @Produces
    @ApplicationScoped
    public KafkaProducer<byte[], byte[]> kafkaProducer() {
        logger.info("Creating KafkaProducer (bootstrapServers={})", bootstrapServers);

        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 10000);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768);

        return new KafkaProducer<>(props);
    }
}