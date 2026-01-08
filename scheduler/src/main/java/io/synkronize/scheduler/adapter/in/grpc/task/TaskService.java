package io.synkronize.scheduler.adapter.in.grpc.task;

import com.google.protobuf.Empty;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.synkronize.scheduler.adapter.in.grpc.task.generated.CreateTaskMessage;
import io.synkronize.scheduler.adapter.in.grpc.task.generated.StopTaskMessage;
import io.synkronize.scheduler.adapter.in.grpc.task.generated.TaskGrpcService;
import io.synkronize.scheduler.core.SynkronizeTaskQueue;
import io.synkronize.scheduler.core.message.TaskMessage;
import io.synkronize.scheduler.core.message.TaskMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class TaskService implements TaskGrpcService {

    private final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final SynkronizeTaskQueue synkronizeTaskQueue;

    public TaskService(SynkronizeTaskQueue synkronizeTaskQueue) {
        this.synkronizeTaskQueue = synkronizeTaskQueue;
    }

    @Override
    @Blocking
    public Uni<Empty> startTask(CreateTaskMessage request) {
        logger.info("Received start task request for task {} with source type {}", request.getTaskId(), request.getSourceType());
        TaskMessage taskMessage = new TaskMessage(request.getEnvId(), request.getTaskId(), request.getSourceType(), request.getConfigMapMap(), TaskMessageType.START);
        try {
            synkronizeTaskQueue.put(taskMessage);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Uni.createFrom().item(Empty.getDefaultInstance());
    }

    @Override
    @Blocking
    public Uni<Empty> stopTask(StopTaskMessage request) {
        logger.info("Received stop task request for task {}", request.getTaskId());
        TaskMessage taskMessage = new TaskMessage(request.getEnvId(), request.getTaskId(), null, null, TaskMessageType.STOP);
        try {
            synkronizeTaskQueue.put(taskMessage);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Uni.createFrom().item(Empty.getDefaultInstance());
    }
}
