package com.kuba.chores.splitter.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.kuba.chords.splitter.AppConfig
import com.kuba.chords.splitter.api.routes.dto.UserDtos._
import com.kuba.chords.splitter.api.routes.dto.TaskDtos._
import com.kuba.chords.splitter.api.routes.dto.ChoreDtos._
import com.kuba.chords.splitter.api.routes.dto.JsonSupport
import com.kuba.chords.splitter.api.routes.Routes
import com.kuba.chords.splitter.service.{ChoresService, TasksService, UsersService}
import com.kuba.chores.splitter.util.DbSetUp
import com.kuba.dziworski.chords.splitter.slick.Tables
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}

class RoutesSpec extends WordSpec with Routes with Matchers with ScalatestRouteTest with JsonSupport with AppConfig with BeforeAndAfterEach with DbSetUp {
  val ApiPrefix = "/api/v1"
  val chores = Tables.Chores

  override val choresService = new ChoresService(db)
  override val usersService = new UsersService(db)
  override val tasksService = new TasksService(db)

  "POST /chores" should {
    "Add new Chore" in {
      Post(s"$ApiPrefix/chores", AddChoreDto("dust", 10, Some(3))) ~> routes ~> check {
        responseAs[String] shouldBe """{"choreId":1}"""
      }
    }
  }

  "GET /chores" should {
    "return list of chores" in {
      addChore("dust", 10, Some(5))
      Get(s"$ApiPrefix/chores") ~> routes ~> check {
        responseAs[GetChoresDto] shouldBe GetChoresDto(
          List(GetChoreDto(1, "dust", 10, Some(5)))
        )
      }
    }
  }


  "POST /chores/1/tasks" should {
    "add task" in {
      val choreId = addChore()
      val userId = addUser()
      val addTask = AddTaskDto(choreId, userId)
      Post(s"$ApiPrefix/chores/1/tasks", addTask) ~> routes ~> check {

      }
    }
  }

  def addChore(name: String = "dust", points: Int = 10, interval: Option[Int] = Some(5)): ChoreId = {
    Post(s"$ApiPrefix/chores", AddChoreDto(name, points, interval)) ~> routes ~> check {
      responseAs[ChoreId]
    }
  }

  def addUser(name: String = "mark", email: String = "mark@gmail.com"): UserId = {
    Post(s"$ApiPrefix/users", AddUserDto(name, email)) ~> routes ~> check {
      responseAs[UserId]
    }
  }

  override protected def beforeEach(): Unit = {
    initDb()
  }

  override protected def afterEach(): Unit = {
    cleanDb()
  }

  private def shouldBeOk = check {
    response.status shouldBe StatusCodes.OK
  }
}
