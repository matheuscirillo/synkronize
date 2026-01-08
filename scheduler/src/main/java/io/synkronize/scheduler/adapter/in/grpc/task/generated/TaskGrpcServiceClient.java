package io.synkronize.scheduler.adapter.in.grpc.task.generated;

import io.quarkus.grpc.MutinyClient;

import java.util.function.BiFunction;

@jakarta.annotation.Generated(value = "by Mutiny Grpc generator", comments = "Source: task_svc.proto")
public class TaskGrpcServiceClient implements TaskGrpcService, MutinyClient<MutinyTaskGrpcServiceGrpc.MutinyTaskGrpcServiceStub> {

    private final MutinyTaskGrpcServiceGrpc.MutinyTaskGrpcServiceStub stub;

    public TaskGrpcServiceClient(String name, io.grpc.Channel channel, BiFunction<String, MutinyTaskGrpcServiceGrpc.MutinyTaskGrpcServiceStub, MutinyTaskGrpcServiceGrpc.MutinyTaskGrpcServiceStub> stubConfigurator) {
        this.stub = stubConfigurator.apply(name, MutinyTaskGrpcServiceGrpc.newMutinyStub(channel));
    }

    private TaskGrpcServiceClient(MutinyTaskGrpcServiceGrpc.MutinyTaskGrpcServiceStub stub) {
        this.stub = stub;
    }

    public TaskGrpcServiceClient newInstanceWithStub(MutinyTaskGrpcServiceGrpc.MutinyTaskGrpcServiceStub stub) {
        return new TaskGrpcServiceClient(stub);
    }

    @Override
    public MutinyTaskGrpcServiceGrpc.MutinyTaskGrpcServiceStub getStub() {
        return stub;
    }

    @Override
    public io.smallrye.mutiny.Uni<com.google.protobuf.Empty> startTask(CreateTaskMessage request) {
        return stub.startTask(request);
    }

    @Override
    public io.smallrye.mutiny.Uni<com.google.protobuf.Empty> stopTask(StopTaskMessage request) {
        return stub.stopTask(request);
    }
}
