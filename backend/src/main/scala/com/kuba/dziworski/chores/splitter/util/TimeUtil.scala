package com.kuba.dziworski.chores.splitter.util

import java.sql.Timestamp
import java.time.temporal.ChronoUnit.HOURS
import java.time.{Clock, Instant, ZonedDateTime}



object TimeUtil {

  def now(implicit clock: Clock) = Timestamp.from(Instant.now(clock)).getTime

  def hoursSince(millis:Long)(implicit clock:Clock): Long = {
    val today = ZonedDateTime.now(clock)
    val day = Instant.ofEpochMilli(millis).atZone(clock.getZone)
    HOURS.between(day,today)
  }

  def getHourOfDay(implicit clock: Clock) : Int = {
    Instant.now(clock).atZone(clock.getZone).getHour
  }
}
