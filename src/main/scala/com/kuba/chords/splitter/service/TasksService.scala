package com.kuba.chords.splitter.service

import com.kuba.chords.splitter.api.routes.dto.TaskDtos.{AddTaskDto, TaskId}
import com.kuba.dziworski.chords.splitter.slick.Tables
import com.kuba.dziworski.chords.splitter.slick.Tables.TasksRow
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class TasksService(db: Database) {
  private val AutoInc = 0
  val tasks = Tables.Tasks

  def addTask(dto:AddTaskDto) : Future[TaskId] = {
    val row = TasksRow(AutoInc,dto.userId.userId,dto.choreId.choreId,null,None)
    val action = tasks  returning tasks.map(_.id) += row
    db.run(action).map(TaskId)
  }
}
