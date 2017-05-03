package com.kuba.chords.splitter.service

import com.kuba.chords.splitter.api.routes.dto.UserDtos._
import com.kuba.dziworski.chords.splitter.slick.Tables
import com.kuba.dziworski.chords.splitter.slick.Tables.UsersRow
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import com.kuba.chords.splitter.api.routes.dto.RowConversions._
import com.kuba.chords.splitter.api.routes.dto.UserPoint

class UsersService(db: Database) {
  private val AutoInc = 0
  val users = Tables.Users
  val tasks = Tables.Tasks
  val chores = Tables.Chores
  val penalties = Tables.Penalties

  def addUser(addUserDto: AddUserDto): Future[UserId] = {
    val row = UsersRow(AutoInc, addUserDto.name, addUserDto.email)
    val action = users returning users.map(_.id) += row
    db.run(action).map(UserId)
  }

  def getUsers: Future[GetUsersDto] = {
    val action = users.result
    db.run(action).map(_.map(_.toDto).toList).map(GetUsersDto)
  }

  def getUsersPoints: Future[List[UserPoint]] = {
    val q = (for {
      t <- tasks
      u <- users if t.userId === u.id && t.completedAt.isDefined
      c <- chores if t.choreId === c.choreId
      p <- penalties if p.userId === u.id
    } yield (t, c, p)).groupBy(_._1.id).map { case (userId, tcp) =>
      val sumOfAllTransactonsForUser = tcp.map(_._2.points).sum.getOrElse(0)
      val sumOfPenaltiesForUser = tcp.map(_._3.points).sum.getOrElse(0)
      val totalPointsForUser = sumOfAllTransactonsForUser - sumOfPenaltiesForUser
      (userId, totalPointsForUser)
    }
    db.run(q.result).map(_.map { case (userId, points) => UserPoint(UserId(userId), points) }.toList)
  }
}
