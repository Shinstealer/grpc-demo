package grpc.grpcdemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import grpc.grpcdemo.GrpcDemoApplication.GreeterService;
import io.grpc.examples.hellowworld.GreeterGrpc;
import io.grpc.examples.hellowworld.HelloReply;
import io.grpc.examples.hellowworld.HelloRequest;
import io.grpc.examples.hellowworld.GreeterGrpc.GreeterBlockingStub;
import io.grpc.testing.GrpcServerRule;

public class GrpcServerSideTest {

  @Rule
  public GrpcServerRule grpcServerRule = new GrpcServerRule().directExecutor();

  @Test
  public void testUnary() {

    grpcServerRule.getServiceRegistry().addService(new GreeterService());
    GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(grpcServerRule.getChannel());

    String testName = "test name";
    HelloReply reply =
        blockingStub.sayHelloUnary(HelloRequest.newBuilder().setName(testName).build());

    assertEquals("Hi" + testName, reply.getMessage());
  }

  @Test
  public void testServerStreaming() {

    grpcServerRule.getServiceRegistry().addService(new GreeterService());
    GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(grpcServerRule.getChannel());

    String testName = "test name";
    Iterator<HelloReply> replies =
        blockingStub.sayHelloServerStreaming(HelloRequest.newBuilder().setName(testName).build());

    List<HelloReply> reply = new ArrayList<>();

    while (replies.hasNext()) {
      reply.add(replies.next());
    }

    assertEquals(reply.size(), 3);
    assertEquals(reply.get(0).getMessage(), "Hello " + testName);
    assertEquals(reply.get(2).getMessage(), "Hello " + testName);


  }



}

