package com.nulabinc.backlog.j2b.cli

import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingFile
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.osinka.i18n.Messages

sealed trait MappingValidateError extends CliError
case object MappingFileMissing    extends MappingValidateError
case object MappingFileBroken     extends MappingValidateError
case object MappingFileNeedFix    extends MappingValidateError

trait MappingValidator extends Logging {

  def validateMapping(
      mappingFile: MappingFile
  ): Either[MappingValidateError, Unit] = {
    if (!mappingFile.isParsed) {
      val error =
        s"""
           |--------------------------------------------------
           |${Messages("cli.mapping.error.broken_file", mappingFile.itemName)}
           |--------------------------------------------------
        """.stripMargin
      ConsoleOut.error(error)
      val message =
        s"""|--------------------------------------------------
            |${Messages(
          "cli.mapping.fix_file",
          mappingFile.filePath
        )}""".stripMargin
      ConsoleOut.println(message)
      Left(MappingFileBroken)
    } else if (!mappingFile.isValid) {
      val error =
        s"""
           |${Messages("cli.mapping.error", mappingFile.itemName)}
           |--------------------------------------------------
           |${mappingFile.errors.mkString("\n")}
           |--------------------------------------------------""".stripMargin
      ConsoleOut.error(error)
      val message =
        s"""
           |--------------------------------------------------
           |${Messages("cli.mapping.fix_file", mappingFile.filePath)}
        """.stripMargin
      ConsoleOut.println(message)
      Left(MappingFileNeedFix)
    } else Right(())
  }
}
