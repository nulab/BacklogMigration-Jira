package com.nulabinc.backlog.j2b.core

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

  val execute = new Subcommand("execute") {
    val backlogKey   = opt[String]("backlog.key", descr = Messages("cli.help.backlog.key"), required = true, noshort = true)
    val backlogUrl   = opt[String]("backlog.url", descr = Messages("cli.help.backlog.url"), required = true, noshort = true)
    val jiraUsername = opt[String]("jira.username", descr = Messages("cli.help.jira.username"), required = true, noshort = true)
    val jiraPassword = opt[String]("jira.password", descr = Messages("cli.help.jira.password"), required = true, noshort = true)
    val jiraUrl      = opt[String]("jira.url", descr = Messages("cli.help.jira.url"), required = true, noshort = true)

    val projectKey = opt[String]("projectKey", descr = Messages("cli.help.projectKey"), required = true)
    val importOnly = opt[Boolean]("importOnly", descr = Messages("cli.help.importOnly"), required = true)
    val optOut     = opt[Boolean]("optOut", descr = Messages("cli.help.optOut"), required = false)
    val help       = opt[String]("help", descr = Messages("cli.help.show_help"))
  }

  val init = new Subcommand("init") {
    val backlogKey   = opt[String]("backlog.key", descr = Messages("cli.help.backlog.key"), required = true, noshort = true)
    val backlogUrl   = opt[String]("backlog.url", descr = Messages("cli.help.backlog.url"), required = true, noshort = true)
    val jiraUsername = opt[String]("jira.username", descr = Messages("cli.help.jira.username"), required = true, noshort = true)
    val jiraPassword = opt[String]("jira.password", descr = Messages("cli.help.jira.password"), required = true, noshort = true)
    val jiraUrl      = opt[String]("jira.url", descr = Messages("cli.help.jira.url"), required = true, noshort = true)

    val projectKey = opt[String]("projectKey", descr = Messages("cli.help.projectKey"), required = true)
    val help       = opt[String]("help", descr = Messages("cli.help.show_help"))
  }

  addSubcommand(execute)
  addSubcommand(init)

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
      cli.subcommand match {
        case Some(cli.init) => J2BCli.init(getConfiguration(cli))
        case _              => J2BCli.help()
      }
      exit(0)
    } catch {
      case e: Throwable =>
        logger.error(e.getMessage, e)
        exit(1)
    }
  }

  private[this] def getConfiguration(cli: CommandLineInterface) = {
    val keys: Array[String] = cli.execute.projectKey().split(":")
    val jira: String        = keys(0)
    val backlog: String     = if (keys.length == 2) keys(1) else keys(0).toUpperCase.replaceAll("-", "_")

    ConsoleOut.println(
      s"""--------------------------------------------------
         |${Messages("common.jira")} ${Messages("common.username")}[${cli.execute.jiraUsername()}]
         |${Messages("common.jira")} ${Messages("common.password")}[${cli.execute.jiraPassword()}]
         |${Messages("common.jira")} ${Messages("common.url")}[${cli.execute.jiraUrl()}]
         |${Messages("common.jira")} ${Messages("common.project_key")}[${jira}]
         |${Messages("common.backlog")} ${Messages("common.url")}[${cli.execute.backlogUrl()}]
         |${Messages("common.backlog")} ${Messages("common.access_key")}[${cli.execute.backlogKey()}]
         |${Messages("common.backlog")} ${Messages("common.project_key")}[${backlog}]
         |${Messages("common.importOnly")}[${cli.execute.importOnly()}]
         |${Messages("common.optOut")}[${cli.execute.optOut.toOption.getOrElse(false)}]
         |--------------------------------------------------
     |""".stripMargin)

    new AppConfiguration(
      jiraConfig    = new JiraApiConfiguration(username = cli.execute.jiraUsername(), password = cli.execute.jiraPassword(), cli.execute.jiraUrl(), projectKey = jira),
      backlogConfig = new BacklogApiConfiguration(url = cli.execute.backlogUrl(), key = cli.execute.backlogKey(), projectKey = backlog),
      importOnly    = cli.execute.importOnly(),
      optOut        = cli.execute.optOut())
  }

  private[this] def exit(exitCode: Int): Unit = {
    AnsiConsole.systemUninstall()
    System.exit(exitCode)
  }
}
