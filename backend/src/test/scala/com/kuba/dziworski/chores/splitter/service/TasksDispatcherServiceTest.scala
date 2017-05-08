package com.kuba.dziworski.chores.splitter.service

import akka.Done
import com.kuba.dziworski.chores.splitter.api.routes.dto.ChoreDtos.{AddChoreDto, ChoreId, GetChoreDto}
import com.kuba.dziworski.chores.splitter.api.routes.dto.TaskDtos.{AddTaskDto, TaskId}
import com.kuba.dziworski.chores.splitter.api.routes.dto.UserDtos.{AddUserDto, UserId}
import com.kuba.dziworski.chores.splitter.api.routes.dto.UserPoint
import com.kuba.dziworski.chores.splitter.service.TasksDispatcherService.{DispatchNotRequired, Dispatched}
import com.kuba.dziworski.chores.splitter.util.{DbSetUp, TestHelpers}
import org.joda.time.DateTime
import org.scalatest._

class TasksDispatcherServiceTest extends FunSuite with Matchers with DbSetUp with TestHelpers with BeforeAndAfterEach {

  val uService = new UsersService(db)
  val tService = new TasksService(db)
  val chService = new ChoresService(db)
  val pService = new PenaltyService(db)
  val taskDispatcher = new TasksDispatcherService(db, uService, chService, pService, tService)

  test("task dispatcher should dispatch only if it has not dispatched in current day") {
    val dateTimeNow = new DateTime(2017, 12, 30, 10, 30, 0)
    val now = dateTimeNow.getMillis
    val tommorow = dateTimeNow.plusDays(1).getMillis
    val oneHourLater = dateTimeNow.plusHours(1).getMillis
    setTime(now)
    await(taskDispatcher.dispatch()) shouldBe Dispatched
    await(taskDispatcher.getLastTaskDispatch) shouldBe now
    setTime(oneHourLater)
    await(taskDispatcher.dispatch()) shouldBe DispatchNotRequired
    setTime(tommorow)
    await(taskDispatcher.dispatch()) shouldBe Dispatched
    await(taskDispatcher.getLastTaskDispatch) shouldBe tommorow
  }

  test("task dispatcher should dispatch tasks to users") {
    val john = addUser("john")
    val mark = addUser("mark")
    val johnId = john.userId
    val markId = mark.userId
    val dishes = addChore("dishes", 5, Some(1))
    val cook = addChore("cook", 10, None)
    val laundry = addChore("laundry", 10, Some(2))
    val iron = addChore("iron", 10, Some(5))

    val firstDispatchTime = new DateTime(2017, 10, 25, 10, 30, 0).getMillis
    setTime(firstDispatchTime)
    await(taskDispatcher.dispatch()) shouldBe Dispatched
    getTasks shouldBe List(
      (laundry, johnId, firstDispatchTime, false),
      (iron, markId, firstDispatchTime, false),
      (dishes, markId, firstDispatchTime, false)
    )
    getUsersPoints shouldBe List(
      (johnId, 0),
      (markId, 0)
    )
    setCompleted(TaskId(1))
    setCompleted(TaskId(2))
    setCompleted(TaskId(3))
    getUsersPoints shouldBe List(
      (johnId, 10),
      (markId, 15)
    )

    val secondDispatchTime = new DateTime(firstDispatchTime).plusDays(1).getMillis
    setTime(secondDispatchTime)
    await(taskDispatcher.dispatch()) shouldBe Dispatched
    getTasks shouldBe List(
      (dishes, johnId, secondDispatchTime, false),
      (laundry, johnId, firstDispatchTime, true),
      (iron, markId, firstDispatchTime, true),
      (dishes, markId, firstDispatchTime, true)
    )
    setCompleted(TaskId(4))
    getUsersPoints shouldBe List(
      (johnId, 15),
      (markId, 15)
    )

    val thirdDispatchTime = new DateTime(secondDispatchTime).plusDays(1).getMillis
    setTime(thirdDispatchTime)
    await(taskDispatcher.dispatch()) shouldBe Dispatched
    getTasks shouldBe List(
      (laundry, johnId, thirdDispatchTime, false),
      (dishes, markId, thirdDispatchTime, false),
      (dishes, johnId, secondDispatchTime, true),
      (laundry, johnId, firstDispatchTime, true),
      (iron, markId, firstDispatchTime, true),
      (dishes, markId, firstDispatchTime, true)
    )
    setCompleted(TaskId(5))
    setCompleted(TaskId(6))
    getUsersPoints shouldBe List(
      (johnId, 25),
      (markId, 20)
    )
  }

  test("task dispatcher should add penalty when task not completed") {
    val john = addUser("john")
    val dishes = addChore("dishes", 10, Some(2))
    val uncompletedTask = addTask(ChoreId(dishes.id), john)
    await(taskDispatcher.dispatch()) shouldBe Dispatched
    getUsersPoints shouldBe List(
      (john.userId, -5)
    )
    setCompleted(TaskId(2))
    setTime(new DateTime(currentTime).plusDays(1).getMillis)
    await(taskDispatcher.dispatch()) shouldBe Dispatched
    getUsersPoints shouldBe List(
      (john.userId, 5)
    )
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

  case class AssertedTask(chore: GetChoreDto, userId: Long, assignedAt: Long, completed: Boolean)

  def getTasks: List[(GetChoreDto, Long, Long, Boolean)] = {
    await(tService.getTasks()).tasks.map(t => (t.chore, t.userId, t.assignedAt, t.completed))
  }

  def addUser(name: String): UserId = {
    val dto = AddUserDto(name, s"$name@gmail.com")
    await(uService.addUser(dto))
  }

  def addTask(choreId: ChoreId, userId: UserId): TaskId = {
    await(tService.addTask(AddTaskDto(choreId, userId)))
  }

  def addChore(name: String, points: Int, interval: Option[Int]): GetChoreDto = {
    val dto = AddChoreDto(name, points, interval)
    val id = await(chService.addChore(dto))
    GetChoreDto(id.choreId, name, points, interval)
  }

  def setCompleted(taskId: TaskId): Done = {
    await(tService.setCompleted(taskId, completed = true))
  }

  def getUsersPoints: List[(Long, Int)] = {
    await(uService.getUsers).users.map(u => (u.id, u.points))
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
