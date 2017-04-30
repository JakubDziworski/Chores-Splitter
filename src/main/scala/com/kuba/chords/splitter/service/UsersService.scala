package com.kuba.chords.splitter.service

import com.kuba.chords.splitter.api.routes.dto.UserDtos._
import com.kuba.dziworski.chords.splitter.slick.Tables
import com.kuba.dziworski.chords.splitter.slick.Tables.UsersRow
import slick.jdbc.H2Profile.api._
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import com.kuba.chords.splitter.api.routes.dto.RowConversions._

class UsersService(db: Database) {
  private val AutoInc = 0
  val users = Tables.Users

  def addUser(addUserDto: AddUserDto): Future[UserId] = {
    val row = UsersRow(AutoInc, addUserDto.name, addUserDto.email)
    val action = users returning users.map(_.id) += row
    db.run(action).map(UserId)
  }

  def getUsers: Future[GetUsersDto] = {
    val action = users.result
    db.run(action).map(_.map(_.toDto).toList).map(GetUsersDto)
  }
}
