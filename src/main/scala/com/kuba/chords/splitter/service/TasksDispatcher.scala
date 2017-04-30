package com.kuba.chords.splitter.service

import java.time.Clock

import akka.Done
import akka.actor.Actor
import com.kuba.chords.splitter.api.routes.dto.ChoreDtos.ChoreId
import com.kuba.chords.splitter.api.routes.dto.TaskDtos.AddTaskDto
import com.kuba.chords.splitter.api.routes.dto.{ChoreDtos, UserPoint}
import com.kuba.chords.splitter.service.TasksDispatcher.Check
import com.kuba.chords.splitter.util.TimeUtil._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class TasksDispatcher(tasksService: TasksService) {

  def dispatch() : Future[Done] = {
      //TODO transactionally!
      for {
        lastDispatch <- tasksService.getLastTaskDispatch()
        choresForToday <- tasksService.getChoresAfterInterval() if isOutdated(lastDispatch)
        points <- tasksService.getUsersPoints()
        newTasks <- tasksService.addTasks(TasksDispatcher.assignTasksForToday(points, choresForToday.chores))
        lastDispatchUpdate <- tasksService.updateLastTaskDispatch()
      } yield lastDispatchUpdate
  }
}


object TasksDispatcher {

  case object Check

  def assignTasksForToday(points: List[UserPoint], choresForToday: List[ChoreDtos.GetChoreDto]): scala.List[AddTaskDto] = {
    val descending = Ordering[Int].reverse
    val ascending = Ordering[Int]

    def loop(usrs: List[UserPoint], chrs: List[ChoreDtos.GetChoreDto], acc: List[AddTaskDto]): List[AddTaskDto] = {
      val leastPointsUsersFirst = usrs.sortBy(_.points)(ascending)
      val mostPointsChoresFirst = chrs.sortBy(_.points)(descending)

      val newTask = for {
        user <- leastPointsUsersFirst.headOption
        chore <- mostPointsChoresFirst.headOption
      } yield AddTaskDto(ChoreId(chore.id), user.userId)

      newTask match {
        case Some(t) =>
          val newUser = UserPoint(t.userId, leastPointsUsersFirst.head.points + mostPointsChoresFirst.head.points)
          val newUsers = newUser :: leastPointsUsersFirst.tail
          val newChores = mostPointsChoresFirst.tail
          loop(newUsers, newChores, t :: acc)
        case None =>
          acc
      }
    }

    loop(points, choresForToday, Nil)
  }

}
