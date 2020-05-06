package com.nulabinc.backlog.j2b

import java.util.Locale

import com.nulabinc.backlog.j2b.cli.J2BCli
import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.core.{ConfigParser, GithubRelease, NextCommand}
import com.nulabinc.backlog.j2b.utils.ClassVersion
import com.nulabinc.backlog.migration.common.conf.BacklogConfiguration
import com.nulabinc.backlog.migration.common.dsl.{
  AppDSL,
  ConsoleDSL,
  StorageDSL
}
import com.nulabinc.backlog.migration.common.errors.{
  MappingFileNotFound,
  MappingValidationError
}
import com.nulabinc.backlog.migration.common.interpreters.{
  JansiConsoleDSL,
  LocalStorageDSL,
  TaskAppDSL
}
import com.nulabinc.backlog.migration.common.messages.ConsoleMessages
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.osinka.i18n.Messages
import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object App extends BacklogConfiguration with Logging {

  private implicit val appDSL: AppDSL[Task] = TaskAppDSL()
  private implicit val storageDSL: StorageDSL[Task] = LocalStorageDSL()
  private implicit val consoleDSL: ConsoleDSL[Task] = JansiConsoleDSL()
  private implicit val exc: Scheduler =
    monix.execution.Scheduler.Implicits.global

  def main(args: Array[String]): Unit = {

    if (!ClassVersion.isValid()) {
      ConsoleOut.error(
        Messages(
          "cli.require_java8",
          System.getProperty("java.specification.version")
        )
      )
      exit(1)
    }

    val config = ConfigParser.parse(args) match {
      case Some(c) =>
        c.commandType match {
          case Some(Config.ExportCommand) => c
          case Some(Config.ImportCommand) => c
          case None                       => throw new RuntimeException("No command found")
        }
      case None => sys.exit(1)
    }

    val program = for {
      _ <- consoleDSL.println(startMessage(applicationName))
      latestVersion <- Task(GithubRelease.checkRelease())
      _ <-
        if (latestVersion != versionName)
          consoleDSL.warnln(notLatestMessage(latestVersion))
        else Task(())
      _ = language match {
        case "ja" => Locale.setDefault(Locale.JAPAN)
        case "en" => Locale.setDefault(Locale.US)
        case _    => Locale.setDefault(Locale.getDefault)
      }
      _ <- consoleDSL.println(configurationMessage(config.getAppConfiguration))
      result <- config.commandType match {
        case Some(Config.ExportCommand) =>
          J2BCli.`export`(config.getAppConfiguration, NextCommand.command(args))
        case Some(Config.ImportCommand) =>
          J2BCli.`import`(config.getAppConfiguration)
        case _ =>
          Task(Right(J2BCli.help()))
      }
      _ <- result match {
        case Right(_) =>
          appDSL.pure(())
        case Left(_: ParameterError) =>
          appDSL.pure(()) // TODO: refactor AppConfigValidator
        case Left(error: MappingError) =>
          error.inner match {
            case _: MappingFileNotFound =>
              consoleDSL.errorln(ConsoleMessages.Mappings.needsSetup)
            case e: MappingValidationError[_] =>
              consoleDSL.errorln(ConsoleMessages.Mappings.validationError(e))
            case e =>
              consoleDSL.errorln(e.toString)
          }
        case Left(ConfirmCanceled) =>
          consoleDSL.errorln(ConsoleMessages.confirmCanceled)
      }
    } yield ()

    val f = program.onErrorRecover { ex =>
      logger.error(ex.getMessage, ex)
      exit(1, ex)
    }.runToFuture

    Await.result(f, Duration.Inf)
  }

  private def exit(exitCode: Int): Unit = {
    System.exit(exitCode)
  }

  private def exit(exitCode: Int, error: Throwable): Unit = {
    ConsoleOut.error(
      "ERROR: " + error.getMessage + "\n" + error.printStackTrace()
    )
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
       |${Messages("common.src")} ${Messages("common.username")}[${conf.jiraUsername}]
       |${Messages("common.src")} ${Messages("common.url")}[${conf.jiraUrl}]
       |${Messages("common.src")} ${Messages("common.project_key")}[${conf.jiraKey}]
       |${Messages("common.dst")} ${Messages("common.url")}[${conf.backlogUrl}]
       |${Messages("common.dst")} ${Messages("common.access_key")}[${conf.backlogKey}]
       |${Messages("common.dst")} ${Messages("common.project_key")}[${conf.backlogKey}]
       |${Messages("common.retryCount")} [${conf.retryCount}]
       |https.proxyHost[${Option(System.getProperty("https.proxyHost"))
      .getOrElse("")}]
       |https.proxyPort[${Option(System.getProperty("https.proxyPort"))
      .getOrElse("")}]
       |https.proxyUser[${Option(System.getProperty("https.proxyUser"))
      .getOrElse("")}]
       |https.proxyPassword[${Option(System.getProperty("https.proxyPassword"))
      .getOrElse("")}]
       |--------------------------------------------------
       |""".stripMargin
}
