package com.kuba.chords.splitter.api.routes

import com.kuba.chords.splitter.AppConfig
import akka.http.scaladsl.server.Directives._

trait Routes extends ChoresRoutes with TaskRoutes with UsersRoutes with PenaltiesRoutes with AppConfig {
  private val apiWrapper = pathPrefix("api" / apiVersion)

  val routes = apiWrapper {
    choresRoutes ~ usersRoutes ~ tasksRoutes ~ penaltiesRoutes
  }
}
