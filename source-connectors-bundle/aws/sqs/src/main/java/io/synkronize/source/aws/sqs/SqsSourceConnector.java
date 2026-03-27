package io.synkronize.source.aws.sqs;

import io.synkronize.connector.source.spi.SourceConnector;
import io.synkronize.connector.source.spi.SynkronizeConnector;
import io.synkronize.connector.source.spi.context.execution.ExecutionContext;
import io.synkronize.connector.source.spi.context.execution.ExecutionFile;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SynkronizeConnector(type = "aws/sqs", factoryClass = SqsSourceConnectorFactory.class)
public class SqsSourceConnector implements SourceConnector {

    private SqsQueueUrl queueUrl;

    private final String queueName;
    private final String queueOwnerId;
    private boolean isClosed = false;

    private final SqsClient sqsClient;

    public SqsSourceConnector(String queueName, String queueOwnerId, SqsClient sqsClient) {
        this.queueName = queueName;
        this.queueOwnerId = queueOwnerId;
        this.sqsClient = sqsClient;
    }

    @Override
    public void onTrigger(ExecutionContext context) {
        SqsQueueUrl sqsQueueUrl = obtainQueueUrl();

        ReceiveMessageResponse response = null;

        try {
            response = this.sqsClient.receiveMessage(receiveMessageRequest(sqsQueueUrl.queueUrl()));
        } catch (Exception e) {
            context.signalError(e);
        }

        if (response != null) {
            if (response.messages().isEmpty()) {
                context.emptyReceive();
                return;
            }

            List<String> successfulReceiptHandles = new ArrayList<>();
            for (Message message : response.messages()) {
                ExecutionFile file = context.create();

                Map<String, String> attributes = buildMessageAttributes(message);
                file.message(message.body());
                file.attributes(attributes);

                context.write(file);
                successfulReceiptHandles.add(message.receiptHandle());
            }

            try {
                deleteMessages(sqsQueueUrl.queueUrl(), successfulReceiptHandles);
            } catch (Exception e) {
                context.signalError(e);
            }
        }
    }

    @Override
    public void onStop() {
        this.sqsClient.close();
        this.isClosed = true;
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
    }

    private void deleteMessages(String queueUrl, List<String> receiptHandles) {
        AtomicInteger counter = new AtomicInteger();
        if (!receiptHandles.isEmpty())
            sqsClient.deleteMessageBatch(DeleteMessageBatchRequest.builder()
                    .queueUrl(queueUrl)
                    .entries(receiptHandles.stream().map(receiptHandle -> DeleteMessageBatchRequestEntry.builder()
                            .receiptHandle(receiptHandle)
                            .id(String.valueOf(counter.incrementAndGet()))
                            .build()).toList())
                    .build());
    }

    private ReceiveMessageRequest receiveMessageRequest(String queueUrl) {
        return ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(20)
                .messageAttributeNames("*")
                .build();
    }

    private SqsQueueUrl obtainQueueUrl() {
        if (queueUrl == null || queueUrl.queueUrl() == null || queueUrl.validUntil() <= Instant.now().getEpochSecond()) {
            GetQueueUrlResponse response = sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .queueOwnerAWSAccountId(queueOwnerId)
                    .build());

            this.queueUrl = new SqsQueueUrl(Instant.now().getEpochSecond() + 1800,
                    response.queueUrl());
        }

        return this.queueUrl;
    }

    private Map<String, String> buildMessageAttributes(Message message) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("#queueName", this.queueName);
        attributes.put("#receiptHandle", message.receiptHandle());
        attributes.put("#messageId", message.messageId());
        attributes.put("#md5OfBody", message.md5OfBody());
        for (Map.Entry<String, MessageAttributeValue> messageAttribute : message.messageAttributes().entrySet()) {
            attributes.put(messageAttribute.getKey(), messageAttribute.getValue().stringValue());
        }
        return attributes;
    }
}
