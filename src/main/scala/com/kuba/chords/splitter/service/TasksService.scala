package com.kuba.chords.splitter.service

import java.sql.Timestamp
import java.time.{Clock, Instant}
import java.util.concurrent.TimeUnit

import akka.Done
import com.kuba.chords.splitter.api.routes.dto.ChoreDtos.GetChoresDto
import com.kuba.chords.splitter.api.routes.dto.TaskDtos.{AddTaskDto, GetTaskDto, GetTasksDto, TaskId}
import com.kuba.chords.splitter.api.routes.dto.UserDtos.UserId
import com.kuba.dziworski.chords.splitter.slick.Tables
import com.kuba.dziworski.chords.splitter.slick.Tables.{ChoresRow, TasksRow, UsersRow}
import slick.jdbc.H2Profile.api._
import com.kuba.chords.splitter.api.routes.dto.RowConversions._
import com.kuba.chords.splitter.api.routes.dto.UserPoint

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future


class TasksService(db: Database,clock:Clock = Clock.systemUTC) {
  private val AutoInc = 0
  val tasks = Tables.Tasks
  val chores = Tables.Chores
  val users = Tables.Users
  val tasksDispatches = Tables.TasksDispatches

  def now  = Timestamp.from(Instant.now(clock)).getTime

  def addTasks(newTasks:List[AddTaskDto]) : Future[List[TaskId]] = {
    val rows = newTasks.map(dto => TasksRow(AutoInc,dto.userId.userId,dto.choreId.choreId,now,None))
    val action = tasks returning tasks.map(_.id) ++= rows
    db.run(action).map(_.map(TaskId).toList)
  }

  def addTask(dto:AddTaskDto) : Future[TaskId] = {
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

  def getChoresAfterInterval() : Future[GetChoresDto] = {
    def milisSinceCompletion(tasks:Tables.Tasks) : Rep[Long] = {
      valueToConstColumn(now) - tasks.completedAt.getOrElse(Long.MaxValue)
    }

    def toMilis(days:Rep[Int]): Rep[Int] = {
      days * (24 * 60 * 60 * 1000)
    }

    val q = for {
      ch <- chores
      t <- tasks if (t.choreId === ch.choreId && milisSinceCompletion(t) < ch.interval.getOrElse(0).asColumnOf[Long])
    } yield ch
    db.run(q.result).map(_.map(_.toDto).toList).map(GetChoresDto)
  }

  def getUsersPoints() : Future[List[UserPoint]] = {
    val q = (for {
      t <- tasks
      u <- users if t.userId === u.id && t.completedAt.isDefined
      c <- chores if t.choreId === c.choreId
    } yield (t,c)).groupBy(_._1.id).map{ case (userId,tc) =>
      val sumOfAllTransactonsForUser = tc.map(_._2.points).sum.getOrElse(0)
      (userId,sumOfAllTransactonsForUser)
    }
    db.run(q.result).map(_.map{case (userId,points) => UserPoint(UserId(userId),points)}.toList)
  }

  def getLastTaskDispatch() : Future[Long] = {
    val q = tasksDispatches.sortBy(_.id.desc).map(_.dispatchedAt)
    db.run(q.result.headOption).map(_.getOrElse(0))
  }

  def updateLastTaskDispatch() : Future[Done] = {
    val q = tasksDispatches.map(_.dispatchedAt) += now
    db.run(q).map(_ => Done)
  }


}
