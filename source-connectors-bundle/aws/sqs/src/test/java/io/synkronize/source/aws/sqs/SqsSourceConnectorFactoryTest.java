package io.synkronize.source.aws.sqs;

import io.synkronize.connector.source.spi.SourceConnector;
import io.synkronize.source.testsupport.SimpleProperties;
import io.synkronize.source.testsupport.SimpleTaskContext;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SqsSourceConnectorFactoryTest {

    private final SqsSourceConnectorFactory factory = new SqsSourceConnectorFactory();

    @Test
    void createThrowsWhenAccessKeyMissing() {
        var ctx = ctx(props().put("secretAccessKey", "s").put("region", "us-east-1").put("queueName", "q"));
        assertThrows(IllegalArgumentException.class, () -> factory.create(ctx));
    }

    @Test
    void createThrowsWhenSecretKeyMissing() {
        var ctx = ctx(props().put("accessKeyId", "a").put("region", "us-east-1").put("queueName", "q"));
        assertThrows(IllegalArgumentException.class, () -> factory.create(ctx));
    }

    @Test
    void createThrowsWhenRegionMissing() {
        var ctx = ctx(props().put("accessKeyId", "a").put("secretAccessKey", "s").put("queueName", "q"));
        assertThrows(IllegalArgumentException.class, () -> factory.create(ctx));
    }

    @Test
    void createThrowsWhenQueueNameMissing() {
        var ctx = ctx(props().put("accessKeyId", "a").put("secretAccessKey", "s").put("region", "us-east-1"));
        assertThrows(IllegalArgumentException.class, () -> factory.create(ctx));
    }

    @Test
    void createReturnsConnectorWhenPropertiesValid() {
        SqsClient mockClient = mock(SqsClient.class);
        SqsClientBuilder builder = mock(SqsClientBuilder.class);
        when(builder.credentialsProvider(any(AwsCredentialsProvider.class))).thenReturn(builder);
        when(builder.region(any(Region.class))).thenReturn(builder);
        when(builder.build()).thenReturn(mockClient);

        try (MockedStatic<SqsClient> mocked = Mockito.mockStatic(SqsClient.class)) {
            mocked.when(SqsClient::builder).thenReturn(builder);
            var ctx = ctx(props()
                    .put("accessKeyId", "a")
                    .put("secretAccessKey", "s")
                    .put("region", "us-east-1")
                    .put("queueName", "my-queue")
                    .put("queueOwnerId", "123"));
            SourceConnector connector = factory.create(ctx);
            assertInstanceOf(SqsSourceConnector.class, connector);
        }
    }

    private static SimpleTaskContext ctx(SimpleProperties p) {
        return new SimpleTaskContext("t1", "aws/sqs", p);
    }

    private static SimpleProperties props() {
        return new SimpleProperties();
    }
}
