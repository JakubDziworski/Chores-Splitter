package com.kuba.chords.splitter.util

import java.sql.Timestamp
import java.time.{Clock, Instant}

import org.joda.time.{DateTime, LocalDateTime}

object TimeUtil {
  def now(implicit clock: Clock) = Timestamp.from(Instant.now(clock)).getTime
  def isOutdated(milis: Long) = {
    val today = LocalDateTime.now()
    val day = new DateTime(milis).toLocalDate
    day.isBefore(today)
  }

}
