package com.nulabinc.backlog.j2b.cli

import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.osinka.i18n.Messages

trait HelpCommand extends Logging {

  def help(): Unit = {
    val message =
      s"""
         |${Messages("cli.help.sample_command")}
         |${Messages("cli.help")}
      """.stripMargin
    ConsoleOut.println(message)
  }
}
