package io.synkronize.source.kafka;

import io.synkronize.connector.source.spi.SourceConnector;
import io.synkronize.connector.source.spi.SynkronizeConnector;
import io.synkronize.connector.source.spi.context.execution.ExecutionContext;
import io.synkronize.connector.source.spi.context.execution.ExecutionFile;
import io.synkronize.connector.source.spi.context.task.Properties;
import io.synkronize.connector.source.spi.context.task.TaskContext;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SynkronizeConnector("apache/kafka")
public class KafkaSourceConnector implements SourceConnector {

    private KafkaConsumer<byte[], byte[]> kafkaConsumer;

    private boolean isClosed = false;

    @Override
    public void onSchedule(TaskContext context) {
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
        this.kafkaConsumer = new KafkaConsumer<>(consumerProps);
        this.kafkaConsumer.subscribe(Collections.singletonList(topic));
    }

    @Override
    public void onTrigger(ExecutionContext context) {
        ConsumerRecords<byte[], byte[]> records = kafkaConsumer.poll(Duration.ofSeconds(10));
        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        for (ConsumerRecord<byte[], byte[]> record : records) {
            ExecutionFile file = context.create();
            file.message(new String(record.value(), StandardCharsets.UTF_8));

            Map<String, String> attributes = new HashMap<>();
            attributes.put("#topic", record.topic());
            attributes.put("#offset", String.valueOf(record.offset()));
            attributes.put("#partition", String.valueOf(record.partition()));
            attributes.put("#timestamp", String.valueOf(record.timestamp()));
            file.attributes(attributes);

            context.write(file);
            offsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));
        }

        if (!offsets.isEmpty())
            this.kafkaConsumer.commitSync(offsets);
    }

    @Override
    public void onStop() {
        this.kafkaConsumer.close();
        this.isClosed = true;
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
    }
}
