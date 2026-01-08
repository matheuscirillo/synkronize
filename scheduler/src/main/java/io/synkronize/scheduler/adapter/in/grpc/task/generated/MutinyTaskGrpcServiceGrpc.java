package io.synkronize.scheduler.adapter.in.grpc.task.generated;

import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.synkronize.scheduler.adapter.in.grpc.task.generated.TaskGrpcServiceGrpc.getServiceDescriptor;

@jakarta.annotation.Generated(value = "by Mutiny Grpc generator", comments = "Source: task_svc.proto")
public final class MutinyTaskGrpcServiceGrpc implements io.quarkus.grpc.MutinyGrpc {

    private MutinyTaskGrpcServiceGrpc() {
    }

    public static MutinyTaskGrpcServiceStub newMutinyStub(io.grpc.Channel channel) {
        return new MutinyTaskGrpcServiceStub(channel);
    }

    public static class MutinyTaskGrpcServiceStub extends io.grpc.stub.AbstractStub<MutinyTaskGrpcServiceStub> implements io.quarkus.grpc.MutinyStub {

        private TaskGrpcServiceGrpc.TaskGrpcServiceStub delegateStub;

        private MutinyTaskGrpcServiceStub(io.grpc.Channel channel) {
            super(channel);
            delegateStub = TaskGrpcServiceGrpc.newStub(channel);
        }

        private MutinyTaskGrpcServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
            delegateStub = TaskGrpcServiceGrpc.newStub(channel).build(channel, callOptions);
        }

        @Override
        protected MutinyTaskGrpcServiceStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new MutinyTaskGrpcServiceStub(channel, callOptions);
        }

        public io.smallrye.mutiny.Uni<com.google.protobuf.Empty> startTask(CreateTaskMessage request) {
            return io.quarkus.grpc.stubs.ClientCalls.oneToOne(request, delegateStub::startTask);
        }

        public io.smallrye.mutiny.Uni<com.google.protobuf.Empty> stopTask(StopTaskMessage request) {
            return io.quarkus.grpc.stubs.ClientCalls.oneToOne(request, delegateStub::stopTask);
        }
    }

    public static abstract class TaskGrpcServiceImplBase implements io.grpc.BindableService {

        private String compression;

        /**
         * Set whether the server will try to use a compressed response.
         *
         * @param compression the compression, e.g {@code gzip}
         */
        public TaskGrpcServiceImplBase withCompression(String compression) {
            this.compression = compression;
            return this;
        }

        public io.smallrye.mutiny.Uni<com.google.protobuf.Empty> startTask(CreateTaskMessage request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        public io.smallrye.mutiny.Uni<com.google.protobuf.Empty> stopTask(StopTaskMessage request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        @Override
        public io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor()).addMethod(TaskGrpcServiceGrpc.getStartTaskMethod(), asyncUnaryCall(new MethodHandlers<CreateTaskMessage, com.google.protobuf.Empty>(this, METHODID_START_TASK, compression))).addMethod(TaskGrpcServiceGrpc.getStopTaskMethod(), asyncUnaryCall(new MethodHandlers<StopTaskMessage, com.google.protobuf.Empty>(this, METHODID_STOP_TASK, compression))).build();
        }
    }

    private static final int METHODID_START_TASK = 0;

    private static final int METHODID_STOP_TASK = 1;

    private static final class MethodHandlers<Req, Resp> implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>, io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>, io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>, io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {

        private final TaskGrpcServiceImplBase serviceImpl;

        private final int methodId;

        private final String compression;

        MethodHandlers(TaskGrpcServiceImplBase serviceImpl, int methodId, String compression) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
            this.compression = compression;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch(methodId) {
                case METHODID_START_TASK:
                    io.quarkus.grpc.stubs.ServerCalls.oneToOne((CreateTaskMessage) request, (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver, compression, serviceImpl::startTask);
                    break;
                case METHODID_STOP_TASK:
                    io.quarkus.grpc.stubs.ServerCalls.oneToOne((StopTaskMessage) request, (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver, compression, serviceImpl::stopTask);
                    break;
                default:
                    throw new AssertionError();
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public io.grpc.stub.StreamObserver<Req> invoke(io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch(methodId) {
                default:
                    throw new AssertionError();
            }
        }
    }
}
