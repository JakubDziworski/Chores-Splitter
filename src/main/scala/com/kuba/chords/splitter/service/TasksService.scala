package com.kuba.chords.splitter.service

import java.sql.Timestamp
import java.time.{Clock, Instant}

import com.kuba.chords.splitter.api.routes.dto.TaskDtos.{AddTaskDto, GetTaskDto, GetTasksDto, TaskId}
import com.kuba.chords.splitter.api.routes.dto.UserDtos.UserId
import com.kuba.dziworski.chords.splitter.slick.Tables
import com.kuba.dziworski.chords.splitter.slick.Tables.{ChoresRow, TasksRow, UsersRow}
import slick.jdbc.H2Profile.api._
import com.kuba.chords.splitter.api.routes.dto.RowConversions._
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future


class TasksService(db: Database,clock:Clock = Clock.systemUTC) {
  private val AutoInc = 0
  val tasks = Tables.Tasks
  val chores = Tables.Chores
  val users = Tables.Users

  def addTask(dto:AddTaskDto) : Future[TaskId] = {
    val now  = Timestamp.from(Instant.now(clock))
    val row = TasksRow(AutoInc,dto.userId.userId,dto.choreId.choreId,now,None)
    val action = tasks  returning tasks.map(_.id) += row
    db.run(action).map(TaskId)
  }

  def getTasksForUser(userId:UserId): Future[GetTasksDto] = {
    val query = for {
      ch <- chores
      t <- tasks if t.choreId === ch.choreId
      u <- users.filter(_.id === userId.userId) if t.userId === u.id
    } yield (ch,t,u)
    db.run(query.result)
      .map(_.map{case (chRow,tRow,uRow) => tRow.toDto(chRow,uRow)}.toList)
      .map(GetTasksDto)
  }

  def getTasks() : Future[GetTasksDto] = {
    val query = for {
      ch <- chores
      t <- tasks if t.choreId === ch.choreId
      u <- users if t.userId === u.id
    } yield (ch,t,u)
    db.run(query.result)
      .map(_.map{case (chRow,tRow,uRow) => tRow.toDto(chRow,uRow)}.toList)
      .map(GetTasksDto)
  }
}
