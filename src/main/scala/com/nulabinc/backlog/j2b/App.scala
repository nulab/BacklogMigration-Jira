package com.nulabinc.backlog.j2b

import com.nulabinc.backlog.j2b.cli.J2BCli
import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.core.{ConfigParser, NextCommand}
import com.nulabinc.backlog.j2b.dsl.{AppDSL, ConsoleDSL}
import com.nulabinc.backlog.j2b.interpreters.{AsyncAppInterpreter, AsyncConsoleInterpreter}
import com.nulabinc.backlog.j2b.utils.ClassVersion
import com.nulabinc.backlog.migration.common.conf.BacklogConfiguration
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.osinka.i18n.Messages
import monix.execution.Scheduler

import scala.concurrent.duration.Duration
import scala.concurrent.Await


object App extends BacklogConfiguration with Logging {

  def main(args: Array[String]): Unit = {

    if (!ClassVersion.isValid()) {
      ConsoleOut.error(Messages("cli.require_java8", System.getProperty("java.specification.version")))
      exit(1)
    }

    val config = ConfigParser.parse(args) match {
      case Some(c) => c.commandType match {
        case Some(Config.ExportCommand) => c
        case Some(Config.ImportCommand) => c
        case None => throw new RuntimeException("No command found")
      }
      case None => sys.exit(1)
    }


    implicit val exc: Scheduler = monix.execution.Scheduler.Implicits.global

    val interpreter = AsyncAppInterpreter(
      consoleInterpreter = AsyncConsoleInterpreter()
    )

    val program = for {
      _ <- AppDSL.fromConsole(ConsoleDSL.print(startMessage(applicationName)))
      latestVersion <- AppDSL.latestRelease()
      _ <- if (latestVersion != versionName) AppDSL.fromConsole(ConsoleDSL.warn(notLatestMessage(latestVersion))) else AppDSL.empty
      _ <- AppDSL.setLanguage(language)
      _ <- config.commandType match {
        case Some(Config.ExportCommand) =>
          AppDSL.export(config.getAppConfiguration, NextCommand.command(args))
        case Some(Config.ImportCommand) =>
          AppDSL.`import`(config.getAppConfiguration)
        case _ =>
          AppDSL.pure(J2BCli.help())
      }
    } yield ()

    val cleanup = interpreter.terminate()

    val f = interpreter
      .run(program)
      .flatMap(_ => cleanup)
      .onErrorHandleWith { ex =>
        cleanup.map { _ =>
          logger.error(ex.getMessage, ex)
          exit(1, ex)
        }
      }
      .runToFuture

    Await.result(f, Duration.Inf)
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

  private def notLatestMessage(latest: String): String =
    s"""
       |--------------------------------------------------
       |${Messages("cli.warn.not.latest", latest, versionName)}
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
