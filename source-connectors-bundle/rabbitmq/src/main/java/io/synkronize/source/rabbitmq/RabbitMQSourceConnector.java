package io.synkronize.source.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;
import io.synkronize.connector.source.spi.SourceConnector;
import io.synkronize.connector.source.spi.SynkronizeConnector;
import io.synkronize.connector.source.spi.context.execution.ExecutionContext;
import io.synkronize.connector.source.spi.context.execution.ExecutionFile;
import io.synkronize.connector.source.spi.context.task.Property;
import io.synkronize.connector.source.spi.context.task.TaskContext;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

@SynkronizeConnector("amqp/rabbitmq")
public class RabbitMQSourceConnector implements SourceConnector {

    private Connection connection;
    private Channel channel;

    private BlockingQueue<GetResponse> responses;
    private boolean isClosed = false;

    private final int PREFETCH_COUNT = 100;

    @Override
    public void onSchedule(TaskContext context) {
        try {
            responses = new LinkedBlockingQueue<>(PREFETCH_COUNT);
            this.connection = createConnection(context);
            this.channel = connection.createChannel();
            Consumer consumer = createConsumer();

            Property queueNameProperty = context.getProperties().get("queueName");
            if (queueNameProperty == null)
                throw new IllegalArgumentException("Property 'queueName' is required");

            boolean autoAck = false;
            this.channel.basicQos(PREFETCH_COUNT);
            this.channel.basicConsume(queueNameProperty.getValue(), autoAck, consumer);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
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

    private Consumer createConsumer() {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                try {
                    responses.put(new GetResponse(envelope, properties, body, Integer.MAX_VALUE));
                } catch (InterruptedException e) {
                    // improvement
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        };
    }
}
