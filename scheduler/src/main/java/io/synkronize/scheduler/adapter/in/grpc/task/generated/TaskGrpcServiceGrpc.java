package io.synkronize.scheduler.adapter.in.grpc.task.generated;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@io.quarkus.Generated(value = "by gRPC proto compiler (version 1.69.1)", comments = "Source: task_svc.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class TaskGrpcServiceGrpc {

    private TaskGrpcServiceGrpc() {
    }

    public static final String SERVICE_NAME = "io.synkronize.TaskGrpcService";

    // Static method descriptors that strictly reflect the proto.
    private static volatile io.grpc.MethodDescriptor<CreateTaskMessage, com.google.protobuf.Empty> getStartTaskMethod;

    @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "StartTask", requestType = CreateTaskMessage.class, responseType = com.google.protobuf.Empty.class, methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
    public static io.grpc.MethodDescriptor<CreateTaskMessage, com.google.protobuf.Empty> getStartTaskMethod() {
        io.grpc.MethodDescriptor<CreateTaskMessage, com.google.protobuf.Empty> getStartTaskMethod;
        if ((getStartTaskMethod = TaskGrpcServiceGrpc.getStartTaskMethod) == null) {
            synchronized (TaskGrpcServiceGrpc.class) {
                if ((getStartTaskMethod = TaskGrpcServiceGrpc.getStartTaskMethod) == null) {
                    TaskGrpcServiceGrpc.getStartTaskMethod = getStartTaskMethod = io.grpc.MethodDescriptor.<CreateTaskMessage, com.google.protobuf.Empty>newBuilder().setType(io.grpc.MethodDescriptor.MethodType.UNARY).setFullMethodName(generateFullMethodName(SERVICE_NAME, "StartTask")).setSampledToLocalTracing(true).setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(CreateTaskMessage.getDefaultInstance())).setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(com.google.protobuf.Empty.getDefaultInstance())).setSchemaDescriptor(new TaskGrpcServiceMethodDescriptorSupplier("StartTask")).build();
                }
            }
        }
        return getStartTaskMethod;
    }

    private static volatile io.grpc.MethodDescriptor<StopTaskMessage, com.google.protobuf.Empty> getStopTaskMethod;

    @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "StopTask", requestType = StopTaskMessage.class, responseType = com.google.protobuf.Empty.class, methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
    public static io.grpc.MethodDescriptor<StopTaskMessage, com.google.protobuf.Empty> getStopTaskMethod() {
        io.grpc.MethodDescriptor<StopTaskMessage, com.google.protobuf.Empty> getStopTaskMethod;
        if ((getStopTaskMethod = TaskGrpcServiceGrpc.getStopTaskMethod) == null) {
            synchronized (TaskGrpcServiceGrpc.class) {
                if ((getStopTaskMethod = TaskGrpcServiceGrpc.getStopTaskMethod) == null) {
                    TaskGrpcServiceGrpc.getStopTaskMethod = getStopTaskMethod = io.grpc.MethodDescriptor.<StopTaskMessage, com.google.protobuf.Empty>newBuilder().setType(io.grpc.MethodDescriptor.MethodType.UNARY).setFullMethodName(generateFullMethodName(SERVICE_NAME, "StopTask")).setSampledToLocalTracing(true).setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(StopTaskMessage.getDefaultInstance())).setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(com.google.protobuf.Empty.getDefaultInstance())).setSchemaDescriptor(new TaskGrpcServiceMethodDescriptorSupplier("StopTask")).build();
                }
            }
        }
        return getStopTaskMethod;
    }

    /**
     * Creates a new async stub that supports all call types for the service
     */
    public static TaskGrpcServiceStub newStub(io.grpc.Channel channel) {
        io.grpc.stub.AbstractStub.StubFactory<TaskGrpcServiceStub> factory = new io.grpc.stub.AbstractStub.StubFactory<TaskGrpcServiceStub>() {

            @Override
            public TaskGrpcServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
                return new TaskGrpcServiceStub(channel, callOptions);
            }
        };
        return TaskGrpcServiceStub.newStub(factory, channel);
    }

    /**
     * Creates a new blocking-style stub that supports unary and streaming output calls on the service
     */
    public static TaskGrpcServiceBlockingStub newBlockingStub(io.grpc.Channel channel) {
        io.grpc.stub.AbstractStub.StubFactory<TaskGrpcServiceBlockingStub> factory = new io.grpc.stub.AbstractStub.StubFactory<TaskGrpcServiceBlockingStub>() {

            @Override
            public TaskGrpcServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
                return new TaskGrpcServiceBlockingStub(channel, callOptions);
            }
        };
        return TaskGrpcServiceBlockingStub.newStub(factory, channel);
    }

    /**
     * Creates a new ListenableFuture-style stub that supports unary calls on the service
     */
    public static TaskGrpcServiceFutureStub newFutureStub(io.grpc.Channel channel) {
        io.grpc.stub.AbstractStub.StubFactory<TaskGrpcServiceFutureStub> factory = new io.grpc.stub.AbstractStub.StubFactory<TaskGrpcServiceFutureStub>() {

            @Override
            public TaskGrpcServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
                return new TaskGrpcServiceFutureStub(channel, callOptions);
            }
        };
        return TaskGrpcServiceFutureStub.newStub(factory, channel);
    }

    /**
     */
    public interface AsyncService {

        /**
         */
        default void startTask(CreateTaskMessage request, io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
            io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStartTaskMethod(), responseObserver);
        }

        /**
         */
        default void stopTask(StopTaskMessage request, io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
            io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStopTaskMethod(), responseObserver);
        }
    }

    /**
     * Base class for the server implementation of the service TaskGrpcService.
     */
    public static abstract class TaskGrpcServiceImplBase implements io.grpc.BindableService, AsyncService {

        @Override
        public io.grpc.ServerServiceDefinition bindService() {
            return TaskGrpcServiceGrpc.bindService(this);
        }
    }

    /**
     * A stub to allow clients to do asynchronous rpc calls to service TaskGrpcService.
     */
    public static class TaskGrpcServiceStub extends io.grpc.stub.AbstractAsyncStub<TaskGrpcServiceStub> {

        private TaskGrpcServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @Override
        protected TaskGrpcServiceStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new TaskGrpcServiceStub(channel, callOptions);
        }

        /**
         */
        public void startTask(CreateTaskMessage request, io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
            io.grpc.stub.ClientCalls.asyncUnaryCall(getChannel().newCall(getStartTaskMethod(), getCallOptions()), request, responseObserver);
        }

        /**
         */
        public void stopTask(StopTaskMessage request, io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
            io.grpc.stub.ClientCalls.asyncUnaryCall(getChannel().newCall(getStopTaskMethod(), getCallOptions()), request, responseObserver);
        }
    }

    /**
     * A stub to allow clients to do synchronous rpc calls to service TaskGrpcService.
     */
    public static class TaskGrpcServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<TaskGrpcServiceBlockingStub> {

        private TaskGrpcServiceBlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @Override
        protected TaskGrpcServiceBlockingStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new TaskGrpcServiceBlockingStub(channel, callOptions);
        }

        /**
         */
        public com.google.protobuf.Empty startTask(CreateTaskMessage request) {
            return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getStartTaskMethod(), getCallOptions(), request);
        }

        /**
         */
        public com.google.protobuf.Empty stopTask(StopTaskMessage request) {
            return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getStopTaskMethod(), getCallOptions(), request);
        }
    }

    /**
     * A stub to allow clients to do ListenableFuture-style rpc calls to service TaskGrpcService.
     */
    public static class TaskGrpcServiceFutureStub extends io.grpc.stub.AbstractFutureStub<TaskGrpcServiceFutureStub> {

        private TaskGrpcServiceFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @Override
        protected TaskGrpcServiceFutureStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new TaskGrpcServiceFutureStub(channel, callOptions);
        }

        /**
         */
        public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> startTask(CreateTaskMessage request) {
            return io.grpc.stub.ClientCalls.futureUnaryCall(getChannel().newCall(getStartTaskMethod(), getCallOptions()), request);
        }

        /**
         */
        public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> stopTask(StopTaskMessage request) {
            return io.grpc.stub.ClientCalls.futureUnaryCall(getChannel().newCall(getStopTaskMethod(), getCallOptions()), request);
        }
    }

    private static final int METHODID_START_TASK = 0;

    private static final int METHODID_STOP_TASK = 1;

    private static final class MethodHandlers<Req, Resp> implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>, io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>, io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>, io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {

        private final AsyncService serviceImpl;

        private final int methodId;

        MethodHandlers(AsyncService serviceImpl, int methodId) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch(methodId) {
                case METHODID_START_TASK:
                    serviceImpl.startTask((CreateTaskMessage) request, (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
                    break;
                case METHODID_STOP_TASK:
                    serviceImpl.stopTask((StopTaskMessage) request, (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
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

    public static io.grpc.ServerServiceDefinition bindService(AsyncService service) {
        return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor()).addMethod(getStartTaskMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(new MethodHandlers<CreateTaskMessage, com.google.protobuf.Empty>(service, METHODID_START_TASK))).addMethod(getStopTaskMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(new MethodHandlers<StopTaskMessage, com.google.protobuf.Empty>(service, METHODID_STOP_TASK))).build();
    }

    private static abstract class TaskGrpcServiceBaseDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {

        TaskGrpcServiceBaseDescriptorSupplier() {
        }

        @Override
        public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
            return TaskCreation.getDescriptor();
        }

        @Override
        public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
            return getFileDescriptor().findServiceByName("TaskGrpcService");
        }
    }

    private static final class TaskGrpcServiceFileDescriptorSupplier extends TaskGrpcServiceBaseDescriptorSupplier {

        TaskGrpcServiceFileDescriptorSupplier() {
        }
    }

    private static final class TaskGrpcServiceMethodDescriptorSupplier extends TaskGrpcServiceBaseDescriptorSupplier implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {

        private final String methodName;

        TaskGrpcServiceMethodDescriptorSupplier(String methodName) {
            this.methodName = methodName;
        }

        @Override
        public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
            return getServiceDescriptor().findMethodByName(methodName);
        }
    }

    private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

    public static io.grpc.ServiceDescriptor getServiceDescriptor() {
        io.grpc.ServiceDescriptor result = serviceDescriptor;
        if (result == null) {
            synchronized (TaskGrpcServiceGrpc.class) {
                result = serviceDescriptor;
                if (result == null) {
                    serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME).setSchemaDescriptor(new TaskGrpcServiceFileDescriptorSupplier()).addMethod(getStartTaskMethod()).addMethod(getStopTaskMethod()).build();
                }
            }
        }
        return result;
    }
}
