package com.kuba.chords.splitter.service

import com.kuba.chords.splitter.api.routes.dto.ChoreDtos._
import com.kuba.dziworski.chords.splitter.slick.Tables
import com.kuba.dziworski.chords.splitter.slick.Tables.ChoresRow
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class ChoresService(db: Database) {
  private val AutoInc = 0
  val chores = Tables.Chores

  private def convertToDto(choresRow: ChoresRow) = {
    GetChoreDto(choresRow.choreId,choresRow.name,choresRow.points,choresRow.interval)
  }

  def addChore(addChoreDto: AddChoreDto): Future[ChoreId] = {
    val row = ChoresRow(AutoInc, AutoInc,addChoreDto.name, addChoreDto.points,addChoreDto.interval)
    val action = chores returning chores.map(_.choreId) += row
    db.run(action).map(id => ChoreId(id))
  }

  def getChores: Future[GetChoresDto] = {
    val action = chores.result
    db.run(action).map(_.map(convertToDto).toList).map(GetChoresDto)
  }

  def getChore(id: ChoreId): Future[GetChoreDto] = {
    val action = chores.filter(_.choreId === id.choreId).result.head
    db.run(action).map(convertToDto)
  }
}
