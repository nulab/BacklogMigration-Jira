package com.nulabinc.backlog.j2b.core

import com.nulabinc.backlog.migration.common.conf.BacklogConfiguration
import com.nulabinc.backlog.migration.common.utils.Logging
import com.osinka.i18n.Messages
import org.rogach.scallop.{ScallopConf, Subcommand}

class CommandLineInterface(arguments: Seq[String]) extends ScallopConf(arguments) with BacklogConfiguration with Logging {

  banner(
    """Usage: Backlog Migration for Jira [OPTION]....

    """.stripMargin)

  footer("\n " + Messages("cli.help"))

  val importCommand = new Subcommand("import") {
    val backlogKey   = opt[String]("backlog.key", descr = Messages("cli.help.backlog.key"), required = true, noshort = true)
    val backlogUrl   = opt[String]("backlog.url", descr = Messages("cli.help.backlog.url"), required = true, noshort = true)
    val jiraUsername = opt[String]("jira.username", descr = Messages("cli.help.jira.username"), required = true, noshort = true)
    val jiraPassword = opt[String]("jira.password", descr = Messages("cli.help.jira.password"), required = true, noshort = true)
    val jiraUrl      = opt[String]("jira.url", descr = Messages("cli.help.jira.url"), required = true, noshort = true)

    val projectKey = opt[String]("projectKey", descr = Messages("cli.help.projectKey"), required = true)
    val help       = opt[String]("help", descr = Messages("cli.help.show_help"))
  }

  val exportCommand = new Subcommand("export") {
    val backlogKey   = opt[String]("backlog.key", descr = Messages("cli.help.backlog.key"), required = true, noshort = true)
    val backlogUrl   = opt[String]("backlog.url", descr = Messages("cli.help.backlog.url"), required = true, noshort = true)
    val jiraUsername = opt[String]("jira.username", descr = Messages("cli.help.jira.username"), required = true, noshort = true)
    val jiraPassword = opt[String]("jira.password", descr = Messages("cli.help.jira.password"), required = true, noshort = true)
    val jiraUrl      = opt[String]("jira.url", descr = Messages("cli.help.jira.url"), required = true, noshort = true)

    val projectKey = opt[String]("projectKey", descr = Messages("cli.help.projectKey"), required = true)
    val help       = opt[String]("help", descr = Messages("cli.help.show_help"))
  }

  addSubcommand(importCommand)
  addSubcommand(exportCommand)

  verify()
}
