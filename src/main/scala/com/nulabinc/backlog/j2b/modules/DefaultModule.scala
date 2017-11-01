package com.nulabinc.backlog.j2b.modules

import com.google.inject.AbstractModule
import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.exporter.service._
import com.nulabinc.backlog.j2b.issue.writer._
import com.nulabinc.backlog.j2b.issue.writer.convert._
import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.jira.converter.MappingConverter
import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.j2b.jira.writer._
import com.nulabinc.backlog.j2b.mapping.file.MappingFileServiceImpl
import com.nulabinc.backlog.j2b.mapping.converter.MappingConvertService
import com.nulabinc.backlog.migration.common.conf.{BacklogApiConfiguration, BacklogPaths}
import com.nulabinc.jira.client.JiraRestClient

class DefaultModule(config: AppConfiguration) extends AbstractModule {

  val jira = JiraRestClient(
    config.jiraUrl,
    config.jiraUsername,
    config.jiraPassword
  )

  override def configure() = {

//    val project = jira.projectRestClient.project(config.jiraKey).right.get

    bind(classOf[JiraRestClient]).toInstance(jira)
//    bind(classOf[Project]).toInstance(project)
    bind(classOf[JiraApiConfiguration]).toInstance(config.jiraConfig)
    bind(classOf[JiraProjectKey]).toInstance(JiraProjectKey(config.jiraConfig.projectKey))
    bind(classOf[BacklogApiConfiguration]).toInstance(config.backlogConfig)
    bind(classOf[BacklogPaths]).toInstance(new BacklogPaths(config.backlogProjectKey))

    // Mapping-file
    bind(classOf[MappingFileService]).to(classOf[MappingFileServiceImpl])

    // Mapping-converter
    bind(classOf[MappingConverter]).to(classOf[MappingConvertService])
  }
}
