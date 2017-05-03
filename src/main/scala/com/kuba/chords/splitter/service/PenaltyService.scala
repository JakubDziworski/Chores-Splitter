package com.kuba.chords.splitter.service

import java.time.Clock

import com.kuba.chords.splitter.api.routes.dto.PenaltyDtos.{AddPenaltyDto, GetPenaltiesDto, PenaltyId}
import com.kuba.dziworski.chords.splitter.slick.Tables
import com.kuba.dziworski.chords.splitter.slick.Tables.PenaltiesRow
import com.kuba.chords.splitter.api.routes.dto.RowConversions._
import scala.concurrent.ExecutionContext.Implicits._
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

class PenaltyService(db: Database)(implicit clock: Clock = Clock.systemUTC()) {

  private val AutoInc = 0
  val penalties = Tables.Penalties


  def getPenalties: Future[GetPenaltiesDto] = {
    val action = penalties.result
    db.run(action).map(seq => seq.map(_.toDto).toList).map(GetPenaltiesDto)
  }
  def addPenalty(addPenaltyDto: AddPenaltyDto): Future[PenaltyId] = {
    val row = PenaltiesRow(AutoInc,addPenaltyDto.userId,addPenaltyDto.points,addPenaltyDto.reason)
    val q = penalties returning penalties.map(_.id) += row
    db.run(q).map(PenaltyId)
  }

}
