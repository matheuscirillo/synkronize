package io.synkronize.source.kafka;

import io.synkronize.source.testsupport.RecordingExecutionContext;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KafkaSourceConnectorTest {

    @SuppressWarnings("unchecked")
    @Test
    void onTriggerDoesNotCommitWhenPollReturnsNothing() {
        KafkaConsumer<byte[], byte[]> consumer = mock(KafkaConsumer.class);
        when(consumer.poll(eq(Duration.ofSeconds(10)))).thenReturn(new ConsumerRecords<>(Map.of()));

        RecordingExecutionContext ctx = new RecordingExecutionContext();
        new KafkaSourceConnector(consumer).onTrigger(ctx);

        assertEquals(0, ctx.writtenMessagesQuantity());
        verify(consumer, never()).commitSync(any(Map.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void onTriggerWritesRecordsAndCommitsOffsets() {
        KafkaConsumer<byte[], byte[]> consumer = mock(KafkaConsumer.class);
        TopicPartition tp = new TopicPartition("events", 2);
        ConsumerRecord<byte[], byte[]> record = new ConsumerRecord<>(
                "events", 2, 10L, null, "hello".getBytes(StandardCharsets.UTF_8));
        when(consumer.poll(eq(Duration.ofSeconds(10))))
                .thenReturn(new ConsumerRecords<>(Map.of(tp, List.of(record))));

        RecordingExecutionContext ctx = new RecordingExecutionContext();
        new KafkaSourceConnector(consumer).onTrigger(ctx);

        assertEquals(1, ctx.writtenMessagesQuantity());
        assertEquals("hello", ctx.getWrittenFiles().getFirst().getMessage());
        assertEquals("events", ctx.getWrittenFiles().getFirst().getAttributes().get("#topic"));
        assertEquals("2", ctx.getWrittenFiles().getFirst().getAttributes().get("#partition"));
        assertEquals("10", ctx.getWrittenFiles().getFirst().getAttributes().get("#offset"));

        ArgumentCaptor<Map<TopicPartition, OffsetAndMetadata>> cap = ArgumentCaptor.forClass(Map.class);
        verify(consumer).commitSync(cap.capture());
        assertEquals(1, cap.getValue().size());
        assertEquals(11L, cap.getValue().get(tp).offset());
    }

    @Test
    void onStopClosesConsumer() {
        KafkaConsumer<byte[], byte[]> consumer = mock(KafkaConsumer.class);
        KafkaSourceConnector connector = new KafkaSourceConnector(consumer);
        connector.onStop();
        verify(consumer).close();
        assertTrue(connector.isClosed());
    }
}
