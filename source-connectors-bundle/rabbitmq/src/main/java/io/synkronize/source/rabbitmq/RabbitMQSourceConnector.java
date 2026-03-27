package io.synkronize.source.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;
import io.synkronize.connector.source.spi.SourceConnector;
import io.synkronize.connector.source.spi.SynkronizeConnector;
import io.synkronize.connector.source.spi.context.execution.ExecutionContext;
import io.synkronize.connector.source.spi.context.execution.ExecutionFile;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

@SynkronizeConnector(type = "amqp/rabbitmq", factoryClass = RabbitMQSourceConnectorFactory.class)
public class RabbitMQSourceConnector implements SourceConnector {

    private final Connection connection;
    private final Channel channel;
    private final BlockingQueue<GetResponse> responses;

    private boolean isClosed = false;

    public RabbitMQSourceConnector(Connection connection,
                                   Channel channel,
                                   BlockingQueue<GetResponse> responses) {
        this.connection = connection;
        this.channel = channel;
        this.responses = responses;
    }

    @Override
    public void onTrigger(ExecutionContext context) {
        boolean isEmpty = responses.isEmpty();
        if (isEmpty) {
            context.emptyReceive();
            return;
        }

        GetResponse response;
        GetResponse last = null;
        try {
            while ((response = responses.poll()) != null) {
                ExecutionFile file = context.create();
                file.message(new String(response.getBody()));
                context.write(file);
                last = response;
            }

            if (last != null) {
                try {
                    this.channel.basicAck(last.getEnvelope().getDeliveryTag(), true);
                } catch (IOException e) {
                    context.signalError(e);
                }
            }
        } catch (Exception e) {
            context.signalError(e);
        }
    }

    @Override
    public void onStop() throws IOException, TimeoutException {
        this.channel.close();
        this.connection.close();
        this.isClosed = true;
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
    }
}
