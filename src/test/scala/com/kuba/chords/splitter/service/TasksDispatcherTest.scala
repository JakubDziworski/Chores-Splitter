package com.kuba.chords.splitter.service

import com.kuba.chords.splitter.api.routes.dto.ChoreDtos.{ChoreId, GetChoreDto}
import com.kuba.chords.splitter.api.routes.dto.TaskDtos.{AddTaskDto, GetTaskDto, GetTasksDto}
import com.kuba.chords.splitter.api.routes.dto.UserDtos.UserId
import com.kuba.chords.splitter.api.routes.dto.UserPoint
import org.scalatest.{FunSuite, Matchers}

class TasksDispatcherTest extends FunSuite with Matchers {

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
    TasksDispatcher.assignTasksForToday(users, chores) shouldBe List(
      task(choreId = 2, userId = 3),
      task(choreId = 1, userId = 4)
    )
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
