import akka.Done
import akka.actor.ActorSystem
import otus.scala.grpc.helloworld3.{HelloRequest, HelloResponse}
import otus.scala.grpc.helloworld3.HelloWorldGrpc.{HelloWorld, HelloWorldStub}
import akka.stream.scaladsl.{Sink, Source}
import io.grpc.{ManagedChannel, ManagedChannelBuilder}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/***
   *   Реализуйте методы HelloWorldStubDecorator
   *   черз методы HelloWorldStub
   *   Проверить свою реализаю можно запустив Server
   * */
object DecoratorTask extends App {

  val channel: ManagedChannel = ManagedChannelBuilder
    .forAddress("localhost", 18080)
    .usePlaintext()
    .build()

  val stub: HelloWorldStub = new HelloWorldStub(channel)

  class HelloWorldStubDecorator(underlining: HelloWorld) {
    def hello(request: HelloRequest): scala.concurrent.Future[HelloResponse] = Future {
        HelloResponse(s"Read message -> ${request.msg}")
      }
    def helloStream(stream: Source[HelloRequest, akka.NotUsed]): Source[HelloResponse, akka.NotUsed] = stream.map {
        request => HelloResponse(s"Read message as stream ->  ${request.msg}")
      }
  }

  val helloWorldClient = new HelloWorldStubDecorator(stub)

  implicit val system: ActorSystem = ActorSystem("client")

  val s: Future[Done] = helloWorldClient.helloStream(Source.repeat(HelloRequest("Hello")))
    .map(resp => println(s"server response: $resp"))
    .runWith(Sink.ignore)

  Await.result(s, Duration.Inf)

}
