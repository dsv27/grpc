import io.grpc.netty.NettyServerBuilder
import io.grpc.stub.StreamObserver
import otus.scala.grpc.helloworld3.{HelloRequest, HelloResponse}
import otus.scala.grpc.helloworld3.HelloWorldGrpc.HelloWorld

import scala.concurrent.{ExecutionContext, Future}

object Server extends App {
  val service = new HelloWorld {
    override def hello(request: HelloRequest): Future[HelloResponse] =
      Future.successful(HelloResponse("345"))

    override def helloStream(responseObserver: StreamObserver[HelloResponse]): StreamObserver[HelloRequest] = {
      new StreamObserver[HelloRequest] {
        override def onNext(value: HelloRequest): Unit =
          responseObserver.onNext(HelloResponse("response for" + value.msg))

        override def onError(t: Throwable): Unit = println(t)

        override def onCompleted(): Unit = ()
      }
    }
  }

  NettyServerBuilder.forPort(18080)
    .addService(HelloWorld.bindService(service, ExecutionContext.Implicits.global))
    .build()
    .start()
    .awaitTermination()
}