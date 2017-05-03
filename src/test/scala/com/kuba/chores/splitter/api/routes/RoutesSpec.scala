package com.kuba.chores.splitter.api.routes

import java.time.{Clock, Instant}

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.kuba.chords.splitter.AppConfig
import com.kuba.chords.splitter.api.routes.Routes
import com.kuba.chords.splitter.api.routes.dto.ChoreDtos._
import com.kuba.chords.splitter.api.routes.dto.JsonSupport
import com.kuba.chords.splitter.api.routes.dto.PenaltyDtos.{AddPenaltyDto, GetPenaltiesDto, GetPenaltyDto, PenaltyId}
import com.kuba.chords.splitter.api.routes.dto.TaskDtos._
import com.kuba.chords.splitter.api.routes.dto.UserDtos._
import com.kuba.chords.splitter.service._
import com.kuba.chores.splitter.util.{DbSetUp, TestHelpers}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}

class RoutesSpec extends WordSpec with Routes with Matchers with ScalatestRouteTest with JsonSupport with AppConfig with BeforeAndAfterEach with DbSetUp with MockFactory with TestHelpers {
  val ApiPrefix = "/api/v1"

  override val choresService = new ChoresService(db)(clockMock)
  override val usersService = new UsersService(db)
  override val tasksService = new TasksService(db)(clockMock)
  override val penaltiesService = new PenaltyService(db)(clockMock)

  "POST /chores" should {
    "Add new Chore" in {
      setTime(100)
      Post(s"$ApiPrefix/chores", AddChoreDto("dust off", 10, Some(3))) ~> routes ~> check {
        responseAs[String] shouldBe """{"choreId":1}"""
        status shouldBe StatusCodes.Created
      }
    }
  }

  "GET /chores" should {
    "return list of chores" in {
      setTime(100)
      addChore("dust off", 10, Some(5))
      Get(s"$ApiPrefix/chores") ~> routes ~> check {
        responseAs[GetChoresDto] shouldBe GetChoresDto(
          List(GetChoreDto(1, "dust off", 10, Some(5)))
        )
      }
    }
  }

  "POST /tasks" should {
    "return Created and task id" in {
      setTime(100)
      val choreId = addChore()
      val userId = addUser()
      val addTask = AddTaskDto(choreId, userId)
      Post(s"$ApiPrefix/tasks", addTask) ~> routes ~> check {
        responseAs[TaskId] shouldBe TaskId(1)
        status shouldBe StatusCodes.Created
      }
    }
  }

  "PUT /chores/1" should {
    "edit chore returning Created with new chore id" in {
      setTime(100)
      val dustId = addChore("dust off",5,Some(3))
      val dto = AddChoreDto("dust off harder",7,Some(2))
      Put(s"$ApiPrefix/chores/1",dto) ~> routes ~> check {
        responseAs[ChoreId] shouldBe ChoreId(2)
      }
    }
    "return only newest edited version of chore" in {
      setTime(100)
      val dustId = addChore("dust off",5,Some(3))
      val dto = AddChoreDto("dust off harder",7,None)
      Put(s"$ApiPrefix/chores/1",dto) ~> routes ~> check {
        responseAs[ChoreId] shouldBe ChoreId(2)
      }
      getChores shouldBe GetChoresDto(List(
        GetChoreDto(
          2,"dust off harder",7,None
        )
      ))
    }
  }

  "GET /tasks" should {
    "return all tasks" in {
      setTime(100)
      val mark = addUser("mark","mark@gmail.com")
      val andrew = addUser("andrew","andrew@gmail.com")
      val sweep = addChore("sweep",5,Some(3))
      val markSweepTask = addTask(mark,sweep)
      val andrewSweepTask = addTask(andrew,sweep)
      Get(s"$ApiPrefix/tasks") ~> routes ~> check {
        responseAs[GetTasksDto] shouldBe GetTasksDto(List(
          GetTaskDto(
            markSweepTask.taskId,
            GetChoreDto(sweep.choreId, "sweep", 5, Some(3)),
            GetUserDto(mark.userId, "mark", "mark@gmail.com"),
            100,
            completed = false),
          GetTaskDto(
            andrewSweepTask.taskId,
            GetChoreDto(sweep.choreId, "sweep", 5, Some(3)),
            GetUserDto(andrew.userId, "andrew", "andrew@gmail.com"),
            100,
            completed = false)
        ))
      }
    }
  }

