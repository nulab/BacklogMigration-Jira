package com.nulabinc.backlog.j2b.cli

import com.nulabinc.backlog.j2b.NextCommand
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.osinka.i18n.Messages

trait ProgressConsole extends Logging {

  def startExportMessage(): Unit = {
    ConsoleOut.println(s"""
                          |${Messages("export.start")}
                          |--------------------------------------------------""".stripMargin)
  }

  def finishExportMessage(nextCommand: NextCommand): Unit = {
    ConsoleOut.println(s"""--------------------------------------------------
                          |${Messages("export.finish", nextCommand.command())}""".stripMargin)
  }
}