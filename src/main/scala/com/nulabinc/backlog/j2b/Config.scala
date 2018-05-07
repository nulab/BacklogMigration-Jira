package com.nulabinc.backlog.j2b

import com.typesafe.config.ConfigFactory

object Config {

  private val config = ConfigFactory.load()

  object Application {
    private val applicationConfig = config.getConfig("application")

    val name: String = applicationConfig.getString("name")
    val version: String = applicationConfig.getString("version")
    val fileName: String = applicationConfig.getString("fileName")
  }
}
