package com.nulabinc.backlog.j2b.modules

import com.google.inject.AbstractModule
import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.issue.writer.convert._
import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingCollectDatabase
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.j2b.mapping.collector.MappingCollectDatabaseInMemory
import com.nulabinc.backlog.j2b.mapping.file.MappingFileServiceImpl
import com.nulabinc.backlog.migration.common.conf.{BacklogApiConfiguration, BacklogPaths}
import com.nulabinc.backlog.migration.common.domain.BacklogProjectKey
import com.nulabinc.jira.client.JiraRestClient
import com.nulabinc.jira.client.domain.field.Field

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
    bind(classOf[BacklogProjectKey]).toInstance(BacklogProjectKey(config.backlogConfig.projectKey))
    bind(classOf[BacklogApiConfiguration]).toInstance(config.backlogConfig)

    // Paths
    bind(classOf[BacklogPaths]).toInstance(new BacklogPaths(config.backlogProjectKey))

    // Mapping-file
    bind(classOf[MappingFileService]).to(classOf[MappingFileServiceImpl])

    // Data
    val fields = jira.fieldAPI.all().right.get

    // Pre fetched data
    bind(classOf[Seq[Field]]).toInstance(fields)

    // Writes
    bind(classOf[UserWrites]).toInstance(new UserWrites)
    bind(classOf[IssueFieldWrites]).toInstance(new IssueFieldWrites(fields))
    bind(classOf[ChangelogItemWrites]).toInstance(new ChangelogItemWrites(fields))
    bind(classOf[AttachmentWrites]).toInstance(new AttachmentWrites)

    // Collector
    bind(classOf[MappingCollectDatabase]).to(classOf[MappingCollectDatabaseInMemory])
  }
}
