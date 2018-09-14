package com.nulabinc.backlog.j2b

import com.nulabinc.backlog.j2b.cli.J2BCli
import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.core.{CommandLineInterface, GithubRelease}
import com.nulabinc.backlog.j2b.dsl.{AppDSL, ConsoleDSL}
import com.nulabinc.backlog.j2b.interpreters.{AsyncAppInterpreter, AsyncConsoleInterpreter}
import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.utils.ClassVersion
import com.nulabinc.backlog.migration.common.conf.{BacklogApiConfiguration, BacklogConfiguration}
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.osinka.i18n.Messages
import monix.eval.Task
import monix.execution.Scheduler
import org.fusesource.jansi.AnsiConsole

import scala.util.{Failure, Success, Try}


object App extends BacklogConfiguration with Logging {

  def main(args: Array[String]): Unit = {

    // DisableSSLCertificateCheckUtil.disableChecks() // TODO: ???

    AnsiConsole.systemInstall()

    if ( ! ClassVersion.isValid()) {
      ConsoleOut.error(Messages("cli.require_java8", System.getProperty("java.specification.version")))
      exit(1)
    }

    implicit val exc: Scheduler = monix.execution.Scheduler.Implicits.global

    val interpreter = AsyncAppInterpreter(
      consoleInterpreter = AsyncConsoleInterpreter()
    )

    val program = for {
      _ <- AppDSL.fromConsole(ConsoleDSL.print(startMessage(applicationName)))
//      _ <- AppDSL.pure(GithubRelease.checkRelease()) // TODO:
      _ <- AppDSL.setLanguage(language)
      _ <- getConfiguration(new CommandLineInterface(args)) match {
        case Success(config) =>
          for {
            _ <- AppDSL.fromConsole(ConsoleDSL.print(configurationMessage(config)))
          } yield ()
        case Failure(error) =>
          logger.error(error.getMessage)
          J2BCli.help()
          for {
            _ <- AppDSL.fromConsole(ConsoleDSL.error(s"${Messages("cli.error.args")}"))
            _ <- AppDSL.exit(1)
          } yield ()
      }
    } yield ()

    val cleanup = interpreter.terminate().flatMap(_ =>
      Task.unit
    )

    interpreter
      .run(program)
      .flatMap(_ => cleanup)
      .onErrorHandleWith { ex =>
        cleanup.map { _ =>
          logger.error(ex.getMessage, ex)
          exit(1, ex)
        }
      }
      .runAsync
//      .foreach { _ =>
//        // Run
//
//
//        getConfiguration(cli) match {
//          case Success(config) =>
//            cli.subcommand match {
//              case Some(cli.importCommand) => J2BCli.`import`(config)
//              case Some(cli.exportCommand) => J2BCli.export(config, NextCommand.command(args))
//              case _                       => J2BCli.help()
//            }
//            exit(0)
//        }
//      }
  }

  private[this] def getConfiguration(cli: CommandLineInterface) = Try {
    val keys: Array[String] = cli.importCommand.projectKey().split(":")
    val jira: String        = keys(0)
    val backlog: String     = if (keys.length == 2) keys(1) else keys(0).toUpperCase.replaceAll("-", "_")

    new AppConfiguration(
      jiraConfig    = JiraApiConfiguration(username = cli.importCommand.jiraUsername(), password = cli.importCommand.jiraPassword(), cli.importCommand.jiraUrl(), projectKey = jira),
      backlogConfig = BacklogApiConfiguration(url = cli.importCommand.backlogUrl(), key = cli.importCommand.backlogKey(), projectKey = backlog)
    )
  }

  private def exit(exitCode: Int): Unit = {
    System.exit(exitCode)
  }

  private def exit(exitCode: Int, error: Throwable): Unit = {
    ConsoleOut.error("ERROR: " + error.getMessage + "\n" + error.printStackTrace())
    exit(exitCode)
  }

  private def startMessage(appName: String): String =
    s"""|$appName
        |--------------------------------------------------
       """.stripMargin

  private def configurationMessage(conf: AppConfiguration): String =
    s"""--------------------------------------------------
       |${Messages("common.jira")} ${Messages("common.username")}[${conf.jiraUsername}]
       |${Messages("common.jira")} ${Messages("common.url")}[${conf.jiraUrl}]
       |${Messages("common.jira")} ${Messages("common.project_key")}[${conf.jiraKey}]
       |${Messages("common.backlog")} ${Messages("common.url")}[${conf.backlogUrl}]
       |${Messages("common.backlog")} ${Messages("common.access_key")}[${conf.backlogKey}]
       |${Messages("common.backlog")} ${Messages("common.project_key")}[${conf.backlogKey}]
       |https.proxyHost[${Option(System.getProperty("https.proxyHost")).getOrElse("")}]
       |https.proxyPort[${Option(System.getProperty("https.proxyPort")).getOrElse("")}]
       |https.proxyUser[${Option(System.getProperty("https.proxyUser")).getOrElse("")}]
       |https.proxyPassword[${Option(System.getProperty("https.proxyPassword")).getOrElse("")}]
       |--------------------------------------------------
       |""".stripMargin
}
