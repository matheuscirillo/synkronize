# <img src="https://matheuscirillo-personal-website.s3.sa-east-1.amazonaws.com/synkronize-icon.svg" width="52" height="52" style="vertical-align: middle;"> Synkronize

*Synkronize was an idea for a platform that aimed to provide a way for users to create, manage and monitor asynchronous integration tasks. **The idea was born as a simple project for learning purposes**. Since I no longer have time to work on it regularly, I made it open source for anyone who may have an interest in it*.

## What actually is *Synkronize*?

Synkronize is a platform that enables developers (and anyone) to create, manage and monitor asynchronous polyglot integration tasks that are executed continuously. If you pay close attention, you'll see it is somewhat similar to Apache NiFi and I actually took some inspiration from looking at NiFi's source code (I never meant to replace NiFi in any sense, though, and this codebase is much, much smaller than NiFi ever was).

### What does *"polyglot integration tasks"* mean in this context?

An integration task is any task that moves data between one or more applications. By polyglot, I mean that the platform is designed to support multiple communication protocols and technologies commonly used by modern systems.

For example, imagine an OMS (Order Management System) that emits `OrderCreatedEvent`, `OrderCancelledEvent` and `OrderUpdatedEvent` events and sends these events to an Apache Kafka topic. Other applications in the ecosystem subscribe to the Kafka topics and are able to receive and process these events.

Now, imagine an application that cannot be changed to subscribe to this Kafka topic (for any reason), but it does expose an HTTP endpoint for receiving events. Synkronize solves this problem by continuously executing an integration task with the **Source** being Apache Kafka and the **Sink** (destination) being this application that could not be changed. Zero code, zero changes, only configuration.

![example](https://matheuscirillo-personal-website.s3.sa-east-1.amazonaws.com/synkronize-example.png)

Kafka was just an example. Now, imagine that the OMS sends the events to an SQS queue, to RabbitMQ or to HTTP endpoints instead of a Kafka Broker. Synkronize would be able to connect to any of these technologies and fan out the events to multiple destinations. This is what makes it polyglot.

Of course, all of these connectors (Source and Sink) must be implemented, but in the ages of AI, I doubt that implementing them would be much of a challenge.

### What about the architecture and implementation?

One high-level sketch I had when I originally came up with the idea is this one:
![high-level architecture](https://matheuscirillo-personal-website.s3.sa-east-1.amazonaws.com/synkronize-arch.png)

Basically, there are three main modules:

- The Scheduler: the module that connects to the Sources, consumes data, wraps it with additional information and sends this data to the buffer as a single message;
- The Executor: the module that reads the messages from the buffer, connects to the Destinations and sends the messages to them. It also reads from MongoDB to get the necessary values to connect to the destinations (for example, keys, certificates, usernames, passwords, hostnames, IPs, etc);
- The Manager: the module that receives task creation requests from the user, validates the configurations, persists them into MongoDB and distributes the tasks between the Scheduler nodes by communicating with them through gRPC. The manager would also act as the backend for the front-end that would exist. This front-end would be used by the users to create, manage and monitor the integration tasks.

Once a task has been placed into an scheduler node, it will schedule the task to be run on a thread and will run it. Until the task crashes or is manually stopped, runs indefinitely.

The Executor is able to enrich and transform the message in runtime. It is able to execute JavaScript code (by using GraalJS, the JavaScript language implementation built on top of GraalVM), XSLT code and Jolt.

What is the buffer? It can be a Kafka Broker, RabbitMQ and other capable tools. The current code supports only Kafka as the buffer, but other implementations are possible and actually easy to switch. A mock implementation (`ConsoleBuffer`) is also present, which only prints the written messages to the console.

Beware that the implementation is nowhere near complete.

Ideally, Synkronize would have several modules, although the only ones you see in this repo are:

- Scheduler
- Source Connectors SPI (Service Provider Interface)
- Source Connectors Bundle
- Extensions Bundle (Quarkus extensions that are run during compilation)

The ideal implementation (which I did build something, but which is currently private) is a bit more complete and includes the following additional modules:

- Manager
- Executor
- Sink Connectors SPI (Service Provider Interface)

I did not make these modules public (maybe someday I will) because they are extremely underdeveloped (not that the public modules are much complete, but anyway :D)

### The Source Connector SPI

The Source Connector SPI (specifically the SourceConnector interface) is meant to be implemented by a code that wants to act as a connector that can be scheduled and run by the Scheduler.

The `SourceConnector` interface is extremely simple and is defined as follows:
```java
package io.synkronize.connector.source.spi;  
  
import io.synkronize.connector.source.spi.context.execution.ExecutionContext;  
import io.synkronize.connector.source.spi.context.task.TaskContext;  
  
import java.io.IOException;  
import java.util.concurrent.TimeoutException;  
  
public interface SourceConnector {  
  
    void onSchedule(TaskContext context);  
  
    void onTrigger(ExecutionContext context);  
  
    void onStop() throws IOException, TimeoutException;  
  
    boolean isClosed();  
  
}
```

The `onSchedule` method is triggered once (at scheduling time) and is the place to put some initialization logic, since parameters such as connection details and credentials are passed wrapped in the `context` parameter.

The `onTrigger` method is triggered on each execution of the task. An `ExecutionContext` is injected and must be used by the implementer to write messages to the buffer. Basically, it provides an adapter between the connector and the buffer.

The `onStop` method is triggered when there is a request to stop the task. This is the place to clean up resources, for example, by closing connections.

The `isClosed` method can be called by the engine to determine whether the task is closed or not. Here, the implementer can implement logic to check whether the task has been closed.

### Existing Source Connectors:

- Apache Kafka
- AWS SQS
- RabbitMQ

These can be found in the `source-connectors-bundle` module.

### Metrics

Metrics are typically exposed through the `/metrics` endpoint by using Micrometer with the Prometheus Exporter. Details on how each module expose it's metrics can be found at the README from each module.

## Final notes

Synkronize is not production-ready. It should be viewed as a learning exercise and architectural experiment rather than a finished product.

I may update the repository sporadically if I feel like experimenting further. Changes may be breaking, and branch hygiene is not guaranteed.

This project represents a personal technical challenge and a space to explore ideas around asynchronous integrations and extensible architectures.

If you’d like to reach out, feel free to contact me at: matheus@matheuscirillo.com