package io.synkronize.source.kafka;

import io.synkronize.connector.source.spi.SourceConnector;
import io.synkronize.connector.source.spi.SynkronizeConnector;
import io.synkronize.connector.source.spi.context.execution.ExecutionContext;
import io.synkronize.connector.source.spi.context.execution.ExecutionFile;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@SynkronizeConnector(type = "apache/kafka", factoryClass = KafkaSourceConnectorFactory.class)
public class KafkaSourceConnector implements SourceConnector {

    private final KafkaConsumer<byte[], byte[]> kafkaConsumer;

    private boolean isClosed = false;

    public KafkaSourceConnector(KafkaConsumer<byte[], byte[]> kafkaConsumer) {
        this.kafkaConsumer = kafkaConsumer;
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
