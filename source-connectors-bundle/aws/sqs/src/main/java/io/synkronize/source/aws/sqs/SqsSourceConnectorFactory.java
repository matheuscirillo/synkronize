package io.synkronize.source.aws.sqs;

import io.synkronize.connector.source.spi.SourceConnectorFactory;
import io.synkronize.connector.source.spi.context.task.Property;
import io.synkronize.connector.source.spi.context.task.TaskContext;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

public class SqsSourceConnectorFactory implements SourceConnectorFactory<SqsSourceConnector> {

    @Override
    public SqsSourceConnector create(TaskContext context) {
        final String QUEUE_NAME_PROPERTY = "queueName";
        final String ACCESS_KEY_ID_PROPERTY = "accessKeyId";
        final String SECRET_ACCESS_KEY_PROPERTY = "secretAccessKey";
        final String REGION_PROPERTY = "region";
        final String QUEUE_OWNER_ID_PROPERTY = "queueOwnerId";

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

        String queueName = queueNameProperty.getValue();
        String queueOwnerId = context.getProperties().get(QUEUE_OWNER_ID_PROPERTY) != null ? context.getProperties().get(QUEUE_OWNER_ID_PROPERTY).getValue() : null;

        SqsClient client = SqsClient.builder()
                .credentialsProvider(credentialsProvider(accessKeyProperty.getValue(),
                        secretKeyProperty.getValue()))
                .region(Region.of(regionProperty.getValue()))
                .build();

        return new SqsSourceConnector(queueName, queueOwnerId, client);
    }

    private StaticCredentialsProvider credentialsProvider(String accessKeyId, String secretAccessKey) {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey));
    }

}
