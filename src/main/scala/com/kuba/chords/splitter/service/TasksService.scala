package com.kuba.chords.splitter.service

import java.sql.Timestamp
import java.time.{Clock, Instant}

import com.kuba.chords.splitter.api.routes.dto.TaskDtos.{AddTaskDto, GetTaskDto, GetTasksDto, TaskId}
import com.kuba.chords.splitter.api.routes.dto.UserDtos.UserId
import com.kuba.dziworski.chords.splitter.slick.Tables
import com.kuba.dziworski.chords.splitter.slick.Tables.TasksRow
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future


class TasksService(db: Database,clock:Clock = Clock.systemUTC) {
  private val AutoInc = 0
  val tasks = Tables.Tasks

  def convert(tasksRow: TasksRow) = {
    GetTaskDto(
      tasksRow.id,
      tasksRow.choreId,
      tasksRow.userId,
      tasksRow.assignedAt.getTime,
      tasksRow.completedAt.isDefined
    )
  }

  def addTask(dto:AddTaskDto) : Future[TaskId] = {
    val now  = Timestamp.from(Instant.now(clock))
    val row = TasksRow(AutoInc,dto.userId.userId,dto.choreId.choreId,now,None)
    val action = tasks  returning tasks.map(_.id) += row
    db.run(action).map(TaskId)
  }

  def getTasksForUser(userId:UserId): Future[GetTasksDto] = {
    val action = tasks.filter(_.userId === userId.userId).result
    db.run(action).map(_.map(convert).toList).map(GetTasksDto)
  }
}
