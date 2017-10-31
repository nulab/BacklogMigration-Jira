package com.nulabinc.backlog.j2b.exporter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.domain.{JiraProjectKey, CollectData}
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.j2b.jira.writer._
import com.nulabinc.jira.client.domain.{Status, User}

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
                         statusService: StatusService,
                         priorityService: PriorityService) {

  def export(): CollectData = {

    val project = projectService.getProjectByKey(projectKey)
    val categories = categoryService.all()
    val versions = versionService.all()
    val issueTypes = issueTypeService.all()
    val fields = fieldService.all()
    val priorities = priorityService.allPriorities()
//    val statuses = statusService.all()

    for {
      _ <- projectWriter.write(project).right
      _ <- categoryWriter.write(categories).right
      _ <- versionsWriter.write(versions).right
      _ <- issueTypesWriter.write(issueTypes).right
      _ <- fieldWriter.write(fields).right
    } yield ()



    val users = fetchIssue(Set.empty[User], 0, 100)

    CollectData(users, Set.empty[Status], priorities)
  }

  private def fetchIssue(users: Set[User], startAt: Long, maxResults: Long): Set[User] = {

    val issues = issueService.issues(startAt, maxResults)

    if (issues.isEmpty) users
    else {
      val collected = issues.map { issue =>

        //        val issueWithChangeLogs = issueService.injectChangeLogsToIssue(issue) // API Call

        issueWriter.write(issue)

        // Collect users
        Seq(
          Some(issue.creator),
          issue.assignee
        ).filter(_.nonEmpty).flatten
      }
      fetchIssue(users ++ collected.flatten, startAt + 100, maxResults)
    }
  }
}
