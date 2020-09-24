package grpc.grpcdemo.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.hellowworld.GreeterGrpc;
import io.grpc.examples.hellowworld.HelloReply;
import io.grpc.examples.hellowworld.HelloRequest;
import io.grpc.examples.hellowworld.GreeterGrpc.GreeterBlockingStub;
import io.grpc.examples.hellowworld.GreeterGrpc.GreeterStub;
import io.grpc.stub.StreamObserver;

public class DemoClient {

  public static void main(String[] args) throws InterruptedException {
    DemoClient client = new DemoClient("localhost", 8980);

    try {
      client.unary("tom");
    } finally {
      client.shutdown();
    }
  }


  private ManagedChannel channel;

  private GreeterBlockingStub blockingStub;

  private GreeterStub stub;

  public DemoClient(String host, int port) {
    this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    this.blockingStub = GreeterGrpc.newBlockingStub(channel);
    this.stub = GreeterGrpc.newStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public String unary(String name) {
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
    return blockingStub.sayHelloUnary(request).toString();// message: "Hello Tom"
  }

  String serverStreaming() {
    HelloRequest request = HelloRequest.newBuilder().setName("Tom").build();
    Iterator<HelloReply> replies = blockingStub.sayHelloServerStreaming(request);
    List<HelloReply> response = new ArrayList<>();

    while (replies.hasNext()) {
      response.add(replies.next());
    }
    return response.toString();// [message: "Hello Tom", message: "Hello Tom", message: "Hello Tom"]
  }


  String clientStreaming() throws Exception {
    HelloRequest request = HelloRequest.newBuilder().setName("TOM").build();
    CountDownLatch finishLatch = new CountDownLatch(1);
    List<HelloReply> response = new ArrayList<>();
    StreamObserver<HelloRequest> streamObserver =
        stub.sayHelloClientStreaming(new StreamObserver<HelloReply>() {

          @Override
          public void onNext(HelloReply reply) {
            response.add(reply);

          }

          @Override
          public void onError(Throwable t) {
            // TODO Auto-generated method stub

          }

          @Override
          public void onCompleted() {
            finishLatch.countDown();

          }

        });
    streamObserver.onNext(request);
    streamObserver.onNext(request);
    streamObserver.onNext(request);
    streamObserver.onCompleted();
    finishLatch.await(10, TimeUnit.SECONDS);
    return response.toString();// message: "Hello [Tom, Tom, Tom]"


  }

  String bidirectionalStreaming() throws Exception {
    HelloRequest request = HelloRequest.newBuilder().setName("Tom").build();
    CountDownLatch finishLatch = new CountDownLatch(1);
    List<HelloReply> response = new ArrayList<>();
    StreamObserver<HelloRequest> streamObserver =
        stub.sayHelloBidirectionalStreaming(new StreamObserver<HelloReply>() {
          @Override
          public void onNext(HelloReply reply) {
            response.add(reply);
          }

          @Override
          public void onError(Throwable t) {
            // ...
          }

          @Override
          public void onCompleted() {
            finishLatch.countDown();
          }
        });
    streamObserver.onNext(request);
    streamObserver.onNext(request);
    streamObserver.onNext(request);
    streamObserver.onCompleted();
    finishLatch.await(10, TimeUnit.SECONDS);
    return response.toString(); // [message: "Hello Tom" , message: "Hello Tom" , message: "Hello
                                // Tom" , message: "Hello Tom" , message: "Hello Tom" , message:
                                // "Hello Tom" , message: "Hello Tom" , message: "Hello Tom" ,
                                // message: "Hello Tom" ]
  }


}
