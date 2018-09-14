package com.nulabinc.backlog.j2b

import com.nulabinc.backlog.j2b.cli.J2BCli
import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.dsl.{AppDSL, ConsoleDSL}
import com.nulabinc.backlog.j2b.interpreters.{AsyncAppInterpreter, AsyncConsoleInterpreter}
import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.utils.ClassVersion
import com.nulabinc.backlog.migration.common.conf.{BacklogApiConfiguration, BacklogConfiguration}
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.osinka.i18n.Messages
import org.fusesource.jansi.AnsiConsole
import org.rogach.scallop.{ScallopConf, Subcommand}
import monix.execution.Scheduler

import scala.util.{Failure, Success, Try}

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

class NextCommand(args: Seq[String]) extends BacklogConfiguration {

  private val formattedArgs = args
    .filterNot(_ == "export")
    .grouped(2)
    .collect {
      case Seq(k, _) if k.contains("password") => language match {
        case "ja" => s"    $k JIRAのパスワード"
        case "en" => s"    $k JIRA_PASSWORD"
        case _    => s"    $k JIRA_PASSWORD"
      }
      case Seq(k, v) => s"    $k $v"
    }.toSeq

  def command(): String = (
      Seq(
        "java -jar",
        s"  ${Config.Application.fileName}",
        "  import"
     ) ++ formattedArgs
    ).mkString(" \\ \n")
}

object J2B extends BacklogConfiguration with Logging {

  def main(args: Array[String]): Unit = {

    implicit val exc: Scheduler = monix.execution.Scheduler.Implicits.global

    val interpreter = AsyncAppInterpreter(
      consoleInterpreter = AsyncConsoleInterpreter()
    )

    val program = for {
      _ <- AppDSL.fromConsole(ConsoleDSL.print(startMessage(applicationName)))
      _ <- AppDSL.pure(AnsiConsole.systemInstall()) // Console initialization
      _ <- AppDSL.setLanguage(language)
      _ <- AppDSL.pure(checkRelease()) // TODO:
    } yield ()

    // DisableSSLCertificateCheckUtil.disableChecks() // TODO: ???

    if ( ! ClassVersion.isValid()) {
      ConsoleOut.error(Messages("cli.require_java8", System.getProperty("java.specification.version")))
      exit(1)
    }

    interpreter
      .run(program)
      .foreach { _ =>
        // Run
        try {
          val cli         = new CommandLineInterface(args)
          val nextCommand = new NextCommand(args)

          getConfiguration(cli) match {
            case Success(config) =>
              cli.subcommand match {
                case Some(cli.importCommand) => J2BCli.`import`(config)
                case Some(cli.exportCommand) => J2BCli.export(config, nextCommand)
                case _                       => J2BCli.help()
              }
              exit(0)
            case Failure(failure) =>
              ConsoleOut.error(s"${Messages("cli.error.args")}")
              logger.error(failure.getMessage)
              J2BCli.help()
              exit(1)
          }
        } catch {
          case e: Throwable =>
            logger.error(e.getMessage, e)
            ConsoleOut.error(s"${Messages("cli.error.unknown")}:${e.getMessage}")
            exit(1)
        }
      }

  }

  private[this] def getConfiguration(cli: CommandLineInterface) = Try {
    val keys: Array[String] = cli.importCommand.projectKey().split(":")
    val jira: String        = keys(0)
    val backlog: String     = if (keys.length == 2) keys(1) else keys(0).toUpperCase.replaceAll("-", "_")

    ConsoleOut.println(
      s"""--------------------------------------------------
         |${Messages("common.jira")} ${Messages("common.username")}[${cli.importCommand.jiraUsername()}]
         |${Messages("common.jira")} ${Messages("common.url")}[${cli.importCommand.jiraUrl()}]
         |${Messages("common.jira")} ${Messages("common.project_key")}[${jira}]
         |${Messages("common.backlog")} ${Messages("common.url")}[${cli.importCommand.backlogUrl()}]
         |${Messages("common.backlog")} ${Messages("common.access_key")}[${cli.importCommand.backlogKey()}]
         |${Messages("common.backlog")} ${Messages("common.project_key")}[${backlog}]
         |https.proxyHost[${Option(System.getProperty("https.proxyHost")).getOrElse("")}]
         |https.proxyPort[${Option(System.getProperty("https.proxyPort")).getOrElse("")}]
         |https.proxyUser[${Option(System.getProperty("https.proxyUser")).getOrElse("")}]
         |https.proxyPassword[${Option(System.getProperty("https.proxyPassword")).getOrElse("")}]
         |--------------------------------------------------
     |""".stripMargin)

    new AppConfiguration(
      jiraConfig    = JiraApiConfiguration(username = cli.importCommand.jiraUsername(), password = cli.importCommand.jiraPassword(), cli.importCommand.jiraUrl(), projectKey = jira),
      backlogConfig = BacklogApiConfiguration(url = cli.importCommand.backlogUrl(), key = cli.importCommand.backlogKey(), projectKey = backlog)
    )
  }

  private[this] def exit(exitCode: Int): Unit = {
    AnsiConsole.systemUninstall()
    System.exit(exitCode)
  }

  private[this] def checkRelease(): Unit = {
    import java.io._
    import java.net._
    import spray.json._
    import spray.json.DefaultJsonProtocol._

    val url = new URL("https://api.github.com/repos/nulab/BacklogMigration-Jira/releases")
    val http = url.openConnection().asInstanceOf[HttpURLConnection]
    val optProxyUser = Option(System.getProperty("https.proxyUser"))
    val optProxyPass = Option(System.getProperty("https.proxyPassword"))

    (optProxyUser, optProxyPass) match {
      case (Some(proxyUser), Some(proxyPass)) =>
        Authenticator.setDefault(new Authenticator() {
          override def getPasswordAuthentication: PasswordAuthentication = {
            new PasswordAuthentication(proxyUser, proxyPass.toCharArray)
          }
        })
      case _ => ()
    }

    try {
      http.setRequestMethod("GET")
      http.connect()

      val reader = new BufferedReader(new InputStreamReader(http.getInputStream))
      val output = new StringBuilder()
      var line = ""

      while (line != null) {
        line = reader.readLine()
        if (line != null)
          output.append(line)
      }
      reader.close()

      val latest = output.toString().parseJson match {
        case JsArray(releases) if releases.nonEmpty =>
          releases(0).asJsObject.fields.apply("tag_name").convertTo[String].replace("v", "")
        case _ => ""
      }

      if (latest != versionName) {
        ConsoleOut.warning(
          s"""
             |--------------------------------------------------
             |${Messages("cli.warn.not.latest", latest, versionName)}
             |--------------------------------------------------
        """.stripMargin)
      }
    } catch {
      case ex: Throwable =>
        logger.error(ex.getMessage, ex)
    }
  }

  private def startMessage(appName: String): String =
    s"""|$appName
        |--------------------------------------------------
       """.stripMargin
}
