package com.kuba.dziworski.chores.splitter.service

import java.sql.Timestamp
import java.time.{Clock, Instant}
import java.util.concurrent.TimeUnit

import akka.Done
import com.kuba.dziworski.chores.splitter.api.routes.dto.ChoreDtos.GetChoresDto
import com.kuba.dziworski.chores.splitter.api.routes.dto.TaskDtos.{AddTaskDto, GetTaskDto, GetTasksDto, TaskId}
import com.kuba.dziworski.chores.splitter.api.routes.dto.UserDtos.UserId
import com.kuba.dziworski.chores.splitter.Tables
import com.kuba.dziworski.chores.splitter.Tables.{ChoresRow, TasksRow, UsersRow}
import slick.jdbc.H2Profile.api._
import com.kuba.dziworski.chores.splitter.api.routes.dto.RowConversions._
import com.kuba.dziworski.chores.splitter.api.routes.dto.UserPoint

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future


class TasksService(db: Database)(implicit clock: Clock = Clock.systemUTC) {
  private val AutoInc = 0
  val tasks = Tables.Tasks
  val chores = Tables.Chores
  val users = Tables.Users
  val penalties = Tables.Penalties
  val tasksDispatches = Tables.TasksDispatches

  def now = Timestamp.from(Instant.now(clock)).getTime

  def addTasks(newTasks: List[AddTaskDto]): Future[List[TaskId]] = {
    val rows = newTasks.map(dto => TasksRow(AutoInc, dto.userId.userId, dto.choreId.choreId, now, None,None))
    val action = tasks returning tasks.map(_.id) ++= rows
    db.run(action).map(_.map(TaskId).toList)
  }

  def addTask(dto: AddTaskDto): Future[TaskId] = {
    val row = TasksRow(AutoInc, dto.userId.userId, dto.choreId.choreId, now, None,None)
    val action = tasks returning tasks.map(_.id) += row
    db.run(action).map(TaskId)
  }

  def getTasksForUser(userId: UserId): Future[GetTasksDto] = {
    val query = for {
      ch <- chores
      t <- tasks if t.choreId === ch.choreId
      u <- users.filter(_.id === userId.userId) if t.userId === u.id
    } yield (ch, t)
    db.run(query.result)
      .map(_.map { case (chRow, tRow) => tRow.toDto(chRow) }.toList)
      .map(GetTasksDto)
  }

  def getTasks(): Future[GetTasksDto] = {
    val query = for {
      ch <- chores
      t <- tasks if t.choreId === ch.choreId
      u <- users if t.userId === u.id
    } yield (ch, t)
    db.run(query.result)
      .map(_.map { case (chRow, tRow) => tRow.toDto(chRow) }.toList)
      .map(GetTasksDto)
  }

  def setCompleted(taskId: TaskId, completed: Boolean): Future[Done] = {
    val q = for {
      t <- tasks if t.id === taskId.taskId
    } yield t.completedAt
    val completion = if (completed) Some(now) else None
    db.run(q.update(completion)).map(_ => Done)
  }

}
