package com.kuba.chords.splitter.service

import java.time.Clock

import akka.Done
import com.kuba.chords.splitter.api.routes.dto.ChoreDtos.ChoreId
import com.kuba.chords.splitter.api.routes.dto.TaskDtos.AddTaskDto
import com.kuba.chords.splitter.api.routes.dto.{ChoreDtos, UserPoint}
import com.kuba.chords.splitter.util.TimeUtil._
import com.kuba.dziworski.chords.splitter.slick.Tables
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class TasksDispatcherService(db: Database,
                             usersService: UsersService,
                             choresService: ChoresService,
                             tasksService: TasksService
                            )(implicit clock: Clock = Clock.systemUTC) {

  val tasksDispatches = Tables.TasksDispatches
  val users = Tables.Users

  def dispatch(): Future[Done] = {
    //TODO transactionally!
    for {
      lastDispatch <- getLastTaskDispatch
      choresForToday <- choresService.getChoresAfterInterval() if isOutdated(lastDispatch)
      points <- usersService.getUsersPoints()
      newTasks <- tasksService.addTasks(TasksDispatcherService.assignTasksForToday(points, choresForToday.chores))
      lastDispatchUpdate <- updateLastTaskDispatch()
    } yield lastDispatchUpdate
  }

  def getLastTaskDispatch: Future[Long] = {
    val q = tasksDispatches.sortBy(_.id.desc).map(_.dispatchedAt)
    db.run(q.result.headOption).map(_.getOrElse(0))
  }

  def updateLastTaskDispatch(): Future[Done] = {
    val q = tasksDispatches.map(_.dispatchedAt) += now
    db.run(q).map(_ => Done)
  }
}


object TasksDispatcherService {

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
