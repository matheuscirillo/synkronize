# Scheduler

The Scheduler is responsible for scheduling task executions on a thread and managing their execution lifecycle.

As already said, this is nowhere near production-ready.

### How is the execution done?
Once a request to start a new task is received, the `DefaultScheduler` (the default implementation of the `Scheduler` interface) orchestrate it's initilization by calling underlying classes that effectively instantiate the corresponding connector, creates an `ExecutionHandler` for that task, saves it's metadata within the `SourceConnectorMetadataHolder`, and then schedules it execution by using the `SynkronizeTaskExecutor` class.

The `SourceConnector` is an interface defined in the Source Connectors SPI module (`io.synkronize.connector.source.spi.SourceConnector`). The **Scheduler** is able to instantiate and run classes that implement this interface.

Every class that wants to be a source connector that can be run by the **Scheduler** must implement the `SourceConnector` interface and be annotated with the `@SynkronizeConnector` annotation and provide a no-args constructor.

Simple implementation example (full implementation of an AWS SQS Source Connector can be found under the `source-connectors-bundle` module):

```java
@SynkronizeConnector("aws/sqs")
public class SqsExampleConnector implements SourceConnector {

    private SqsClient sqsClient;
    private boolean isClosed = false;

    @Override
    public void onSchedule(TaskContext context) {
        this.sqsClient = createSqsClient(context);
    }

    @Override
    public void onTrigger(ExecutionContext context) {
        ReceiveMessageResponse response = this.sqsClient.receiveMessage((ReceiveMessageRequest) null);
        response.messages().forEach(m -> {
            ExecutionFile executionFile = context.create();
            executionFile.attributes(createAttributes());
            executionFile.message(m.body());

            context.write(executionFile);
        });
    }

    @Override
    public void onStop() throws IOException, TimeoutException {
        this.sqsClient.close();
        this.isClosed = true;
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
    }
}
```

The `ExecutionContext` is created and injected by the `ExecutionHandler`. The `ExecutionContext` implementation can be found under the package `io.synkronize.scheduler.connector.context` and this package effectively implements the interfaces defined in `io.synkronize.connector.source.spi.context`.

#### What is the purpose of the `ExecutionContext`?
It is a class that provides some essential features: 
- A way to pass properties of the connector to the `SourceConnector` implementation;
- A way to create the `ExecutionFile` that will be then passed to the buffer to be written;
- A way of signaling errors to the `ExecutionHandler` so uses this info to increment error metrics and delay the next execution

#### What is the purpose of the `ExecutionFile`?
It is the class that wraps the data that will be passed to the buffer to be written. Ideally, it should be able to handle text and binary data, but as of now, it is only able to handle text data.

#### What is the purpose of the `ExecutionHandler`?
It is a class that wraps the actual executions of it's underlying `SourceConnector`. It provides simple functionalities on incrementing counters within the `TaskMetrics`, delay executions of "empty receives" and errors.

#### What is the purpose of the `TaskExecutor`?
It is a class that wraps an `ScheduledExecutorService` and implements a `Runnable` around the already runnable `ExecutionHandler`. It is done to provide a way for "auto-rescheduling" of tasks.

The following snippet is the one that does the "auto-rescheduling thing":
```java
public void schedule(ExecutionHandler executionHandler, Runnable onInterrupt) {
    scheduledExecutorService.schedule(() -> {
        try {
            if (!executionHandler.isCancelled()) {
                executionHandler.run();
            }
        } finally {
            if (!executionHandler.isCancelled()) {
                schedule(executionHandler, onInterrupt);
            } else {
                onInterrupt.run();
            }
        }
    }, executionHandler.getDelay().toMillis(), TimeUnit.MILLISECONDS);
}
```

After a task is executed, it is rescheduled to run, and this code does just that.

### System statistics
There is a class `SystemStats` (`io.synkronize.scheduler.utils.system`) that provides system statistics such as CPU usage and available memory. It uses the `oshi-core` library to gather this information. Ideally, this information would be sent to the Manager to be used during the task allocation process to define what is would be the best node to place the task on. As of now, statistics are just logged to the console.

There is also a class called `AddressResolver` (`io.synkronize.scheduler.utils.net`) that resolves the hostname of the current node. It would also be send while registering the node within the Manager, but it is currently unused.

### Metrics
Metrics are exposed through the `/metrics` endpoint by using Micrometer and Prometheus Exporter. The class `TaskMetrics` is a wrapper around Micrometer's `MeterRegistry` and provides a way to increment execution count, messages written to the buffer and errors.

### gRPC
gRPC is used to start and stop tasks. The Scheduler exposes a gRPC service that allows you to start and stop tasks. Ideally, these endpoints should be called by the Manager to place and stop tasks on the scheduler nodes. The protobuf messages are described in the `.proto` files that reside under `src/main/proto`, specifically in the file `task_svc.proto`.

This file is used during compilation to generate the Java classes for the gRPC service.

To generate the Java classes for the gRPC service, you need to run the following command from the root of the project:
```
mvn quarkus:generate-code -pl scheduler
```
This will generate the Java classes for the gRPC service in the `target/generated-sources/gprc` directory.
