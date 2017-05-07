package com.kuba.dziworski.chores.splitter.service

import java.time.Clock

import com.kuba.dziworski.chores.splitter.api.routes.dto.PenaltyDtos.{AddPenaltyDto, GetPenaltiesDto, PenaltyId}
import com.kuba.dziworski.chores.splitter.api.routes.dto.RowConversions._
import com.kuba.dziworski.chores.splitter.Tables
import com.kuba.dziworski.chores.splitter.Tables.PenaltiesRow
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class PenaltyService(db: Database)(implicit clock: Clock = Clock.systemUTC()) {

  private val AutoInc = 0
  val penalties = Tables.Penalties
  val users = Tables.Users


  def getPenalties: Future[GetPenaltiesDto] = {
    val action = penalties.result
    db.run(action).map(seq => seq.map(_.toDto).toList).map(GetPenaltiesDto)
  }

  def addPenalty(addPenaltyDto: AddPenaltyDto): Future[PenaltyId] = {
    val row = PenaltiesRow(AutoInc, addPenaltyDto.userId, addPenaltyDto.points, addPenaltyDto.reason)
    val q = penalties returning penalties.map(_.id) += row
    db.run(q).map(PenaltyId)
  }

}
