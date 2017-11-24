package com.nulabinc.backlog.j2b.cli

import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingFile
import com.nulabinc.backlog.migration.common.service.ProjectService
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.osinka.i18n.Messages

sealed trait ConfirmError extends CliError
case object ConfirmCanceled extends ConfirmError

case class ConfirmedProjectKeys(jiraKey: String, backlogKey: String)

trait InteractiveConfirm extends Logging {

  def confirmProject(config: AppConfiguration, projectService: ProjectService): Either[ConfirmError, ConfirmedProjectKeys] =
    projectService.optProject(config.backlogConfig.projectKey) match {
      case Some(_) =>
        val input: String = scala.io.StdIn.readLine(Messages("cli.backlog_project_already_exist", config.backlogConfig.projectKey))
        if (input == "y" || input == "Y") Right(ConfirmedProjectKeys(config.jiraConfig.projectKey, config.backlogConfig.projectKey))
        else Left(ConfirmCanceled)
      case None =>
        Right(ConfirmedProjectKeys(config.jiraConfig.projectKey, config.backlogConfig.projectKey))
    }

  def finalConfirm(confirmedProjectKeys: ConfirmedProjectKeys,
                   statusMappingFile: MappingFile,
                   priorityMappingFile: MappingFile,
                   userMappingFile: MappingFile): Either[ConfirmError, Unit] = {
    def mappingString(mappingFile: MappingFile): String = {
      mappingFile.unMarshal() match {
        case Some(mappings) =>
          mappings
            .map(mapping =>
              s"- ${mappingFile.display(mapping.src, mappingFile.jiras)} => ${mappingFile.display(mapping.dst, mappingFile.backlogs)}")
            .mkString("\n")
        case _ => throw new RuntimeException
      }
    }

    ConsoleOut.println(s"""
                          |${Messages("cli.mapping.show", Messages("common.projects"))}
                          |--------------------------------------------------
                          |- ${confirmedProjectKeys.jiraKey} => ${confirmedProjectKeys.backlogKey}
                          |--------------------------------------------------
                          |
         |${Messages("cli.mapping.show", userMappingFile.itemName)}
                          |--------------------------------------------------
                          |${mappingString(userMappingFile)}
                          |--------------------------------------------------
                          |
         |${Messages("cli.mapping.show", priorityMappingFile.itemName)}
                          |--------------------------------------------------
                          |${mappingString(priorityMappingFile)}
                          |--------------------------------------------------
                          |
         |${Messages("cli.mapping.show", statusMappingFile.itemName)}
                          |--------------------------------------------------
                          |${mappingString(statusMappingFile)}
                          |--------------------------------------------------
                          |""".stripMargin)
    val input: String = scala.io.StdIn.readLine(Messages("cli.confirm"))
    if (input == "y" || input == "Y") Right(())
    else {
      ConsoleOut.println(s"""
                            |--------------------------------------------------
                            |${Messages("cli.cancel")}""".stripMargin)
      Left(ConfirmCanceled)
    }
  }

}
