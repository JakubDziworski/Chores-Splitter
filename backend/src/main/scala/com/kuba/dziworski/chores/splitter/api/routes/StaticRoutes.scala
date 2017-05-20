package com.kuba.dziworski.chores.splitter.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import com.kuba.dziworski.chores.splitter.AppConfig
import com.kuba.dziworski.chores.splitter.service.Failures.TimeForTaskAlreadyEndedException

trait StaticRoutes {
  private val StaticContentDir = "../web-client/build/"

  val staticRoutes = (path("") | path("dashboard") | path("index.html")) {
    getFromFile(s"${StaticContentDir}index.html")
  } ~
    path(Remaining) { path =>
      getFromFile(s"$StaticContentDir$path")
    }
}