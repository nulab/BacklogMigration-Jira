package com.nulabinc.backlog.j2b.cli

import com.google.inject.Guice
import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.exporter.Exporter
import com.nulabinc.backlog.j2b.jira.conf.JiraBacklogPaths
import com.nulabinc.backlog.j2b.jira.converter.MappingConverter
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.j2b.modules._
import com.nulabinc.backlog.migration.common.conf.BacklogConfiguration
import com.nulabinc.backlog.migration.common.modules.{ServiceInjector => BacklogInjector}
import com.nulabinc.backlog.migration.common.service.{ProjectService, SpaceService, PriorityService => BacklogPriorityService, StatusService => BacklogStatusService, UserService => BacklogUserService}
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.nulabinc.backlog.migration.importer.core.Boot
import com.nulabinc.jira.client.JiraRestClient
import com.osinka.i18n.Messages

object J2BCli extends BacklogConfiguration
    with Logging
    with HelpCommand
    with ConfigValidator
    with MappingValidator
    with MappingConsole
    with ProgressConsole
    with InteractiveConfirm
    with Tracker {

  def export(config: AppConfiguration): Unit = {

    startExportMessage()

    val jiraInjector    = Guice.createInjector(new ExportModule(config))
    val jiraRestClient  = jiraInjector.getInstance(classOf[JiraRestClient])

    val backlogInjector         = BacklogInjector.createInjector(config.backlogConfig)
    val backlogSpaceService     = backlogInjector.getInstance(classOf[SpaceService])
    val backlogUserService      = backlogInjector.getInstance(classOf[BacklogUserService])
    val backlogPriorityService  = backlogInjector.getInstance(classOf[BacklogPriorityService])
    val backlogStatusService    = backlogInjector.getInstance(classOf[BacklogStatusService])

    if (validateConfig(config, jiraRestClient, backlogSpaceService)) {

      // Delete old exports
      val jiraBacklogPaths = new JiraBacklogPaths(config.jiraConfig.projectKey, config.backlogConfig.projectKey)

      jiraBacklogPaths.outputPath.deleteRecursively(force = true, continueOnFailure = true)

      // Export
      val exporter     = jiraInjector.getInstance(classOf[Exporter])
      val collectData  = exporter.export(jiraBacklogPaths)

      // Mapping file
      val mappingFileService  = jiraInjector.getInstance(classOf[MappingFileService])

      List(
        mappingFileService.createUserMappingFile(collectData.users, backlogUserService.allUsers()),
        mappingFileService.createPriorityMappingFile(collectData.priorities, backlogPriorityService.allPriorities()),
        mappingFileService.createStatusMappingFile(collectData.statuses, backlogStatusService.allStatuses())
      ).foreach { mappingFile =>
        if (mappingFile.isExists) {
          displayMergedMappingFileMessageToConsole(mappingFile)
        } else {
          mappingFile.create()
          displayCreateMappingFileMessageToConsole(mappingFile)
        }
      }

      finishExportMessage()
    }
  }

  def `import`(config: AppConfiguration): Unit = {

    val jiraInjector    = Guice.createInjector(new ImportModule(config))
    val backlogInjector = BacklogInjector.createInjector(config.backlogConfig)

    val jiraRestClient          = jiraInjector.getInstance(classOf[JiraRestClient])
    val spaceService            = backlogInjector.getInstance(classOf[SpaceService])
    val backlogUserService      = backlogInjector.getInstance(classOf[BacklogUserService])
    val backlogPriorityService  = backlogInjector.getInstance(classOf[BacklogPriorityService])
    val backlogStatusService    = backlogInjector.getInstance(classOf[BacklogStatusService])

    if (validateConfig(config, jiraRestClient, spaceService)) {

      // Mapping file
      val jiraBacklogPaths    = new JiraBacklogPaths(config.jiraConfig.projectKey, config.backlogConfig.projectKey)
      val mappingFileService  = jiraInjector.getInstance(classOf[MappingFileService])
      val statusMappingFile   = mappingFileService.createStatusesMappingFileFromJson(jiraBacklogPaths.jiraStatusesJson, backlogStatusService.allStatuses())
      val priorityMappingFile = mappingFileService.createPrioritiesMappingFileFromJson(jiraBacklogPaths.jiraPrioritiesJson, backlogPriorityService.allPriorities())
      val userMappingFile     = mappingFileService.createUserMappingFileFromJson(jiraBacklogPaths.jiraUsersJson, backlogUserService.allUsers())

      for {
        _           <- mappingFileExists(statusMappingFile).right
        _           <- mappingFileExists(priorityMappingFile).right
        _           <- mappingFileExists(userMappingFile).right
        _           <- validateMapping(statusMappingFile).right
        _           <- validateMapping(priorityMappingFile).right
        _           <- validateMapping(userMappingFile).right
        projectKeys <- confirmProject(config, backlogInjector.getInstance(classOf[ProjectService])).right
        _           <- finalConfirm(projectKeys, statusMappingFile, priorityMappingFile, userMappingFile).right
      } yield {
        // Convert
        val converter = jiraInjector.getInstance(classOf[MappingConverter])

        converter.convert(
          userMaps      = userMappingFile.tryUnMarshal(),
          priorityMaps  = priorityMappingFile.tryUnMarshal(),
          statusMaps    = statusMappingFile.tryUnMarshal()
        )

        // Import
        Boot.execute(config.backlogConfig, false)

        // Tracking
        if (!config.isOptOut) {
          val userService = backlogInjector.getInstance(classOf[BacklogUserService])
          tracking(config, spaceService, userService)
        }
      }
    }
  }

}
