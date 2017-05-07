package com.kuba.dziworski.chores.splitter.service

import com.kuba.dziworski.chores.splitter.api.routes.dto.RowConversions._
import com.kuba.dziworski.chores.splitter.api.routes.dto.UserDtos._
import com.kuba.dziworski.chores.splitter.Tables
import com.kuba.dziworski.chores.splitter.Tables.UsersRow
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

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
    val completedTasksWithChores = tasks.join(chores).on((t,ch) => ch.choreId === t.choreId && t.completedAt.isDefined)
    val usersCompletedChoresPoints = for {
      (user,chore) <- users.joinLeft(completedTasksWithChores)
            .on((u,tch) => u.id === tch._1.userId)
            .map { case (user,taskWithChore) =>
              val chore = taskWithChore.map(_._2)
              (user,chore)
            }
    } yield (user,chore.map(_.points).getOrElse(0))

    val userPenalties = for {
    (user,penalty) <- users.joinLeft(penalties).on(_.id === _.userId)
    } yield (user,penalty.map(x => valueToConstColumn(0) - x.points).getOrElse(0))

    val union = usersCompletedChoresPoints unionAll userPenalties


    val z = union.groupBy(_._1).map { case (user, userPoints) =>
      val pointsSum = userPoints.map(_._2).sum.getOrElse(0)
      (user,pointsSum)
    }
    db.run(z.result).map(_.map { case (user, points) => user.toDto(points) }.toList).map(GetUsersDto)
  }
}
