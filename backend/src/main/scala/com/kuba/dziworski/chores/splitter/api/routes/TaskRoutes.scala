package com.kuba.dziworski.chores.splitter.api.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import com.kuba.dziworski.chores.splitter.api.routes.dto.JsonSupport
import com.kuba.dziworski.chores.splitter.api.routes.dto.TaskDtos._
import com.kuba.dziworski.chores.splitter.api.routes.dto.UserDtos.UserId
import com.kuba.dziworski.chores.splitter.service.TasksService


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
    } ~ pathPrefix(LongNumber) { taskId =>
      (path("set-completed") & put) {
        val done = tasksService.setCompleted(TaskId(taskId),completed = true)
        complete(OK,done)
      } ~ (path("set-uncompleted") & put) {
        val done = tasksService.setCompleted(TaskId(taskId),completed = false)
        complete(OK,done)
      }
    } ~ path("user" / LongNumber) { userId =>
     get {
       val task = tasksService.getTasksForUser(UserId(userId))
       complete(OK,task)
     }
    }
  }
}