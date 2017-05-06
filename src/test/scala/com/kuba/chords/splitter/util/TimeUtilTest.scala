package com.kuba.chords.splitter.util


import com.kuba.chores.splitter.util.TestHelpers
import org.joda.time.DateTime
import org.scalatest.{FunSuite, Matchers}

/**
  * Created by jdziworski on 05.05.17.
  */
class TimeUtilTest extends FunSuite with TestHelpers with Matchers {

  test("isBeforeToday should return correct values") {
    val now = new DateTime(2017,12,25,5,30,0)
    setTime(now.getMillis)
    TimeUtil.isBeforeToday(now.minusHours(1).getMillis) shouldBe false
    TimeUtil.isBeforeToday(now.minusHours(3).getMillis) shouldBe false
    TimeUtil.isBeforeToday(now.plusHours(1).getMillis) shouldBe false
    TimeUtil.isBeforeToday(now.plusHours(2).getMillis) shouldBe false
    TimeUtil.isBeforeToday(now.plusDays(1).getMillis) shouldBe false
    TimeUtil.isBeforeToday(now.plusDays(3).getMillis) shouldBe false
    TimeUtil.isBeforeToday(now.plusYears(5).getMillis) shouldBe false

    TimeUtil.isBeforeToday(0) shouldBe true
    TimeUtil.isBeforeToday(now.minusHours(6).getMillis) shouldBe true
    TimeUtil.isBeforeToday(now.minusHours(12).getMillis) shouldBe true
    TimeUtil.isBeforeToday(now.minusDays(22).getMillis) shouldBe true
  }

  test("days since") {
    val now = new DateTime(2017,12,25,5,30,0)
    setTime(now.getMillis)
    TimeUtil.daysSince(now.minusHours(1).getMillis) shouldBe 0
    TimeUtil.daysSince(now.minusHours(5).getMillis) shouldBe 0
    TimeUtil.daysSince(now.minusHours(5).minusMinutes(30).getMillis) shouldBe 0
    TimeUtil.daysSince(now.minusHours(5).minusMinutes(31).getMillis) shouldBe 1
    TimeUtil.daysSince(now.minusHours(6).getMillis) shouldBe 1
    TimeUtil.daysSince(now.minusDays(1).getMillis) shouldBe 1
    TimeUtil.daysSince(now.minusDays(2).getMillis) shouldBe 2
    TimeUtil.daysSince(now.minusDays(3).getMillis) shouldBe 3
  }
}
