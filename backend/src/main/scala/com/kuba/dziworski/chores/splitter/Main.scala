package com.kuba.dziworski.chores.splitter

import akka.http.scaladsl.Http
import scala.concurrent.ExecutionContext.Implicits._
import scala.io.StdIn

object Main extends AppLoader {
  def main(args: Array[String]): Unit = {
    val host = appConfig.serverHost
    val port = appConfig.serverPort
    val bindingFuture = Http().bindAndHandle(routes.routes, host, port)
    println(s"Server online at http://$host:$port/\nPress RETURN to stop...")
    import scala.concurrent.duration._
    system.scheduler.schedule(5 seconds,30 minutes,new Runnable {
      override def run() = taskDispatcher.dispatch()
    })

    StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }
}
