package com.nulabinc.backlog.j2b.cli

import com.nulabinc.backlog.j2b.jira.domain.{Mapping, MappingFile}
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.osinka.i18n.Messages

trait MappingConsole extends Logging {

  def displayToConsole(mappingFile: MappingFile): Unit = {
    if (mappingFile.isExists) mappingFileExists(mappingFile)
    else                      createMappingFile(mappingFile)
  }

  private def mappingFileExists(mappingFile: MappingFile): Unit = {
    val addItems = mappingFile.merge()
    val message = if (addItems.nonEmpty) {
      def displayItem(value: String) = {
        if (value.isEmpty) Messages("common.empty") else value
      }
      def display(mapping: Mapping) = {
        s"- ${mapping.src} => ${displayItem(mapping.dst)}"
      }
      val mappingString = addItems.map(display).mkString("\n")
      s"""
         |--------------------------------------------------
         |${Messages("cli.mapping.merge_file", mappingFile.itemName, mappingFile.filePath)}
         |[${mappingFile.filePath}]
         |${mappingString}
         |--------------------------------------------------""".stripMargin
    } else {
      s"""
         |--------------------------------------------------
         |${Messages("cli.mapping.no_change", mappingFile.itemName)}
         |--------------------------------------------------""".stripMargin
    }
    ConsoleOut.println(message)
  }

  private def createMappingFile(mappingFile: MappingFile): Unit = {
    mappingFile.create()
    val message =
      s"""
         |--------------------------------------------------
         |${Messages("cli.mapping.output_file", mappingFile.itemName)}
         |[${mappingFile.filePath}]
         |--------------------------------------------------""".stripMargin
    ConsoleOut.println(message)
  }

}
