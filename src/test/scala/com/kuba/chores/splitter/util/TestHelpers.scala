package com.kuba.chores.splitter.util

import java.time.{Clock, Instant}

import org.scalamock.scalatest.MockFactory

trait TestHelpers extends MockFactory {

  implicit val clockMock = mock[Clock]

  def setTime(time: Long): Any = {
    (clockMock.instant _).expects().anyNumberOfTimes.returning(Instant.ofEpochMilli(time))
  }

}
