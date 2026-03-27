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

        var bootstrapProperty = properties.get("bootstrapServers");
        if (bootstrapProperty == null)
            throw new IllegalArgumentException("Property 'bootstrapServers' is required");
        String bootstrapServers = bootstrapProperty.getValue();
        if (bootstrapServers == null)
            throw new IllegalArgumentException("Property 'bootstrapServers' is required");

        var groupProperty = properties.get("groupId");
        if (groupProperty == null)
            throw new IllegalArgumentException("Property 'groupId' is required");
        String groupId = groupProperty.getValue();
        if (groupId == null)
            throw new IllegalArgumentException("Property 'groupId' is required");

        var topicProperty = properties.get("topic");
        if (topicProperty == null)
            throw new IllegalArgumentException("Property 'topic' is required");
        String topic = topicProperty.getValue();
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
