package io.synkronize.scheduler.messaging.buffer;

import io.synkronize.scheduler.messaging.serializer.JsonSerializer;
import io.synkronize.scheduler.model.SynkronizeMessage;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class KafkaBuffer implements Buffer {

    private final KafkaProducer<byte[], byte[]> kafkaProducer;
    private final JsonSerializer jsonSerializer;
    private final String topic;

    public KafkaBuffer(KafkaProducer<byte[], byte[]> kafkaProducer, JsonSerializer jsonSerializer, String topic) {
        this.kafkaProducer = kafkaProducer;
        this.jsonSerializer = jsonSerializer;
        this.topic = topic;
    }

    @Override
    public void write(SynkronizeMessage message) {
        byte[] payload = jsonSerializer
                .serialize(message)
                .getBytes(StandardCharsets.UTF_8);

        ProducerRecord<byte[], byte[]> record = new ProducerRecord<>(topic, payload);

        try {
            kafkaProducer.send(record).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while writing to Kafka", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to write message to Kafka", e);
        }
    }

    @Override
    public void close() throws IOException {
        kafkaProducer.flush();
        kafkaProducer.close();
    }
}
