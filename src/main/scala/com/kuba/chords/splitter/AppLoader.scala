package com.kuba.chords.splitter

import com.kuba.chords.splitter.api.routes.Routes
import com.kuba.chords.splitter.service.{ChoresService, TasksService, UsersService}

object AppLoader {
  def main(args: Array[String]): Unit = {
    val config = new AppConfig {}
    val routes = new Routes {
      override val choresService: ChoresService = new ChoresService(null)
      override val usersService: UsersService = new UsersService(null)
      override val tasksService: TasksService = new TasksService(null)
    }
  }
}
