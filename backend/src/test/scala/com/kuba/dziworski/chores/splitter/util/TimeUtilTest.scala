package com.kuba.dziworski.chores.splitter.util


import com.kuba.dziworski.chores.splitter.util.TestHelpers
import org.joda.time.DateTime
import org.scalatest.{FunSuite, Matchers}

/**
  * Created by jdziworski on 05.05.17.
  */
class TimeUtilTest extends FunSuite with TestHelpers with Matchers {

  test("hours since") {
    val now = new DateTime(2017,12,25,5,30,0)
    setTime(now.getMillis)
    TimeUtil.daysSince(now.plusHours(18).getMillis) shouldBe 0
    TimeUtil.daysSince(now.minusHours(6).getMillis) shouldBe 1
    TimeUtil.daysSince(now.minusHours(29).getMillis) shouldBe 1
    TimeUtil.daysSince(now.minusHours(30).getMillis) shouldBe 2
    TimeUtil.daysSince(now.plusHours(19).getMillis) shouldBe -1
  }

  test("hour of day") {
    val time = new DateTime(2017,12,25,5,30,0)
    setTime(time.getMillis)
    TimeUtil.getHourOfDay shouldBe 5
    setTime(time.plusMinutes(30).getMillis)
    TimeUtil.getHourOfDay shouldBe 6
  }
}
