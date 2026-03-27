package io.synkronize.source.kafka;

import io.synkronize.connector.source.spi.SourceConnectorFactory;
import io.synkronize.connector.source.spi.context.task.Properties;
import io.synkronize.connector.source.spi.context.task.TaskContext;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;

public class KafkaSourceConnectorFactory implements SourceConnectorFactory<KafkaSourceConnector> {

    @Override
    public KafkaSourceConnector create(TaskContext context) {
        Properties properties = context.getProperties();
        String bootstrapServers = properties.get("bootstrapServers").getValue();
        if (bootstrapServers == null)
            throw new IllegalArgumentException("Property 'bootstrapServers' is required");

        String groupId = properties.get("groupId").getValue();
        if (groupId == null)
            throw new IllegalArgumentException("Property 'groupId' is required");

        String topic = properties.get("topic").getValue();
        if (topic == null)
            throw new IllegalArgumentException("Property 'topic' is required");

        java.util.Properties consumerProps = new java.util.Properties();
        consumerProps.setProperty("bootstrap.servers", bootstrapServers);
        consumerProps.setProperty("group.id", groupId);
        consumerProps.setProperty("enable.auto.commit", "false");
        consumerProps.setProperty("key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        consumerProps.setProperty("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        KafkaConsumer<byte[], byte[]> kafkaConsumer = new KafkaConsumer<>(consumerProps);
        kafkaConsumer.subscribe(Collections.singletonList(topic));

        return new KafkaSourceConnector(kafkaConsumer);
    }
}
