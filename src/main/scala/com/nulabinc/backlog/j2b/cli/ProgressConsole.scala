package com.nulabinc.backlog.j2b.cli

import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.osinka.i18n.Messages

trait ProgressConsole extends Logging {

  def finishExportMessage(nextCommandStr: String): Unit = {
    ConsoleOut.println(s"""--------------------------------------------------
                          |${Messages("export.finish", nextCommandStr)}""".stripMargin)
  }
}
