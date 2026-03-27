package io.synkronize.source.aws.sqs;

import io.synkronize.source.testsupport.RecordingExecutionContext;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SqsSourceConnectorTest {

    private static final String QUEUE_NAME = "q1";
    private static final String QUEUE_URL = "https://sqs.example.com/123/q1";

    @Test
    void onTriggerSignalsEmptyReceiveWhenNoMessages() {
        SqsClient client = mock(SqsClient.class);
        when(client.getQueueUrl(any(GetQueueUrlRequest.class)))
                .thenReturn(GetQueueUrlResponse.builder().queueUrl(QUEUE_URL).build());
        when(client.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder().messages(List.of()).build());

        RecordingExecutionContext ctx = new RecordingExecutionContext();
        new SqsSourceConnector(QUEUE_NAME, null, client).onTrigger(ctx);

        assertTrue(ctx.isEmptyReceive());
        assertEquals(0, ctx.writtenMessagesQuantity());
        verify(client, times(0)).deleteMessageBatch(any(DeleteMessageBatchRequest.class));
    }

    @Test
    void onTriggerWritesMessagesAndDeletesBatch() {
        SqsClient client = mock(SqsClient.class);
        when(client.getQueueUrl(any(GetQueueUrlRequest.class)))
                .thenReturn(GetQueueUrlResponse.builder().queueUrl(QUEUE_URL).build());

        Message msg = Message.builder()
                .body("payload")
                .receiptHandle("rh-1")
                .messageId("mid-1")
                .md5OfBody("md5-1")
                .messageAttributes(Map.of(
                        "attr1", MessageAttributeValue.builder()
                                .stringValue("x")
                                .dataType("String")
                                .build()))
                .build();

        when(client.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder().messages(List.of(msg)).build());

        RecordingExecutionContext ctx = new RecordingExecutionContext();
        new SqsSourceConnector(QUEUE_NAME, null, client).onTrigger(ctx);

        assertFalse(ctx.isEmptyReceive());
        assertEquals(1, ctx.writtenMessagesQuantity());
        assertEquals("payload", ctx.getWrittenFiles().getFirst().getMessage());
        assertEquals(QUEUE_NAME, ctx.getWrittenFiles().getFirst().getAttributes().get("#queueName"));
        assertEquals("rh-1", ctx.getWrittenFiles().getFirst().getAttributes().get("#receiptHandle"));
        assertEquals("x", ctx.getWrittenFiles().getFirst().getAttributes().get("attr1"));

        ArgumentCaptor<DeleteMessageBatchRequest> deleteCap = ArgumentCaptor.forClass(DeleteMessageBatchRequest.class);
        verify(client).deleteMessageBatch(deleteCap.capture());
        assertEquals(QUEUE_URL, deleteCap.getValue().queueUrl());
        assertEquals("rh-1", deleteCap.getValue().entries().getFirst().receiptHandle());
        assertEquals("1", deleteCap.getValue().entries().getFirst().id());
    }

    @Test
    void onTriggerSignalsErrorWhenReceiveFails() {
        SqsClient client = mock(SqsClient.class);
        when(client.getQueueUrl(any(GetQueueUrlRequest.class)))
                .thenReturn(GetQueueUrlResponse.builder().queueUrl(QUEUE_URL).build());
        when(client.receiveMessage(any(ReceiveMessageRequest.class))).thenThrow(new RuntimeException("network"));

        RecordingExecutionContext ctx = new RecordingExecutionContext();
        new SqsSourceConnector(QUEUE_NAME, null, client).onTrigger(ctx);

        assertEquals("network", ctx.getError().getMessage());
        verify(client, times(0)).deleteMessageBatch(any(DeleteMessageBatchRequest.class));
    }

    @Test
    void onTriggerSignalsErrorWhenDeleteFails() {
        SqsClient client = mock(SqsClient.class);
        when(client.getQueueUrl(any(GetQueueUrlRequest.class)))
                .thenReturn(GetQueueUrlResponse.builder().queueUrl(QUEUE_URL).build());
        Message msg = Message.builder()
                .body("a")
                .receiptHandle("rh")
                .messageId("id")
                .md5OfBody("m")
                .messageAttributes(Map.of())
                .build();
        when(client.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder().messages(List.of(msg)).build());
        when(client.deleteMessageBatch(any(DeleteMessageBatchRequest.class))).thenThrow(new RuntimeException("delete"));

        RecordingExecutionContext ctx = new RecordingExecutionContext();
        new SqsSourceConnector(QUEUE_NAME, null, client).onTrigger(ctx);

        assertEquals("delete", ctx.getError().getMessage());
        assertEquals(1, ctx.writtenMessagesQuantity());
    }

    @Test
    void onStopClosesClientAndMarksClosed() {
        SqsClient client = mock(SqsClient.class);
        SqsSourceConnector connector = new SqsSourceConnector(QUEUE_NAME, null, client);
        connector.onStop();
        verify(client).close();
        assertTrue(connector.isClosed());
    }

    @Test
    void getQueueUrlUsesQueueOwnerWhenProvided() {
        SqsClient client = mock(SqsClient.class);
        when(client.getQueueUrl(any(GetQueueUrlRequest.class)))
                .thenReturn(GetQueueUrlResponse.builder().queueUrl(QUEUE_URL).build());
        when(client.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(ReceiveMessageResponse.builder().messages(List.of()).build());

        ArgumentCaptor<GetQueueUrlRequest> cap = ArgumentCaptor.forClass(GetQueueUrlRequest.class);
        new SqsSourceConnector(QUEUE_NAME, "123456789012", client).onTrigger(new RecordingExecutionContext());
        verify(client).getQueueUrl(cap.capture());
        assertEquals(QUEUE_NAME, cap.getValue().queueName());
        assertEquals("123456789012", cap.getValue().queueOwnerAWSAccountId());
    }
}
