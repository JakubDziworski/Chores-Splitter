package com.kuba.dziworski.chores.splitter.service

object Failures {
  case object TimeForTaskAlreadyEndedException extends RuntimeException("Cannot change completion state of task which already ended")

}
