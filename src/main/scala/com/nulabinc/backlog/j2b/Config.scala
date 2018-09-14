package com.nulabinc.backlog.j2b

import com.nulabinc.backlog.j2b.Config.CommandType
import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.migration.common.conf.BacklogApiConfiguration
import com.typesafe.config.ConfigFactory

case class Config(
  backlogKey: String = "",
  backlogUrl: String = "",
  jiraUsername: String = "",
  jiraPassword: String = "",
  jiraUrl: String = "",
  projectKey: String = "",
  commandType: Option[CommandType] = None
) {

  val getAppConfiguration: AppConfiguration = {
    val keys = projectKey.split(":")
    val jira = keys(0)
    val backlog = if (keys.length == 2) keys(1) else keys(0).toUpperCase.replaceAll("-", "_")

    new AppConfiguration(
      jiraConfig = JiraApiConfiguration(
        username = jiraUsername,
        password = jiraPassword,
        url = jiraUrl,
        projectKey = jira
      ),
      backlogConfig = BacklogApiConfiguration(
        url = backlogUrl,
        key = backlogKey,
        projectKey = backlog
      )
    )
  }
}

  /*
    private[this] def getConfiguration(cli: CommandLineInterface) = Try {
    val keys: Array[String] = cli.importCommand.projectKey().split(":")
    val jira: String        = keys(0)
    val backlog: String     = if (keys.length == 2) keys(1) else keys(0).toUpperCase.replaceAll("-", "_")

    new AppConfiguration(
      jiraConfig    = JiraApiConfiguration(username = cli.importCommand.jiraUsername(), password = cli.importCommand.jiraPassword(), cli.importCommand.jiraUrl(), projectKey = jira),
      backlogConfig = BacklogApiConfiguration(url = cli.importCommand.backlogUrl(), key = cli.importCommand.backlogKey(), projectKey = backlog)
    )
  }
   */

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
