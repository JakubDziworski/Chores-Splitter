package com.kuba.dziworski.chores.splitter.util

import org.flywaydb.core.Flyway
import slick.jdbc.H2Profile.api._

trait DbSetUp {
  val flyway = new Flyway
  flyway.setDataSource("jdbc:h2:mem:chore_splitter;DB_CLOSE_DELAY=-1;MODE=PostgreSQL", "SA", "")
  val db = Database.forDataSource(flyway.getDataSource(), None)

  def initDb(): Unit = {
    flyway.migrate()
  }

  def cleanDb(): Unit = {
    flyway.clean()
  }
}
