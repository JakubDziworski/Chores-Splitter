package com.kuba.chords.splitter

import com.typesafe.config.ConfigFactory

trait AppConfig {
  val config = ConfigFactory.defaultApplication()
  val apiVersion : String = config.getString("api.version")
  val serverHost: String = config.getString("server.host")
  val serverPort: Int = config.getInt("server.port")
}
