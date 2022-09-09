package com.nulabinc.backlog.j2b.core

import com.nulabinc.backlog.j2b.Config
import com.nulabinc.backlog.migration.common.client.IAAH
import com.nulabinc.backlog.migration.common.utils.Logging
import com.osinka.i18n.Messages

object ConfigParser extends Logging {

  def parse(args: Array[String])(defaultRetryCount: Int): Option[Config] =
    parser.parse(args, Config(retryCount = defaultRetryCount))

  def help()(defaultRetryCount: Int): Unit = {
    parser.parse(Seq("--help"), Config(retryCount = defaultRetryCount)).getOrElse("")
  }

  private def parser =
    new scopt.OptionParser[Config](Config.Application.fileName) {

      head(Config.Application.name, Config.Application.version)

      opt[String]("backlog.url")
        .required()
        .action((x, c) => c.copy(backlogUrl = x))
        .text(Messages("cli.help.backlog.url"))

      opt[String]("backlog.key")
        .required()
        .action((x, c) => c.copy(backlogKey = x))
        .text(Messages("cli.help.backlog.key"))

      opt[String]("jira.username")
        .required()
        .action((x, c) => c.copy(jiraUsername = x))
        .text(Messages("cli.help.jira.username"))

      opt[String]("jira.apiKey")
        .required()
        .action((x, c) => c.copy(jiraApiKey = x))
        .text(Messages("cli.help.jira.apiKey"))

      opt[String]("jira.url")
        .required()
        .action((x, c) => c.copy(jiraUrl = x))
        .text(Messages("cli.help.jira.url"))

      opt[String]("projectKey")
        .required()
        .action((x, c) => c.copy(projectKey = x))
        .text(Messages("cli.help.projectKey"))

      opt[Int]("retryCount")
        .action((x, c) => c.copy(retryCount = x))
        .text(Messages("cli.help.retryCount"))

      opt[String]("iaah")
        .action((x, c) => c.copy(iaah = Some(IAAH(x))))

      cmd("export")
        .action((_, c) => c.copy(commandType = Some(Config.ExportCommand)))
        .text("Export the JIRA project.")

      cmd("import")
        .action { (_, c) => c.copy(commandType = Some(Config.ImportCommand)) }
        .text("Import the project to Backlog.")

      help("help") text "print this usage text."

      override def showUsageOnError = Some(true)

    }
}
