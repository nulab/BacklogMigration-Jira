package com.nulabinc.backlog.j2b.cli

import com.google.inject.Guice
import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.exporter.Exporter
import com.nulabinc.backlog.j2b.jira.converter.MappingConverter
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.j2b.mapping.file._
import com.nulabinc.backlog.j2b.modules._
import com.nulabinc.backlog.migration.common.conf.{BacklogConfiguration, BacklogPaths}
import com.nulabinc.backlog.migration.common.domain.BacklogUser
import com.nulabinc.backlog.migration.common.modules.ServiceInjector
import com.nulabinc.backlog.migration.common.service.{ProjectService, SpaceService}
import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.backlog.migration.importer.core.Boot
import com.nulabinc.jira.client.JiraRestClient

object J2BCli extends BacklogConfiguration
    with Logging
    with HelpCommand
    with ConfigValidator
    with MappingValidator
    with MappingConsole
    with InteractiveConfirm {

  def export(config: AppConfiguration): Unit = {

    val jiraInjector    = Guice.createInjector(new ExportModule(config))
    val backlogInjector = ServiceInjector.createInjector(config.backlogConfig)

    val jiraRestClient = jiraInjector.getInstance(classOf[JiraRestClient])
    val spaceService   = backlogInjector.getInstance(classOf[SpaceService])

    if (validateConfig(config, jiraRestClient, spaceService)) {

      // Delete old exports
      val backlogPaths = backlogInjector.getInstance(classOf[BacklogPaths])
      backlogPaths.outputPath.deleteRecursively(force = true, continueOnFailure = true)

      // Export
      val exporter            = jiraInjector.getInstance(classOf[Exporter])
      val collectData         = exporter.export()
      val mappingFileService  = jiraInjector.getInstance(classOf[MappingFileService])

      List(
        mappingFileService.createUserMappingFile(collectData.users),
        mappingFileService.createPriorityMappingFile(collectData.priorities),
        mappingFileService.createStatusMappingFile(collectData.statuses)
      ).foreach { mappingFile =>
        if (mappingFile.isExists) {
          displayMergedMappingFileMessageToConsole(mappingFile)
        } else {
          mappingFile.create()
          displayCreateMappingFileMessageToConsole(mappingFile)
        }
      }
    }
  }

  def `import`(config: AppConfiguration): Unit = {

    val jiraInjector    = Guice.createInjector(new ImportModule(config))
    val backlogInjector = ServiceInjector.createInjector(config.backlogConfig)

    val jiraRestClient = jiraInjector.getInstance(classOf[JiraRestClient])
    val spaceService   = backlogInjector.getInstance(classOf[SpaceService])

    if (validateConfig(config, jiraRestClient, spaceService)) {

      import com.nulabinc.backlog4j.{Status => BacklogStatus, Priority => BacklogPriority}
      import com.nulabinc.jira.client.domain.{Status => JiraStatus, Priority => JiraPriority, User => JiraUser}

      val statusMappingFile   = new StatusMappingFile(Seq.empty[JiraStatus], Seq.empty[BacklogStatus])
      val priorityMappingFile = new PriorityMappingFile(Seq.empty[JiraPriority], Seq.empty[BacklogPriority])
      val userMappingFile     = new UserMappingFile(config.backlogConfig, Seq.empty[JiraUser], Seq.empty[BacklogUser])

      for {
        _           <- mappingFileExists(statusMappingFile).right
        _           <- mappingFileExists(priorityMappingFile).right
        _           <- mappingFileExists(userMappingFile).right
        projectKeys <- confirmProject(config, backlogInjector.getInstance(classOf[ProjectService])).right
        _           <- finalConfirm(projectKeys, statusMappingFile, priorityMappingFile, userMappingFile).right
      } yield ()

      // Convert
      val converter = jiraInjector.getInstance(classOf[MappingConverter])

      converter.convert(
        userMaps      = userMappingFile.tryUnMarshal(),
        priorityMaps  = priorityMappingFile.tryUnMarshal(),
        statusMaps    = statusMappingFile.tryUnMarshal()
      )

      // Import
      Boot.execute(config.backlogConfig, false)
    }
  }

}
