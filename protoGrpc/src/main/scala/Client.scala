import io.grpc.{ManagedChannel, ManagedChannelBuilder}
import io.grpc.stub.{ClientResponseObserver, StreamObserver}
import otus.scala.grpc.helloworld3.{HelloRequest, HelloResponse}
import otus.scala.grpc.helloworld3.HelloWorldGrpc.{HelloWorld, HelloWorldStub}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Client extends App {

  val channel: ManagedChannel = ManagedChannelBuilder
    .forAddress("localhost", 18080)
    .usePlaintext()
    .build()

  val stub = new HelloWorldStub(channel)
  val req = stub.hello(HelloRequest(msg = "123"))
  val res = Await.result(req, Duration.Inf)
  println(res)

  val reqObserver = stub.helloStream(new StreamObserver[HelloResponse] {
    override def onNext(value: HelloResponse): Unit = println("Received" + value.msg)

    override def onError(t: Throwable): Unit = println(t)

    override def onCompleted(): Unit = ()
  })

  reqObserver.onNext(HelloRequest("1"))
  reqObserver.onNext(HelloRequest("2"))
  reqObserver.onNext(HelloRequest("3"))
  reqObserver.onNext(HelloRequest("4"))

  Thread.sleep(2000)

}