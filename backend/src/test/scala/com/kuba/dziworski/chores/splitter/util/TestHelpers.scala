package com.kuba.dziworski.chores.splitter.util

import java.time.{Clock, Instant, ZoneId}

import org.joda.time.DateTime
import org.scalamock.scalatest.MockFactory

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

trait TestHelpers extends MockFactory {

  implicit val clockMock = mock[Clock]
  var currentTime = DateTime.now().getMillis
  autoVerify = false
  (clockMock.instant _).expects().anyNumberOfTimes.onCall(_ => Instant.ofEpochMilli(currentTime))
  (clockMock.getZone _).expects().anyNumberOfTimes.onCall(_ => ZoneId.systemDefault())

  def setTime(time: Long): Any = {
    println(s"setting time $time = ${new DateTime(time)}")
    this.currentTime = time
  }

  def await[T](future:Future[T]): T = {
    Await.result(future,3 seconds)
  }

}
