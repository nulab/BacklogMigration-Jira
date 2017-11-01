package com.nulabinc.backlog.j2b.cli

import com.google.inject.Guice
import com.nulabinc.backlog.j2b.conf.{AppConfigValidator, AppConfiguration}
import com.nulabinc.backlog.j2b.exporter.Exporter
import com.nulabinc.backlog.j2b.jira.converter.MappingConverter
import com.nulabinc.backlog.j2b.jira.domain.Mapping
import com.nulabinc.backlog.j2b.jira.service.{MappingFileService, PriorityService}
import com.nulabinc.backlog.j2b.modules.{DefaultModule, ExportModule}
import com.nulabinc.backlog.migration.common.conf.BacklogConfiguration
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.nulabinc.backlog.migration.importer.core.Boot
import com.nulabinc.jira.client.JiraRestClient
import com.nulabinc.jira.client.domain.field.Field
import com.osinka.i18n.Messages

object J2BCli extends BacklogConfiguration
    with Logging
    with HelpCommand {

  def export(config: AppConfiguration): Unit = {

    val injector = Guice.createInjector(new ExportModule(config))

    if (validateConfig(config)) {
      val exporter = injector.getInstance(classOf[Exporter])

      val collectData = exporter.export()

      val mappingFileService = injector.getInstance(classOf[MappingFileService])

      mappingFileService.outputUserMappingFile(collectData.users)
      mappingFileService.outputPriorityMappingFile(collectData.priorities)
    }
  }

  def `import`(config: AppConfiguration): Unit = {
    if (validateConfig(config)) {

      val injector = Guice.createInjector(new DefaultModule(config))

      // Convert
      val converter = injector.getInstance(classOf[MappingConverter])
      converter.convert()

      // Import
      Boot.execute(config.backlogConfig, false)
    }
  }

  def doImport(config: AppConfiguration): Unit = {
    if (validateConfig(config)) {

    }
  }

  private[this] def validateConfig(config: AppConfiguration): Boolean = {
    val validator = new AppConfigValidator()
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
