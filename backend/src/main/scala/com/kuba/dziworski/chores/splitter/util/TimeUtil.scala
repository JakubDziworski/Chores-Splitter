package com.kuba.dziworski.chores.splitter.util

import java.sql.Timestamp
import java.time.temporal.ChronoUnit.DAYS
import java.time.{Clock, Instant, ZonedDateTime}



object TimeUtil {

  def now(implicit clock: Clock) = Timestamp.from(Instant.now(clock)).getTime

  def daysSince(millis:Long)(implicit clock:Clock): Long = {
    val today = ZonedDateTime.now(clock).toLocalDate
    val day = Instant.ofEpochMilli(millis).atZone(clock.getZone).toLocalDate
    DAYS.between(day,today)
  }

  def getHourOfDay(implicit clock: Clock) : Int = {
    Instant.now(clock).atZone(clock.getZone).getHour
  }
}
