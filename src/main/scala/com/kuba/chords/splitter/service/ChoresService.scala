package com.kuba.chords.splitter.service

import java.sql.Timestamp
import java.time.Clock

import com.kuba.chords.splitter.api.routes.dto.ChoreDtos._
import com.kuba.dziworski.chords.splitter.slick.Tables
import com.kuba.dziworski.chords.splitter.slick.Tables.ChoresRow
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class ChoresService(db: Database,clock: Clock = Clock.systemUTC()) {

  private val AutoInc = 0
  val chores = Tables.Chores

  def now(): Timestamp = {
    Timestamp.from(clock.instant())
  }
  private def convertToDto(choresRow: ChoresRow) = {
    GetChoreDto(choresRow.choreId,choresRow.name,choresRow.points,choresRow.interval)
  }

  def addChore(addChoreDto: AddChoreDto): Future[ChoreId] = {
    val columns = chores.map(c => (c.name,c.points,c.createdAt, c.interval))
    val row = (addChoreDto.name, addChoreDto.points,now(),addChoreDto.interval)
    val action = columns returning chores.map(_.choreId) += row
    db.run(action).map(id => ChoreId(id))
  }

  def editChore(choreId: ChoreId, dto: AddChoreDto): Future[ChoreId] = {
    def querySrcChoreId = {
      chores
        .filter(_.choreId === choreId.choreId)
        .map(ch => ch.srcChoreId.getOrElse(ch.choreId))
        .result.head
    }

    def insert(srcChoreId:Long) = {
      val columns = chores.map(c => (c.srcChoreId ,c.name,c.points,c.createdAt, c.interval))
      val row = (Some(srcChoreId),dto.name,dto.points,now(),dto.interval)
      columns returning chores.map(_.choreId) += row
    }

    val action = for {
      srcChore <- querySrcChoreId
      newChoreId <- insert(srcChore)
    } yield newChoreId

    db.run(action).map(ChoreId)
  }

  def getChores: Future[GetChoresDto] = {
    val action = chores.distinctOn(ch => ch.srcChoreId.getOrElse(ch.choreId)).result
    db.run(action).map(_.map(convertToDto).toList).map(GetChoresDto)
  }

  def getChore(id: ChoreId): Future[GetChoreDto] = {
    val action = chores.filter(_.choreId === id.choreId).result.head
    db.run(action).map(convertToDto)
  }
}
