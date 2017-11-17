package com.nulabinc.backlog.j2b.cli

import com.google.inject.Guice
import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.exporter.Exporter
import com.nulabinc.backlog.j2b.jira.converter.MappingConverter
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.j2b.modules._
import com.nulabinc.backlog.migration.common.conf.BacklogConfiguration
import com.nulabinc.backlog.migration.common.modules.ServiceInjector
import com.nulabinc.backlog.migration.common.service.SpaceService
import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.backlog.migration.importer.core.Boot
import com.nulabinc.jira.client.JiraRestClient

object J2BCli extends BacklogConfiguration
    with Logging
    with HelpCommand
    with ConfigValidatable
    with MappingConsole {

  def export(config: AppConfiguration): Unit = {

    val jiraInjector    = Guice.createInjector(new ExportModule(config))
    val backlogInjector = ServiceInjector.createInjector(config.backlogConfig)

    val jiraRestClient = jiraInjector.getInstance(classOf[JiraRestClient])
    val spaceService   = backlogInjector.getInstance(classOf[SpaceService])

    if (validateConfig(config, jiraRestClient, spaceService)) {
      val exporter            = jiraInjector.getInstance(classOf[Exporter])
      val collectData         = exporter.export()
      val mappingFileService  = jiraInjector.getInstance(classOf[MappingFileService])

      List(
        mappingFileService.createUserMappingFile(collectData.users),
        mappingFileService.createPriorityMappingFile(collectData.priorities),
        mappingFileService.createStatusMappingFile(collectData.statuses)
      ).foreach { mappingFile =>
        displayToConsole(mappingFile)
      }
    }
  }

  def `import`(config: AppConfiguration): Unit = {

    val jiraInjector    = Guice.createInjector(new ImportModule(config))
    val backlogInjector = ServiceInjector.createInjector(config.backlogConfig)

    val jiraRestClient = jiraInjector.getInstance(classOf[JiraRestClient])
    val spaceService   = backlogInjector.getInstance(classOf[SpaceService])

    if (validateConfig(config, jiraRestClient, spaceService)) {

      // Convert
      val mappingFileService = jiraInjector.getInstance(classOf[MappingFileService])
      val converter = jiraInjector.getInstance(classOf[MappingConverter])

      converter.convert(
        userMaps      = mappingFileService.userMappingsFromFile(),
        priorityMaps  = mappingFileService.priorityMappingsFromFile(),
        statusMaps    = mappingFileService.statusMappingsFromFile()
      )

      // Import
      Boot.execute(config.backlogConfig, false)
    }
  }

  def doImport(config: AppConfiguration): Unit = {

//    val injector = Guice.createInjector(new ImportModule(config))
//
//    if (validateConfig(config, injector)) {
//
//    }
  }


}
