package io.synkronize.source.aws.sqs;

import io.synkronize.connector.source.spi.SourceConnector;
import io.synkronize.connector.source.spi.SynkronizeConnector;
import io.synkronize.connector.source.spi.context.execution.ExecutionContext;
import io.synkronize.connector.source.spi.context.execution.ExecutionFile;
import io.synkronize.connector.source.spi.context.task.Property;
import io.synkronize.connector.source.spi.context.task.TaskContext;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
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

@SynkronizeConnector("aws/sqs")
public class SqsSourceConnector implements SourceConnector {

    private final String QUEUE_NAME_PROPERTY = "queueName";
    private final String ACCESS_KEY_ID_PROPERTY = "accessKeyId";
    private final String SECRET_ACCESS_KEY_PROPERTY = "secretAccessKey";
    private final String REGION_PROPERTY = "region";
    private final String QUEUE_OWNER_ID_PROPERTY = "queueOwnerId";

    private SqsQueueUrl queueUrl;
    private String queueName;
    private String queueOwnerId;
    private boolean isClosed = false;

    private SqsClient sqsClient;

    @Override
    public void onSchedule(TaskContext context) {
        Property accessKeyProperty = context.getProperties().get(ACCESS_KEY_ID_PROPERTY);
        if (accessKeyProperty == null)
            throw new IllegalArgumentException("Property 'accessKeyId' is required");

        Property secretKeyProperty = context.getProperties().get(SECRET_ACCESS_KEY_PROPERTY);
        if (secretKeyProperty == null)
            throw new IllegalArgumentException("Property 'secretAccessKey' is required");

        Property regionProperty = context.getProperties().get(REGION_PROPERTY);
        if (regionProperty == null)
            throw new IllegalArgumentException("Property 'region' is required");

        Property queueNameProperty = context.getProperties().get(QUEUE_NAME_PROPERTY);
        if (queueNameProperty == null)
            throw new IllegalArgumentException("Property 'queueName' is required");

        this.queueName = queueNameProperty.getValue();
        this.queueOwnerId = context.getProperties().get(QUEUE_OWNER_ID_PROPERTY) != null ? context.getProperties().get(QUEUE_OWNER_ID_PROPERTY).getValue() : null;

        this.sqsClient = SqsClient.builder()
                .credentialsProvider(credentialsProvider(accessKeyProperty.getValue(), secretKeyProperty.getValue()))
                .region(Region.of(regionProperty.getValue()))
                .build();
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

    private static StaticCredentialsProvider credentialsProvider(String accessKeyId, String secretAccessKey) {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey));
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
