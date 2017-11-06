package com.nulabinc.backlog.j2b.exporter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.domain.{CollectData, JiraProjectKey}
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.j2b.jira.writer._
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging, ProgressBar}
import com.nulabinc.jira.client.domain.{Status, User}
import com.osinka.i18n.Messages

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
                         priorityService: PriorityService)
    extends Logging {

  private val console            = (ProgressBar.progress _)(Messages("common.issues"), Messages("message.exporting"), Messages("message.exported"))
  private val issuesInfoProgress = (ProgressBar.progress _)(Messages("common.issues_info"), Messages("message.collecting"), Messages("message.collected"))

  def export(): CollectData = {

    val project = projectService.getProjectByKey(projectKey)
    val categories = categoryService.all()
    val versions = versionService.all()
    val issueTypes = issueTypeService.all()
    val fields = fieldService.all()
    val priorities = priorityService.allPriorities()
    val statuses = statusService.all()

    // project
    projectWriter.write(project)
    ConsoleOut.boldln(Messages("message.executed", Messages("common.project"), Messages("message.exported")), 1)

    // category
    categoryWriter.write(categories)
    ConsoleOut.boldln(Messages("message.executed", Messages("common.category"), Messages("message.exported")), 1)

    // version
    versionsWriter.write(versions)
    ConsoleOut.boldln(Messages("message.executed", Messages("common.version"), Messages("message.exported")), 1)

    // issue type
    issueTypesWriter.write(issueTypes)
    ConsoleOut.boldln(Messages("message.executed", Messages("common.issue_type"), Messages("message.exported")), 1)

    // custom field
    fieldWriter.write(fields)
    ConsoleOut.boldln(Messages("message.executed", Messages("common.custom_field"), Messages("message.exported")), 1)

    // issue
    val total = issueService.count
    val users = fetchIssue(Set.empty[User], 1, total, 0, 100)

    CollectData(users, statuses, priorities)
  }

  private def fetchIssue(users: Set[User], index: Long, total: Long, startAt: Long, maxResults: Long): Set[User] = {

    val issues = issueService.issues(startAt, maxResults)

    if (issues.isEmpty) users
    else {
      val collected = issues.zipWithIndex.map {
        case (issue, i) =>

          // changelogs
          val issueWithChangeLogs = issueService.injectChangeLogsToIssue(issue) // API Call

          // attachments


          issueWriter.write(issueWithChangeLogs)

        console(i + index.toInt, total.toInt)

        // Collect users
        Seq(
          Some(issueWithChangeLogs.creator),
          issueWithChangeLogs.assignee
        ).filter(_.nonEmpty).flatten
      }
      fetchIssue(users ++ collected.flatten, index + collected.length , total, startAt + maxResults, maxResults)
    }
  }
}
