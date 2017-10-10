package com.nulabinc.backlog.j2b.cli

import com.nulabinc.backlog.j2b.conf.{AppConfigValidator, AppConfiguration}
import com.nulabinc.backlog.migration.common.conf.BacklogConfiguration
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.osinka.i18n.Messages

object J2BCli extends BacklogConfiguration with Logging {

  def init(config: AppConfiguration): Unit = {
    if (validateConfig(config)) {

    }
  }

  def migrate(config: AppConfiguration): Unit = {
    if (validateConfig(config)) {

    }
  }

  def doImport(config: AppConfiguration): Unit = {
    if (validateConfig(config)) {

    }
  }

  def help(): Unit = {
    val message =
      s"""
         |${Messages("cli.help.sample_command")}
         |${Messages("cli.help")}
      """.stripMargin
    ConsoleOut.println(message)
  }

  private[this] def validateConfig(config: AppConfiguration): Boolean = {
    val validator = new AppConfigValidator()
    val errors = validator.validate(config)
    if (errors.isEmpty) true
    else {
      val message =
        s"""
           |
           |${Messages("cli.param.error")}
           |--------------------------------------------------
           |${errors.mkString("\n")}
           |
        """.stripMargin
      ConsoleOut.error(message)
      false
    }
  }
}
