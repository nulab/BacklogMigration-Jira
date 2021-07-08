package com.nulabinc.backlog.j2b

import com.nulabinc.backlog.j2b.Config.CommandType
import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.migration.common.client.IAAH
import com.nulabinc.backlog.migration.common.conf.BacklogApiConfiguration
import com.typesafe.config.ConfigFactory

case class Config(
    backlogKey: String = "",
    backlogUrl: String = "",
    jiraUsername: String = "",
    jiraApiKey: String = "",
    jiraUrl: String = "",
    projectKey: String = "",
    retryCount: Int = 20,
    iaah: Option[IAAH] = None,
    commandType: Option[CommandType] = None
) {

  def getAppConfiguration(iaah: IAAH): AppConfiguration = {
    val keys = projectKey.split(":")
    val jira = keys(0)
    val backlog =
      if (keys.length == 2) keys(1)
      else keys(0).toUpperCase.replaceAll("-", "_")

    new AppConfiguration(
      jiraConfig = JiraApiConfiguration(
        username = jiraUsername,
        apiKey = jiraApiKey,
        url = jiraUrl,
        projectKey = jira
      ),
      backlogConfig = BacklogApiConfiguration(
        url = backlogUrl,
        key = backlogKey,
        projectKey = backlog,
        iaah = iaah
      ),
      retryCount = retryCount
    )
  }
}

object Config {

  private val config = ConfigFactory.load()

  object Application {
    private val applicationConfig = config.getConfig("application")

    val name: String = applicationConfig.getString("name")
    val version: String = applicationConfig.getString("version")
    val fileName: String = applicationConfig.getString("fileName")
  }

  sealed trait CommandType
  case object ImportCommand extends CommandType
  case object ExportCommand extends CommandType
}
