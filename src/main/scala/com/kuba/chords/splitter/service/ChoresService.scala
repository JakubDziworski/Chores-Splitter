package com.kuba.chords.splitter.service

import java.sql.Timestamp
import java.time.Clock

import com.kuba.chords.splitter.api.routes.dto.ChoreDtos._
import com.kuba.dziworski.chords.splitter.slick.Tables
import com.kuba.chords.splitter.api.routes.dto.RowConversions._
import com.kuba.dziworski.chords.splitter.slick.Tables.ChoresRow
import com.kuba.chords.splitter.util.TimeUtil._
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class ChoresService(db: Database)(implicit clock: Clock = Clock.systemUTC()) {

  private val AutoInc = 0
  val chores = Tables.Chores
  val tasks = Tables.Tasks

  def addChore(addChoreDto: AddChoreDto): Future[ChoreId] = {
    val row = {
      chores.length.result.map(l => ChoresRow(l+1,l+1,now, addChoreDto.name,addChoreDto.points,addChoreDto.interval))
    }

    val action = for {
      r <-row
      insertedId <- chores returning chores.map(_.choreId) forceInsert r
    } yield insertedId

    db.run(action).map(id => ChoreId(id))
  }

  def editChore(choreId: ChoreId, dto: AddChoreDto): Future[ChoreId] = {
    def querySrcChoreId = {
      chores
        .filter(_.choreId === choreId.choreId)
        .map(ch => ch.srcChoreId)
        .result.head
    }

    def insert(srcChoreId:Long) = {
      val columns = chores.map(c => (c.srcChoreId ,c.name,c.points,c.createdAt, c.interval))
      val row = (srcChoreId,dto.name,dto.points,now,dto.interval)
      columns returning chores.map(_.choreId) += row
    }

    val action = for {
      srcChore <- querySrcChoreId
      newChoreId <- insert(srcChore)
    } yield newChoreId

    db.run(action).map(ChoreId)
  }

  def getChores: Future[GetChoresDto] = {
    val newestChoresIds = chores.groupBy(_.srcChoreId).map{case (srcId,chrs) =>
        chrs.map(_.choreId).max.getOrElse(0L)
    }
    val q = for {
      chId <- newestChoresIds
      ch <- chores if ch.choreId === chId
    } yield ch

    db.run(q.result).map(_.map(_.toDto).toList).map(GetChoresDto)
  }

  def getChoresAfterInterval(): Future[GetChoresDto] = {
    def milisSinceCompletion(tasks: Tables.Tasks): Rep[Long] = {
      valueToConstColumn(now) - tasks.completedAt.getOrElse(Long.MaxValue)
    }

    def toMilis(days: Rep[Int]): Rep[Int] = {
      days * (24 * 60 * 60 * 1000)
    }

    val q = for {
      ch <- chores
      t <- tasks if (t.choreId === ch.choreId && milisSinceCompletion(t) < ch.interval.getOrElse(0).asColumnOf[Long])
    } yield ch
    db.run(q.result).map(_.map(_.toDto).toList).map(GetChoresDto)
  }

  def getChore(id: ChoreId): Future[GetChoreDto] = {
    val action = chores.filter(_.choreId === id.choreId).result.head
    db.run(action).map(_.toDto)
  }
}
