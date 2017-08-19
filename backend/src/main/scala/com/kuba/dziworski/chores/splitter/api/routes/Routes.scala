package com.kuba.dziworski.chores.splitter.api.routes

import akka.http.scaladsl.model.StatusCodes
import com.kuba.dziworski.chores.splitter.AppConfig
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import com.kuba.dziworski.chores.splitter.service.Failures.TimeForTaskAlreadyEndedException

trait Routes extends ChoresRoutes with TaskRoutes with UsersRoutes with PenaltiesRoutes with StaticRoutes with AppConfig {
  private val apiWrapper = pathPrefix("api" / apiVersion)
  private val StaticContentDir = "../web-client/build/"

  val exceptionHandler = ExceptionHandler {
    case e@TimeForTaskAlreadyEndedException =>
      complete(StatusCodes.BadRequest, e.getMessage)
  }

  val logDuration = extractRequestContext.flatMap { ctx =>
    val start = System.currentTimeMillis()
    // handling rejections here so that we get proper status codes
    mapResponse { resp =>
      val d = System.currentTimeMillis() - start
      println(s"[${resp.status.intValue()}] ${ctx.request.method.name} ${ctx.request.uri} took: ${d}ms")
      resp
    }
  }

  val routes = (logDuration & handleExceptions(exceptionHandler)) {
    apiWrapper {
        choresRoutes ~ usersRoutes ~ tasksRoutes ~ penaltiesRoutes
    } ~ staticRoutes
  }
}