  "PUT /tasks/1/set-completed" should {
    "mark task 1 as completed" in {
      setTime(200)
      val andrew = addUser("andrew","andrew@gmail.com")
      val sweep = addChore("sweep",5,Some(3))
      val andrewSweepTask = addTask(andrew,sweep)
      Put(s"$ApiPrefix/tasks/1/set-completed") ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
      getTasks() shouldBe GetTasksDto(List(
        GetTaskDto(
          1,
          GetChoreDto(sweep.choreId, "sweep", 5, Some(3)),
          GetUserDto(1, "andrew", "andrew@gmail.com"),
          200,
          completed = true)
      ))
    }
  }

  "PUT /tasks/1/set-uncompleted" should {
    "mark task 1 as uncompleted" in {
      setTime(200)
      val andrew = addUser("andrew","andrew@gmail.com")
      val sweep = addChore("sweep",5,Some(3))
      val andrewSweepTask = addTask(andrew,sweep)
      Put(s"$ApiPrefix/tasks/1/set-completed") ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
      getTasks() shouldBe GetTasksDto(List(
        GetTaskDto(
          1,
          GetChoreDto(sweep.choreId, "sweep", 5, Some(3)),
          GetUserDto(1, "andrew", "andrew@gmail.com"),
          200,
          completed = true)
      ))
      Put(s"$ApiPrefix/tasks/1/set-uncompleted") ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
      getTasks() shouldBe GetTasksDto(List(
        GetTaskDto(
          1,
          GetChoreDto(sweep.choreId, "sweep", 5, Some(3)),
          GetUserDto(1, "andrew", "andrew@gmail.com"),
          200,
          completed = false)
      ))
    }
  }

  "GET /tasks/user/1" should {
    "return users' tasks" in {
      setTime(100)
      val andrew = addUser("andrew","andrew@gmail.com")
      val john = addUser("john","john@gmail.com")
      val sweep = addChore("sweep",5,Some(3))
      val andrewSweepTask = addTask(andrew,sweep)
      val johnSweepTask = addTask(john,sweep)
      Get(s"$ApiPrefix/tasks/user/${andrewSweepTask.taskId}") ~> routes ~> check {
        responseAs[GetTasksDto] shouldBe GetTasksDto(List(
          GetTaskDto(
            andrewSweepTask.taskId,
            GetChoreDto(sweep.choreId, "sweep", 5, Some(3)),
            GetUserDto(andrew.userId, "andrew", "andrew@gmail.com"),
            100,
            completed = false))
        )
      }
    }
  }

  "GET /users" should {
    "return users" in {
      setTime(100)
      addUser("john","john@gmail.com")
      addUser("stefan","stefan@gmail.com")
      Get(s"$ApiPrefix/users") ~> routes ~> check {
        responseAs[GetUsersDto] shouldBe GetUsersDto(List(
          GetUserDto(1, "john", "john@gmail.com"),
          GetUserDto(2, "stefan", "stefan@gmail.com")
        ))
      }
    }
  }

  "POST and GET /penalties" should {
    "add and return penalties" in {
      val userId = addUser("john","john@gmail.com").userId
      Post(s"$ApiPrefix/penalties",AddPenaltyDto(userId,10,"did not sweep")) ~> routes ~> check {
        responseAs[PenaltyId] shouldBe PenaltyId(1)
      }
      Get(s"$ApiPrefix/penalties") ~> routes ~> check {
        responseAs[GetPenaltiesDto] shouldBe GetPenaltiesDto(List(
          GetPenaltyDto(1,userId,10,"did not sweep")
        ))
      }
    }
  }

  def addChore(name: String = "dust off", points: Int = 10, interval: Option[Int] = Some(5)): ChoreId = {
    Post(s"$ApiPrefix/chores", AddChoreDto(name, points, interval)) ~> routes ~> check {
      responseAs[ChoreId]
    }
  }

  def getChores(): GetChoresDto = {
    Get(s"$ApiPrefix/chores") ~> routes ~> check {
      responseAs[GetChoresDto]
    }
  }

  def addUser(name: String = "mark", email: String = "mark@gmail.com"): UserId = {
    Post(s"$ApiPrefix/users", AddUserDto(name, email)) ~> routes ~> check {
      responseAs[UserId]
    }
  }

  def addTask(userId:UserId,choreId:ChoreId): TaskId = {
    Post(s"$ApiPrefix/tasks",AddTaskDto(choreId,userId)) ~> routes ~> check {
      responseAs[TaskId]
    }
  }

  def getTasks(): GetTasksDto = {
    Get(s"$ApiPrefix/tasks") ~> routes ~> check {
      responseAs[GetTasksDto]
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
