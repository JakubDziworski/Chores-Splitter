package com.kuba.dziworski.chores.splitter.api.routes.dto

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.kuba.dziworski.chores.splitter.api.routes.dto.TaskDtos._
import com.kuba.dziworski.chores.splitter.api.routes.dto.ChoreDtos._
import com.kuba.dziworski.chores.splitter.api.routes.dto.PenaltyDtos.{GetPenaltyDto, PenaltyFormat}
import com.kuba.dziworski.chores.splitter.api.routes.dto.UserDtos._
import com.kuba.dziworski.chores.splitter.Tables.{ChoresRow, PenaltiesRow, TasksRow, UsersRow}
import spray.json.DefaultJsonProtocol

trait JsonSupport extends ChoreFormat with TaskFormat with UserFormat with PenaltyFormat

object ChoreDtos {
  case class AddChoreDto(name: String, points: Int, interval: Option[Int])
  case class EditChoreDto(choreId: String,name: String, points: Int, interval: Option[Int])
  case class ChoreId(choreId: Long)
  case class GetChoreDto(id: Long, name: String, points: Int, interval: Option[Int])
  case class GetChoresDto(chores: List[GetChoreDto])

  trait ChoreFormat extends SprayJsonSupport with DefaultJsonProtocol {
    implicit lazy val choreInputDtoFormat = jsonFormat3(AddChoreDto)
    implicit lazy val choreIdFormat = jsonFormat1(ChoreId)
    implicit lazy val addChoreDtoFormat = jsonFormat4(GetChoreDto)
    implicit lazy val editChoreDtoFormat = jsonFormat4(EditChoreDto)
    implicit lazy val choresDtoFormat = jsonFormat1(GetChoresDto)
  }
}

object UserDtos {

  case class UserId(userId: Long)

  case class AddUserDto(name: String, email: String)

  case class GetUserDto(id: Long, name: String, email: String,points:Int)

  case class GetUsersDto(users: List[GetUserDto])

  trait UserFormat extends SprayJsonSupport with DefaultJsonProtocol {
    implicit lazy val userIdFormat = jsonFormat1(UserId)
    implicit lazy val addUserDtoFormat = jsonFormat2(AddUserDto)
    implicit lazy val getUserDtoFormat = jsonFormat4(GetUserDto)
    implicit lazy val getUsersDtoFormat = jsonFormat1(GetUsersDto)
  }
}

object TaskDtos {
  case class TaskId(taskId:Long)
  case class AddTaskDto(choreId: ChoreId, userId: UserId)
  case class GetTaskDto(id: Long, chore:GetChoreDto, userId:Long, assignedAt: Long, completed: Boolean)
  case class GetTasksDto(tasks:List[GetTaskDto])

  trait TaskFormat extends SprayJsonSupport with DefaultJsonProtocol with ChoreFormat with UserFormat {
    implicit lazy val addTaskDtoFormat = jsonFormat2(AddTaskDto)
    implicit lazy val taskIdFormat = jsonFormat1(TaskId)
    implicit lazy val getTaskDtoFormat = jsonFormat5(GetTaskDto)
    implicit lazy val getTasksDtoFormat = jsonFormat1(GetTasksDto)
  }
}

object PenaltyDtos {
  case class PenaltyId(penaltyId:Long)
  case class AddPenaltyDto(userId: Long,points:Int,reason:String)
  case class GetPenaltyDto(id:Long,userId:Long,points:Int,reason:String)
  case class GetPenaltiesDto(penalties:List[GetPenaltyDto])

  trait PenaltyFormat extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val penaltyIdFormat = jsonFormat1(PenaltyId)
    implicit val addPenaltyFormat = jsonFormat3(AddPenaltyDto)
    implicit val getPenaltyFormat = jsonFormat4(GetPenaltyDto)
    implicit val getPenaltiesFormat = jsonFormat1(GetPenaltiesDto)
  }
}

object RowConversions {
  implicit class UserConverter(userRow: UsersRow) {
    def toDto(points:Int) : GetUserDto = {
      GetUserDto(userRow.id,userRow.name,userRow.email,points)
    }
  }

  implicit class ChoreConverter(choreRow: ChoresRow) {
    def toDto : GetChoreDto = {
      GetChoreDto(choreRow.choreId,choreRow.name,choreRow.points,choreRow.interval)
    }
  }

  implicit class ChoresConverter(choresRows: Iterable[ChoresRow]) {
    def toDto  = GetChoresDto(choresRows.map(_.toDto).toList)
  }

  implicit class TaskConverter(taskRow: TasksRow) {
    def toDto(choreRow: ChoresRow): GetTaskDto = {
      GetTaskDto(
        taskRow.id,
        choreRow.toDto,
        taskRow.userId,
        taskRow.assignedAt,
        taskRow.completedAt.isDefined
      )
    }
  }

  implicit class PenaltyConverter(penaltyRow: PenaltiesRow) {
    def toDto: GetPenaltyDto = {
      GetPenaltyDto(
        penaltyRow.id,
        penaltyRow.userId,
        penaltyRow.points,
        penaltyRow.reason
      )
    }
  }
}

case class UserPoint(userId:UserId,points:Int)


