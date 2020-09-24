package grpc.grpcdemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import grpc.grpcdemo.client.DemoClient;
import io.grpc.examples.hellowworld.HelloReply;
import io.grpc.examples.hellowworld.HelloRequest;
import io.grpc.examples.hellowworld.GreeterGrpc.GreeterImplBase;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcServerRule;


public class GrpcClientSideTest {

  @Rule
  public GrpcServerRule grpcServerRule = new GrpcServerRule().directExecutor();

  public String host = "localhost";
  public int port = 8980;

  @Test
  public void test() {
    GreeterImplBase service = Mockito.spy(new GreeterImplBase() {
    });
    grpcServerRule.getServiceRegistry().addService(service);

    ArgumentCaptor<HelloRequest> requestCaptor = ArgumentCaptor.forClass(HelloRequest.class);
    String testName = "test name";
    DemoClient client = new DemoClient(host, port);
    client.unary(testName);

    Mockito.verify(service).sayHelloUnary(requestCaptor.capture(),
        (StreamObserver<HelloReply>) Matchers.any(HelloReply.class));

    assertEquals(testName, requestCaptor.getValue().getName());
  }



}
