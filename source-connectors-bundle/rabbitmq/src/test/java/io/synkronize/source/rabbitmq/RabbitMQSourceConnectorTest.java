package io.synkronize.source.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;
import io.synkronize.source.testsupport.RecordingExecutionContext;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RabbitMQSourceConnectorTest {

    @Test
    void onTriggerSignalsEmptyReceiveWhenQueueDrained() {
        Channel channel = mock(Channel.class);
        Connection connection = mock(Connection.class);
        var responses = new LinkedBlockingQueue<GetResponse>();

        RecordingExecutionContext ctx = new RecordingExecutionContext();
        new RabbitMQSourceConnector(connection, channel, responses).onTrigger(ctx);

        assertTrue(ctx.isEmptyReceive());
        assertEquals(0, ctx.writtenMessagesQuantity());
    }

    @Test
    void onTriggerAcksLastDeliveryAfterWriting() throws Exception {
        Channel channel = mock(Channel.class);
        Connection connection = mock(Connection.class);
        var responses = new LinkedBlockingQueue<GetResponse>();
        Envelope envelope = new Envelope(99L, false, "ex", "rk");
        responses.add(new GetResponse(envelope, null, "msg-a".getBytes(StandardCharsets.UTF_8), Integer.MAX_VALUE));

        RecordingExecutionContext ctx = new RecordingExecutionContext();
        new RabbitMQSourceConnector(connection, channel, responses).onTrigger(ctx);

        assertEquals(1, ctx.writtenMessagesQuantity());
        assertEquals("msg-a", ctx.getWrittenFiles().getFirst().getMessage());
        verify(channel).basicAck(99L, true);
    }

    @Test
    void onTriggerSignalsErrorWhenAckFails() throws Exception {
        Channel channel = mock(Channel.class);
        doThrow(new IOException("ack")).when(channel).basicAck(anyLong(), anyBoolean());
        Connection connection = mock(Connection.class);
        var responses = new LinkedBlockingQueue<GetResponse>();
        Envelope envelope = new Envelope(1L, false, "ex", "rk");
        responses.add(new GetResponse(envelope, null, "x".getBytes(StandardCharsets.UTF_8), Integer.MAX_VALUE));

        RecordingExecutionContext ctx = new RecordingExecutionContext();
        new RabbitMQSourceConnector(connection, channel, responses).onTrigger(ctx);

        assertNotNull(ctx.getError());
        assertEquals("ack", ctx.getError().getMessage());
    }

    @Test
    void onStopClosesChannelAndConnection() throws Exception {
        Channel channel = mock(Channel.class);
        Connection connection = mock(Connection.class);
        var responses = new LinkedBlockingQueue<GetResponse>();
        RabbitMQSourceConnector connector = new RabbitMQSourceConnector(connection, channel, responses);
        connector.onStop();
        verify(channel).close();
        verify(connection).close();
        assertTrue(connector.isClosed());
    }
}
