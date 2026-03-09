package io.synkronize.executor.buffer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.synkronize.executor.model.SynkronizeMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class KafkaBufferReader implements BufferReader {

    private final Logger logger = LoggerFactory.getLogger(KafkaBufferReader.class);

    private final KafkaConsumer<byte[], byte[]> kafkaConsumer;
    private final ObjectMapper objectMapper;

    public KafkaBufferReader(KafkaConsumer<byte[], byte[]> kafkaConsumer, ObjectMapper objectMapper, String topic) {
        this.kafkaConsumer = kafkaConsumer;
        this.objectMapper = objectMapper;
        this.kafkaConsumer.subscribe(java.util.Collections.singletonList(topic));
    }

    @Override
    public Map<String, Deque<SynkronizeMessage>> read(Duration duration) {
        ConsumerRecords<byte[], byte[]> records = kafkaConsumer.poll(duration);
        Map<String, Deque<SynkronizeMessage>> messageQueue = new HashMap<>(records.count());
        for (ConsumerRecord<byte[], byte[]> record : records) {
            SynkronizeMessage message = deserialize(record.value());
            messageQueue.computeIfAbsent(message.taskId(), _ -> new ArrayDeque<>()).offer(message);
        }

        return messageQueue;
    }

    @Override
    public void commit(Duration duration) {
        this.kafkaConsumer.commitSync(duration);
    }

    @Override
    public void close() throws IOException {
        logger.info("Closing KafkaBufferReader");
        kafkaConsumer.close();
    }

    private SynkronizeMessage deserialize(byte[] value) {
        try {
            return objectMapper.readValue(new String(value, StandardCharsets.UTF_8), SynkronizeMessage.class);
        } catch (IOException e) {
            throw new RuntimeException("Error while deserializing synkronize message", e);
        }
    }
}
