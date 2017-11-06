package com.nulabinc.backlog.j2b.cli

import com.google.inject.{Guice, Injector}
import com.nulabinc.backlog.j2b.conf.{AppConfigValidator, AppConfiguration}
import com.nulabinc.backlog.j2b.exporter.Exporter
import com.nulabinc.backlog.j2b.jira.converter.MappingConverter
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.j2b.mapping.file._
import com.nulabinc.backlog.j2b.modules._
import com.nulabinc.backlog.migration.common.conf.BacklogConfiguration
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.nulabinc.backlog.migration.importer.core.Boot
import com.nulabinc.jira.client.domain.{Priority, Status, User}
import com.osinka.i18n.Messages

object J2BCli extends BacklogConfiguration
    with Logging
    with HelpCommand {

  def export(config: AppConfiguration): Unit = {

    val injector = Guice.createInjector(new ExportModule(config))

    if (validateConfig(config, injector)) {
      val exporter = injector.getInstance(classOf[Exporter])

      val collectData = exporter.export()

      val mappingFileService = injector.getInstance(classOf[MappingFileService])

      mappingFileService.outputUserMappingFile(collectData.users)
      mappingFileService.outputPriorityMappingFile(collectData.priorities)
      mappingFileService.outputStatusMappingFile(collectData.statuses)
    }
  }

  def `import`(config: AppConfiguration): Unit = {

    val injector = Guice.createInjector(new ImportModule(config))

    if (validateConfig(config, injector)) {


      // Convert
      val userMappingFile     = new UserMappingFile(config.jiraConfig, config.backlogConfig, Seq.empty[User])
      val priorityMappingFile = new PriorityMappingFile(config.jiraConfig, config.backlogConfig, Seq.empty[Priority])
      val statusMappingFile   = new StatusMappingFile(config.jiraConfig, config.backlogConfig, Seq.empty[Status])

      val converter = injector.getInstance(classOf[MappingConverter])
      converter.convert(
        userMaps = userMappingFile.tryUnmarshal(),
        priorityMaps = priorityMappingFile.tryUnmarshal(),
        statusMaps = statusMappingFile.tryUnmarshal()
      )

      // Import
      Boot.execute(config.backlogConfig, false)
    }
  }

  def doImport(config: AppConfiguration): Unit = {

    val injector = Guice.createInjector(new ImportModule(config))

    if (validateConfig(config, injector)) {

    }
  }

  private def validateConfig(config: AppConfiguration, injector: Injector): Boolean = {
    val validator = injector.getInstance(classOf[AppConfigValidator])
    val errors = validator.validate(config)
    if (errors.isEmpty) true
    else {
      val message =
        s"""
           |
           |${Messages("cli.param.error")}
           |--------------------------------------------------
           |${errors.mkString("\n")}
           |
        """.stripMargin
      ConsoleOut.error(message)
      false
    }
  }
}
