package com.kuba.dziworski.chores.splitter

import com.typesafe.config.ConfigFactory

trait AppConfig {
  val config = ConfigFactory.defaultApplication()
  val apiVersion : String = config.getString("api.version")
  val serverHost: String = config.getString("server.host")
  val serverPort: Int = config.getInt("server.port")
  val jdbcUrl : String = config.getString("db.url")
  val jdbcUser : String = config.getString("db.user")
  val jdbcPassword : String = config.getString("db.password")
}
