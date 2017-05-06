package com.kuba.chords.splitter.util

import java.sql.Timestamp
import java.time.temporal.ChronoUnit.DAYS
import java.time.{Clock, Instant, LocalDate, LocalDateTime}



object TimeUtil {

  def now(implicit clock: Clock) = Timestamp.from(Instant.now(clock)).getTime

  def isBeforeToday(milis: Long)(implicit clock: Clock) = {
    daysSince(milis) > 0
  }

  def daysSince(milis:Long)(implicit clock:Clock): Long = {
    val today = LocalDate.now(clock)
    val day = Instant.ofEpochMilli(milis).atZone(clock.getZone).toLocalDate
    DAYS.between(day,today)
  }
}
