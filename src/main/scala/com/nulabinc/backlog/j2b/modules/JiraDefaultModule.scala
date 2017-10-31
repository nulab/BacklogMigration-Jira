package com.nulabinc.backlog.j2b.modules

import com.google.inject.AbstractModule
import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.exporter.service._
import com.nulabinc.backlog.j2b.issue.writer._
import com.nulabinc.backlog.j2b.issue.writer.convert._
import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.j2b.jira.writer._
import com.nulabinc.backlog.j2b.mapping.file.MappingFileServiceImpl
import com.nulabinc.backlog.migration.common.conf.{BacklogApiConfiguration, BacklogPaths}
import com.nulabinc.jira.client.JiraRestClient

class JiraDefaultModule(config: AppConfiguration) extends AbstractModule {

  override def configure() = {

    val jira = JiraRestClient(
      config.jiraUrl,
      config.jiraUsername,
      config.jiraPassword
    )
//    val project = jira.projectRestClient.project(config.jiraKey).right.get

    bind(classOf[JiraRestClient]).toInstance(jira)
//    bind(classOf[Project]).toInstance(project)
    bind(classOf[JiraApiConfiguration]).toInstance(config.jiraConfig)
    bind(classOf[JiraProjectKey]).toInstance(JiraProjectKey(config.jiraConfig.projectKey))
    bind(classOf[BacklogApiConfiguration]).toInstance(config.backlogConfig)
    bind(classOf[BacklogPaths]).toInstance(new BacklogPaths(config.backlogProjectKey))

    // Data
    val fields = jira.fieldAPI.all().right.get

    // Writes
    bind(classOf[UserWrites]).toInstance(new UserWrites)
    bind(classOf[IssueFieldWrites]).toInstance(new IssueFieldWrites(fields))

    // Writer
    bind(classOf[ProjectWriter]).to(classOf[ProjectFileWriter])
    bind(classOf[ComponentWriter]).to(classOf[ComponentFileWriter])
    bind(classOf[VersionWriter]).to(classOf[VersionFileWriter])
    bind(classOf[IssueTypeWriter]).to(classOf[IssueTypeFileWriter])
    bind(classOf[FieldWriter]).to(classOf[FieldFileWriter])
    bind(classOf[IssueWriter]).to(classOf[IssueFileWriter])

    // Exporter
    bind(classOf[ProjectService]).to(classOf[JiraClientProjectService])
    bind(classOf[CategoryService]).to(classOf[JiraClientCategoryService])
    bind(classOf[VersionService]).to(classOf[JiraClientVersionService])
    bind(classOf[IssueTypeService]).to(classOf[JiraClientIssueTypeService])
    bind(classOf[FieldService]).to(classOf[JiraClientFieldService])
    bind(classOf[StatusService]).to(classOf[JiraClientStatusService])
    bind(classOf[IssueService]).to(classOf[JiraClientIssueService])

    // Mapping-file
    bind(classOf[MappingFileService]).to(classOf[MappingFileServiceImpl])
  }
}
