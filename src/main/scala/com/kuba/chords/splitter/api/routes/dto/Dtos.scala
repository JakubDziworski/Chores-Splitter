package com.kuba.chords.splitter.api.routes.dto

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.kuba.chords.splitter.api.routes.dto.TaskDtos._
import com.kuba.chords.splitter.api.routes.dto.ChoreDtos._
import com.kuba.chords.splitter.api.routes.dto.UserDtos._
import spray.json.DefaultJsonProtocol

trait OutputDto
trait InputDto
trait JsonSupport extends ChoreFormat with TaskFormat with UserFormat

object ChoreDtos {
  case class AddChoreDto(name: String, points: Int, interval: Option[Int])
  case class EditChoreDto(choreId: ChoreId,name: String, points: Int, interval: Option[Int])
  case class ChoreId(choreId: Long) extends InputDto
  case class GetChoreDto(id: Long, name: String, points: Int, interval: Option[Int]) extends OutputDto
  case class GetChoresDto(chores: List[GetChoreDto]) extends OutputDto

  trait ChoreFormat extends SprayJsonSupport with DefaultJsonProtocol {
    implicit lazy val choreInputDtoFormat = jsonFormat3(AddChoreDto)
    implicit lazy val choreIdFormat = jsonFormat1(ChoreId)
    implicit lazy val addChoreDtoFormat = jsonFormat4(GetChoreDto)
    implicit lazy val editChoreDtoFormat = jsonFormat4(EditChoreDto)
    implicit lazy val choresDtoFormat = jsonFormat1(GetChoresDto)
  }
}

object UserDtos {

  case class UserId(userId: Long) extends InputDto

  case class AddUserDto(name: String, email: String) extends InputDto

  case class GetUserDto(id: Long, name: String, email: String) extends OutputDto

  case class GetUsersDto(users: List[GetUserDto]) extends OutputDto

  trait UserFormat extends SprayJsonSupport with DefaultJsonProtocol {
    implicit lazy val userIdFormat = jsonFormat1(UserId)
    implicit lazy val addUserDtoFormat = jsonFormat2(AddUserDto)
    implicit lazy val getUserDtoFormat = jsonFormat3(GetUserDto)
    implicit lazy val getUsersDtoFormat = jsonFormat1(GetUsersDto)
  }
}

object TaskDtos {
  case class TaskId(taskId:Long) extends InputDto
  case class AddTaskDto(choreId: ChoreId, userId: UserId)
  case class GetTaskDto(id: Long, choreId: Long, userId: Long, assignedAt: Long, completed: Boolean)
  case class GetTasksDto(tasks:List[GetTaskDto])

  trait TaskFormat extends SprayJsonSupport with DefaultJsonProtocol with ChoreFormat with UserFormat {
    implicit lazy val addTaskDtoFormat = jsonFormat2(AddTaskDto)
    implicit lazy val taskIdFormat = jsonFormat1(TaskId)
    implicit lazy val getTaskDtoFormat = jsonFormat5(GetTaskDto)
    implicit lazy val getTasksDtoFormat = jsonFormat1(GetTasksDto)
  }
}
