package com.nulabinc.backlog.j2b

import java.util.Locale

import com.nulabinc.backlog.j2b.cli.J2BCli
import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.utils.ClassVersion
import com.nulabinc.backlog.migration.common.conf.{BacklogApiConfiguration, BacklogConfiguration}
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.osinka.i18n.Messages
import org.fusesource.jansi.AnsiConsole
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
    val optOut     = opt[Boolean]("optOut", descr = Messages("cli.help.optOut"), required = false)
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

object J2B extends BacklogConfiguration with Logging {

  def main(args: Array[String]): Unit = {

    ConsoleOut.println(
      s"""|$applicationName
          |--------------------------------------------------
       """.stripMargin)

    // Console initialization
    AnsiConsole.systemInstall()

    // Set language
    language match {
      case "ja" => Locale.setDefault(Locale.JAPAN)
      case "en" => Locale.setDefault(Locale.US)
      case _    => Locale.setDefault(Locale.getDefault)
    }

    // DisableSSLCertificateCheckUtil.disableChecks() // TODO: ???
    // checkRelease()                                 // TODO: Github repo does not exist


    if ( ! ClassVersion.isValid()) {
      ConsoleOut.error(Messages("cli.require_java8", System.getProperty("java.specification.version")))
      exit(1)
    }

    // Run
    try {
      val cli = new CommandLineInterface(args)
      val config = getConfiguration(cli)
      cli.subcommand match {
        case Some(cli.importCommand)  => J2BCli.`import`(config)
        case Some(cli.exportCommand)  => J2BCli.export(config)
        case _                        => J2BCli.help()
      }
      exit(0)
    } catch {
      case e: Throwable =>
        logger.error(e.getMessage, e)
        ConsoleOut.error(s"${Messages("cli.error.unknown")}:${e.getMessage}")
        exit(1)
    }
  }

  private[this] def getConfiguration(cli: CommandLineInterface) = {
    val keys: Array[String] = cli.importCommand.projectKey().split(":")
    val jira: String        = keys(0)
    val backlog: String     = if (keys.length == 2) keys(1) else keys(0).toUpperCase.replaceAll("-", "_")

    ConsoleOut.println(
      s"""--------------------------------------------------
         |${Messages("common.jira")} ${Messages("common.username")}[${cli.importCommand.jiraUsername()}]
         |${Messages("common.jira")} ${Messages("common.password")}[${cli.importCommand.jiraPassword()}]
         |${Messages("common.jira")} ${Messages("common.url")}[${cli.importCommand.jiraUrl()}]
         |${Messages("common.jira")} ${Messages("common.project_key")}[${jira}]
         |${Messages("common.backlog")} ${Messages("common.url")}[${cli.importCommand.backlogUrl()}]
         |${Messages("common.backlog")} ${Messages("common.access_key")}[${cli.importCommand.backlogKey()}]
         |${Messages("common.backlog")} ${Messages("common.project_key")}[${backlog}]
         |${Messages("common.optOut")}[${cli.importCommand.optOut.toOption.getOrElse(false)}]
         |--------------------------------------------------
     |""".stripMargin)

    new AppConfiguration(
      jiraConfig    = new JiraApiConfiguration(username = cli.importCommand.jiraUsername(), password = cli.importCommand.jiraPassword(), cli.importCommand.jiraUrl(), projectKey = jira),
      backlogConfig = new BacklogApiConfiguration(url = cli.importCommand.backlogUrl(), key = cli.importCommand.backlogKey(), projectKey = backlog),
      optOut        = cli.importCommand.optOut())
  }

  private[this] def exit(exitCode: Int): Unit = {
    AnsiConsole.systemUninstall()
    System.exit(exitCode)
  }
}
