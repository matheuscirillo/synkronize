package io.synkronize.scheduler.adapter.in.grpc.task.generated;

import io.grpc.BindableService;
import io.quarkus.grpc.GrpcService;
import io.quarkus.grpc.MutinyBean;

@jakarta.annotation.Generated(value = "by Mutiny Grpc generator", comments = "Source: task_svc.proto")
public class TaskGrpcServiceBean extends MutinyTaskGrpcServiceGrpc.TaskGrpcServiceImplBase implements BindableService, MutinyBean {

    private final TaskGrpcService delegate;

    TaskGrpcServiceBean(@GrpcService TaskGrpcService delegate) {
        this.delegate = delegate;
    }

    @Override
    public io.smallrye.mutiny.Uni<com.google.protobuf.Empty> startTask(CreateTaskMessage request) {
        try {
            return delegate.startTask(request);
        } catch (UnsupportedOperationException e) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }
    }

    @Override
    public io.smallrye.mutiny.Uni<com.google.protobuf.Empty> stopTask(StopTaskMessage request) {
        try {
            return delegate.stopTask(request);
        } catch (UnsupportedOperationException e) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }
    }
}
