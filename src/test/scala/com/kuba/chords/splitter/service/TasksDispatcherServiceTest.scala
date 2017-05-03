package com.kuba.chords.splitter.service

import com.kuba.chords.splitter.api.routes.dto.ChoreDtos.{ChoreId, GetChoreDto}
import com.kuba.chords.splitter.api.routes.dto.TaskDtos.AddTaskDto
import com.kuba.chords.splitter.api.routes.dto.UserDtos.UserId
import com.kuba.chords.splitter.api.routes.dto.UserPoint
import com.kuba.chores.splitter.util.{DbSetUp, TestHelpers}
import org.scalatest.{BeforeAndAfterEach, FunSuite, Ignore, Matchers}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Await
import scala.concurrent.duration._

class TasksDispatcherServiceTest extends FunSuite with Matchers with DbSetUp with TestHelpers with BeforeAndAfterEach {

  val uService = new UsersService(db)
  val tService = new TasksService(db)
  val chService = new ChoresService(db)
  val taskDispatcher = new TasksDispatcherService(db,uService,chService ,tService)

  test("quick") {
    setTime(50001024)
    val result = Await.result(taskDispatcher.dispatch(),3 seconds)
  }

  test("testAssignTasksForToday") {

    val chores = List(
      chore(id = 1, points = 20),
      chore(id = 2, points = 15)
    )

    val users = List(
      user(id = 1, points = 15),
      user(id = 2, points = 20),
      user(id = 3, points = 10),
      user(id = 4, points = 7),
      user(id = 5, points = 23)
    )
    TasksDispatcherService.assignTasksForToday(users, chores) shouldBe List(
      task(choreId = 2, userId = 3),
      task(choreId = 1, userId = 4)
    )
  }

  override protected def beforeEach(): Unit = {
    initDb()
  }

  override protected def afterEach(): Unit = {
    cleanDb()
  }

  def chore(id: Long, points: Int) = {
    GetChoreDto(id, s"name ${id}", points, None)
  }

  def user(id: Long, points: Int) = {
    UserPoint(UserId(id), points)
  }

  def task(choreId: Long, userId: Long) = {
    AddTaskDto(ChoreId(choreId), UserId(userId))
  }

}
