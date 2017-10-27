package com.nulabinc.backlog.j2b.exporter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.j2b.jira.writer._
import com.nulabinc.jira.client.domain.issue.Issue

class Exporter @Inject()(projectKey: JiraProjectKey,
                         projectService: ProjectService,
                         projectWriter: ProjectWriter,
                         categoryService: CategoryService,
                         categoryWriter: ComponentWriter,
                         versionService: VersionService,
                         versionsWriter: VersionWriter,
                         issueTypeService: IssueTypeService,
                         issueTypesWriter: IssueTypeWriter,
                         fieldService: FieldService,
                         fieldWriter: FieldWriter,
                         issueService: IssueService,
                         issueWriter: IssueWriter,
                         statusService: StatusService) {

  def export(): Unit = {

    val project = projectService.getProjectByKey(projectKey)
    val categories = categoryService.all()
    val versions = versionService.all()
    val issueTypes = issueTypeService.all()
    val fields = fieldService.all()
//    val statuses = statusService.all()

    for {
      _ <- projectWriter.write(project).right
      _ <- categoryWriter.write(categories).right
      _ <- versionsWriter.write(versions).right
      _ <- issueTypesWriter.write(issueTypes).right
      _ <- fieldWriter.write(fields).right
    } yield ()


  }

  private def fetchIssue(startAt: Long, maxResults: Long) = {

    val issues = issueService.issues(startAt, maxResults)

    issueWriter.write(issues)


  }
}
