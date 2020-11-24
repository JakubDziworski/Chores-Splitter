package com.kuba.dziworski.chores.splitter.service

import java.sql.Timestamp
import java.time.{Clock, Instant}

import akka.Done
import com.kuba.dziworski.chores.splitter.Tables
import com.kuba.dziworski.chores.splitter.Tables.TasksRow
import com.kuba.dziworski.chores.splitter.api.routes.dto.RowConversions._
import com.kuba.dziworski.chores.splitter.api.routes.dto.TaskDtos.{AddTaskDto, GetTasksDto, TaskId}
import com.kuba.dziworski.chores.splitter.api.routes.dto.UserDtos.UserId
import slick.jdbc.H2Profile.api._
import com.kuba.dziworski.chores.splitter.service.Failures.TimeForTaskAlreadyEndedException
import com.kuba.dziworski.chores.splitter.util.TimeUtil

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future


class TasksService(db: Database)(implicit clock: Clock) {
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
    val q = for {
      ch <- chores
      t <- tasks if t.choreId === ch.choreId
      u <- users.filter(_.id === userId.userId) if t.userId === u.id
    } yield (ch, t)

    val sorted = q.sortBy(_._2.id.desc)
    db.run(sorted.result)
      .map(_.map { case (chRow, tRow) => tRow.toDto(chRow) }.toList)
      .map(GetTasksDto)
  }

  def getTasks(): Future[GetTasksDto] = {
    val q = for {
      ch <- chores
      t <- tasks if t.choreId === ch.choreId
      u <- users if t.userId === u.id
    } yield (ch, t)
    val sorted = q.sortBy(_._2.id.desc)
    db.run(sorted.result)
      .map(_.map { case (chRow, tRow) => tRow.toDto(chRow) }.toList)
      .map(GetTasksDto)
  }

  def setCompleted(taskId: TaskId, completed: Boolean): Future[Done] = {
    val q = for {
      t <- tasks if t.id === taskId.taskId
    } yield t

    db.run(q.result.head).flatMap { t =>
      if(TimeUtil.hoursSince(t.assignedAt) >= 24) {
        Future.failed(TimeForTaskAlreadyEndedException)
      } else {
        val completion = if (completed) Some(now) else None
        val update = q.map(_.completedAt).update(completion)
        db.run(update).map(_ => Done)
      }
    }
  }

}
