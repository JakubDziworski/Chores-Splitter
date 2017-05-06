package com.kuba.dziworski.chores.splitter.service

import java.time.Clock

import akka.Done
import com.kuba.dziworski.chores.splitter.api.routes.dto.ChoreDtos.ChoreId
import com.kuba.dziworski.chores.splitter.api.routes.dto.TaskDtos.AddTaskDto
import com.kuba.dziworski.chores.splitter.api.routes.dto.UserDtos.UserId
import com.kuba.dziworski.chores.splitter.api.routes.dto.{ChoreDtos, UserPoint}
import com.kuba.dziworski.chores.splitter.service.TasksDispatcherService.{DispatchNotRequired, DispatchResult, Dispatched}
import com.kuba.dziworski.chores.splitter.util.TimeUtil._
import com.kuba.dziworski.chores.splitter.Tables
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class TasksDispatcherService(db: Database,
                             usersService: UsersService,
                             choresService: ChoresService,
                             penaltyService: PenaltyService,
                             tasksService: TasksService
                            )(implicit clock: Clock = Clock.systemDefaultZone()) {
  val UncompletedTaskPenaltyFactor = 0.5f
  val tasksDispatches = Tables.TasksDispatches
  val users = Tables.Users
  val tasks = Tables.Tasks
  val chores = Tables.Chores
  val penalties = Tables.Penalties

  def dispatch(): Future[DispatchResult] = {
    //TODO transactionally!
    getLastTaskDispatch.flatMap { lastDispatch =>
      if(isBeforeToday(lastDispatch)) {
        for {
          _ <- addPenaltyForUncompletedTasks()
          choresForToday <- choresService.getChoresAfterInterval()
          points <- usersService.getUsers.map(_.users.map(u => UserPoint(UserId(u.id),u.points)))
          newTasks <- tasksService.addTasks(TasksDispatcherService.assignTasksForToday(points, choresForToday.chores))
          lastDispatchUpdate <- updateLastTaskDispatch()
        } yield Dispatched
      } else {
        Future(DispatchNotRequired)
      }
    }
  }

  def getLastTaskDispatch: Future[Long] = {
    val q = tasksDispatches.sortBy(_.id.desc).map(_.dispatchedAt)
    db.run(q.result.headOption).map(_.getOrElse(0))
  }

  def updateLastTaskDispatch(): Future[Done] = {
    val q = tasksDispatches.map(_.dispatchedAt) += now
    db.run(q).map(_ => Done)
  }

  def addPenaltyForUncompletedTasks() : Future[Done] = {
    val uncompletedTasks = for {
      ch <- chores
      t <- tasks if t.choreId === ch.choreId && t.completedAt.isEmpty && t.penaltyAddedAt.isEmpty
      u <- users if t.userId === u.id
    } yield (ch,t)

    def getUncompleted = {
      db.run(uncompletedTasks.result)
    }

    def insertPenalties(rows : Seq[(Tables.ChoresRow,Tables.TasksRow)]) = {
      val insertRows = rows.map { choresTasks =>
        val chore = choresTasks._1
        val task = choresTasks._2
        val penalty = (UncompletedTaskPenaltyFactor*chore.points).toInt
        Tables.PenaltiesRow(0, task.userId,penalty,s"Uncompleted ${chore.name}")
      }
      db.run(penalties ++= insertRows)
    }

    def markTransactionsPenaltied() = {
      val q = tasks
        .filter(_.completedAt.isEmpty)
        .filter(_.penaltyAddedAt.isEmpty)
        .map(_.penaltyAddedAt)
        .update(Some(now))
      db.run(q)
    }

    for {
       uncompletedTasks <- getUncompleted
       _ <- insertPenalties(uncompletedTasks)
       _ <- markTransactionsPenaltied()
    } yield Done
  }
}


object TasksDispatcherService {

  sealed trait DispatchResult
  case object DispatchNotRequired extends DispatchResult
  case object Dispatched extends DispatchResult

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
