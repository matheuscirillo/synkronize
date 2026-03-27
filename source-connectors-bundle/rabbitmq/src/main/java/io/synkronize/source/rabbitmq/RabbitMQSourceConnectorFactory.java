package io.synkronize.source.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;
import io.synkronize.connector.source.spi.SourceConnectorFactory;
import io.synkronize.connector.source.spi.context.task.Property;
import io.synkronize.connector.source.spi.context.task.TaskContext;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

public class RabbitMQSourceConnectorFactory implements SourceConnectorFactory<RabbitMQSourceConnector> {

    private static final int PREFETCH_COUNT = 100;

    @Override
    public RabbitMQSourceConnector create(TaskContext context) {
        try {
            BlockingQueue<GetResponse> responses = new LinkedBlockingQueue<>(PREFETCH_COUNT);
            Connection connection = createConnection(context);
            Channel channel = connection.createChannel();

            Property queueNameProperty = context.getProperties().get("queueName");
            if (queueNameProperty == null)
                throw new IllegalArgumentException("Property 'queueName' is required");

            channel.basicQos(PREFETCH_COUNT);
            Consumer consumer = createConsumer(channel, responses);
            channel.basicConsume(queueNameProperty.getValue(), false, consumer);

            return new RabbitMQSourceConnector(connection, channel, responses);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection createConnection(TaskContext context) throws IOException, TimeoutException {
        Property usernameProperty = context.getProperties().get("username");
        if (usernameProperty == null)
            throw new IllegalArgumentException("Property 'username' is required");

        Property passwordProperty = context.getProperties().get("password");
        if (passwordProperty == null)
            throw new IllegalArgumentException("Property 'password' is required");

        Property virtualHostProperty = context.getProperties().get("virtualHost");
        if (virtualHostProperty == null)
            throw new IllegalArgumentException("Property 'virtualHost' is required");

        Property hostnameProperty = context.getProperties().get("hostname");
        if (hostnameProperty == null)
            throw new IllegalArgumentException("Property 'hostname' is required");

        Property portProperty = context.getProperties().get("port");
        if (portProperty == null)
            throw new IllegalArgumentException("Property 'port' is required");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(usernameProperty.getValue());
        factory.setPassword(passwordProperty.getValue());
        factory.setVirtualHost(virtualHostProperty.getValue());
        factory.setHost(hostnameProperty.getValue());
        factory.setPort(Integer.parseInt(portProperty.getValue()));

        return factory.newConnection();
    }

    private Consumer createConsumer(Channel channel, BlockingQueue<GetResponse> responses) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                try {
                    responses.put(new GetResponse(envelope, properties, body, Integer.MAX_VALUE));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
    }
}
