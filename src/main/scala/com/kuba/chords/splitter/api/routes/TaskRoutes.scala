package com.kuba.chords.splitter.api.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import com.kuba.chords.splitter.api.routes.dto.JsonSupport
import com.kuba.chords.splitter.api.routes.dto.TaskDtos._
import com.kuba.chords.splitter.api.routes.dto.UserDtos.UserId
import com.kuba.chords.splitter.service.TasksService


trait TaskRoutes extends JsonSupport {
  val tasksService: TasksService

  val tasksRoutes = pathPrefix("tasks") {
    pathEndOrSingleSlash {
      get {
        complete(OK,tasksService.getTasks())
      } ~ (pathEndOrSingleSlash  & post & entity(as[AddTaskDto])) { taskDto =>
        val taskId = tasksService.addTask(taskDto)
        complete(Created, taskId)
      }
    } ~ path("user" / LongNumber) { userId =>
     get {
       val task = tasksService.getTasksForUser(UserId(userId))
       complete(OK,task)
     }
    }
  }
}