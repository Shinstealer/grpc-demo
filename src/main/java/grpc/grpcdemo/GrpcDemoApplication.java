package grpc.grpcdemo;

import java.util.ArrayList;
import java.util.List;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.grpc.examples.hellowworld.HelloReply;
import io.grpc.examples.hellowworld.HelloRequest;
import io.grpc.examples.hellowworld.GreeterGrpc.GreeterImplBase;
import io.grpc.stub.StreamObserver;

@SpringBootApplication
public class GrpcDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(GrpcDemoApplication.class, args);
  }


  @GRpcService
  public static class GreeterService extends GreeterImplBase {

    @Override
    public void sayHelloUnary(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
      HelloReply reply = HelloReply.newBuilder().setMessage("Hi" + request.getName()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }

    @Override
    public void sayHelloServerStreaming(HelloRequest request,
        StreamObserver<HelloReply> responseObserver) {
      HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
      responseObserver.onNext(reply);
      responseObserver.onNext(reply);
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<HelloRequest> sayHelloClientStreaming(
        StreamObserver<HelloReply> responseObserver) {
      List<String> requests = new ArrayList<>();
      return new StreamObserver<HelloRequest>() {
        @Override
        public void onNext(HelloRequest request) {
          requests.add(request.getName());

        }

        @Override
        public void onError(Throwable t) {
          // TODO Auto-generated method stub

        }

        @Override
        public void onCompleted() {
          HelloReply reply =
              HelloReply.newBuilder().setMessage("Hello " + requests.toString()).build();
          responseObserver.onNext(reply);

          responseObserver.onCompleted();

        }
      };
    }

    @Override
    public StreamObserver<HelloRequest> sayHelloBidirectionalStreaming(
        StreamObserver<HelloReply> responseObserver) {
      return new StreamObserver<HelloRequest>() {
        @Override
        public void onNext(HelloRequest request) {
          HelloReply reply =
              HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
          responseObserver.onNext(reply);
          responseObserver.onNext(reply);
          responseObserver.onNext(reply);

        }

        @Override
        public void onError(Throwable t) {
          // TODO Auto-generated method stub

        }

        @Override
        public void onCompleted() {
          responseObserver.onCompleted();

        }
      };
    }


  }

}
