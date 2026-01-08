package io.synkronize.scheduler.adapter.in.grpc.task.generated;

import io.quarkus.grpc.MutinyService;

@jakarta.annotation.Generated(value = "by Mutiny Grpc generator", comments = "Source: task_svc.proto")
public interface TaskGrpcService extends MutinyService {

    io.smallrye.mutiny.Uni<com.google.protobuf.Empty> startTask(CreateTaskMessage request);

    io.smallrye.mutiny.Uni<com.google.protobuf.Empty> stopTask(StopTaskMessage request);
}
