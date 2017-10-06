package com.nulabinc.backlog.j2b.cli

import com.nulabinc.backlog.migration.common.conf.BacklogConfiguration
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.osinka.i18n.Messages

object J2BCli extends BacklogConfiguration with Logging {

  def init(): Unit = {

  }

  def help(): Unit = {
    val message =
      s"""
         |${Messages("cli.help.sample_command")}
         |${Messages("cli.help")}
      """.stripMargin
    ConsoleOut.println(message)
  }
}
