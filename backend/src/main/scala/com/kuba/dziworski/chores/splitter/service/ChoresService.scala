package com.kuba.dziworski.chores.splitter.service

import java.time.Clock

import com.kuba.dziworski.chores.splitter.api.routes.dto.ChoreDtos._
import com.kuba.dziworski.chores.splitter.api.routes.dto.RowConversions._
import com.kuba.dziworski.chores.splitter.util.TimeUtil._
import com.kuba.dziworski.chores.splitter.Tables
import com.kuba.dziworski.chores.splitter.Tables.{ChoresRow, TasksRow}
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class ChoresService(db: Database)(implicit clock: Clock) {

  val chores = Tables.Chores
  val tasks = Tables.Tasks

  def addChore(addChoreDto: AddChoreDto): Future[ChoreId] = {
    val row = {
      chores.length.result.map(l => ChoresRow(l + 1, l + 1, now, addChoreDto.name, addChoreDto.points, addChoreDto.interval))
    }

    val action = for {
      r <- row
      _ <- chores forceInsert r
    } yield r.choreId

    db.run(action).map(id => ChoreId(id))
  }

  def editChore(choreId: ChoreId, dto: AddChoreDto): Future[ChoreId] = {
    def querySrcChoreId = {
      chores
        .filter(_.choreId === choreId.choreId)
        .map(ch => ch.srcChoreId)
        .result.head
    }

    def insert(srcChoreId: Long,newId:Long) = {
      val row = ChoresRow(newId,srcChoreId, now,dto.name, dto.points, dto.interval)
      chores += row
    }

    val action = for {
      srcChore <- querySrcChoreId
      newId <- (chores.length.asColumnOf[Long]+1L).result
      _ <- insert(srcChore,newId)
    } yield newId

    db.run(action).map(ChoreId)
  }

  def getChores: Future[GetChoresDto] = {
    val newestChoresIds = chores.groupBy(_.srcChoreId).map { case (srcId, chrs) =>
      chrs.map(_.choreId).max.getOrElse(0L)
    }
    val q = for {
      chId <- newestChoresIds
      ch <- chores if ch.choreId === chId
    } yield ch
    db.run(q.sortBy(_.srcChoreId.desc).result).map(_.toDto)
  }

  def getChoresAfterInterval(): Future[GetChoresDto] = {

    val q = chores.filter(_.interval.isDefined).joinLeft(tasks).on((chore,t) => t.choreId === chore.choreId)

    def allTasksAreAfterInterval(interval:Int, tasksRows: Seq[TasksRow]) = {
      tasksRows.forall(_.completedAt.forall(hoursSince(_) >= interval*24))
    }

    db.run(q.result)
      .map(rows =>
        rows.groupBy(_._1.srcChoreId)
          .values
          .filter { tasksForChore =>
            val tasks = tasksForChore.flatMap(_._2)
            val chore = tasksForChore.map(_._1)
            val newestChore = chore.maxBy(_.choreId)
            allTasksAreAfterInterval(newestChore.interval.get, tasks)
          }
          .map(_.map(_._1).maxBy(_.choreId))
          .toList.sortBy(_.choreId))
      .map(_.toDto)
  }

  def getChore(id: ChoreId): Future[GetChoreDto] = {
    val action = chores.filter(_.choreId === id.choreId).result.head
    db.run(action).map(_.toDto)
  }
}
