package io.synkronize.scheduler.api.grpc;

import com.google.protobuf.Empty;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.synkronize.scheduler.api.grpc.contract.CreateTaskMessage;
import io.synkronize.scheduler.api.grpc.contract.StopTaskMessage;
import io.synkronize.scheduler.api.grpc.contract.TaskGrpcService;
import io.synkronize.scheduler.messaging.MessageQueue;
import io.synkronize.scheduler.model.TaskMessage;
import io.synkronize.scheduler.model.TaskMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class TaskService implements TaskGrpcService {

    private final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final MessageQueue messageQueue;

    public TaskService(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    @Blocking
    public Uni<Empty> startTask(CreateTaskMessage request) {
        logger.info("Received start task request for task {} with source type {}", request.getTaskId(), request.getSourceType());
        TaskMessage taskMessage = new TaskMessage(request.getEnvId(), request.getTaskId(), request.getSourceType(), request.getConfigMapMap(), TaskMessageType.START);
        try {
            messageQueue.put(taskMessage);
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
            messageQueue.put(taskMessage);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Uni.createFrom().item(Empty.getDefaultInstance());
    }
}
