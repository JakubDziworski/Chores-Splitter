package com.kuba.dziworski.chores.splitter.service

import com.kuba.dziworski.chores.splitter.api.routes.dto.ChoreDtos.{AddChoreDto, ChoreId, GetChoreDto, GetChoresDto}
import com.kuba.dziworski.chores.splitter.api.routes.dto.TaskDtos.{AddTaskDto, TaskId}
import com.kuba.dziworski.chores.splitter.api.routes.dto.UserDtos.{AddUserDto, UserId}
import com.kuba.dziworski.chores.splitter.util.{DbSetUp, TestHelpers}
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterEach, FunSuite, Matchers, WordSpec}

class ChoresServiceTest extends WordSpec with Matchers with DbSetUp with TestHelpers with BeforeAndAfterEach {

  val choresService = new ChoresService(db)
  val tasksService = new TasksService(db)
  val usersService = new UsersService(db)

  "getChoresAfterInterval" should {
    "calculate only for newest chore if chore was modified" in {
      val originalChore = AddChoreDto("sweep", 2, Some(1))
      val modifiedChore = originalChore.copy(interval = Some(3))
      choresService.addChore(originalChore)
      await(choresService.getChoresAfterInterval()) shouldBe GetChoresDto(List(
        GetChoreDto(1,"sweep",2,Some(1))
      ))
      await(usersService.addUser(AddUserDto("mathew","mathew@gmail.com")))
      await(tasksService.addTask(AddTaskDto(ChoreId(1),UserId(1))))
      await(tasksService.setCompleted(TaskId(1),completed = true))
      val lastChoreCompletionTime = new DateTime(currentTime)
      await(choresService.getChoresAfterInterval()) shouldBe GetChoresDto(List[GetChoreDto]())
      await(choresService.editChore(ChoreId(1),modifiedChore))
      await(choresService.getChoresAfterInterval()) shouldBe GetChoresDto(List[GetChoreDto]())
      setTime(lastChoreCompletionTime.plusDays(1).getMillis)
      await(choresService.getChoresAfterInterval()) shouldBe GetChoresDto(List[GetChoreDto]())
      setTime(lastChoreCompletionTime.plusDays(3).getMillis)
      await(choresService.getChoresAfterInterval()) shouldBe GetChoresDto(List(
        GetChoreDto(2,"sweep",2,Some(3))
      ))
    }
  }

  override protected def beforeEach(): Unit = {
    initDb()
  }

  override protected def afterEach(): Unit = {
    cleanDb()
  }
}
