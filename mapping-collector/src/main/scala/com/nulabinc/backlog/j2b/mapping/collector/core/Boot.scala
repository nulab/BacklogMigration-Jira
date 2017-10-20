package com.nulabinc.backlog.j2b.mapping.collector.core

import com.google.inject.Guice
import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.mapping.collector.modules.JiraModule
import com.nulabinc.backlog.j2b.mapping.collector.service.MappingCollector
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.nulabinc.jira.client.domain.User
import com.osinka.i18n.Messages

import scala.collection.mutable

object Boot extends Logging {

  def execute(apiConfig: JiraApiConfiguration): MappingData = {

    val injector = Guice.createInjector(new JiraModule(apiConfig))

    ConsoleOut.println(s"""
                          |${Messages("cli.project_info.start")}
                          |--------------------------------------------------
                          |""".stripMargin)

    val mappingCollector = injector.getInstance(classOf[MappingCollector])
    val mappingData = mappingCollector.boot()

    ConsoleOut.println(s"""|--------------------------------------------------
                           |${Messages("cli.project_info.finish")}
                           |""".stripMargin)

    mappingData
  }
}
