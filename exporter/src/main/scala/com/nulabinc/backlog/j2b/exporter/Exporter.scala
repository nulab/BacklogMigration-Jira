package com.nulabinc.backlog.j2b.exporter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.domain.{CollectData, JiraProjectKey}
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.j2b.jira.writer._
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging, ProgressBar}
import com.nulabinc.jira.client.domain._
import com.nulabinc.jira.client.domain.changeLog.AssigneeFieldId
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
                         priorityService: PriorityService,
                         commentService: CommentService,
                         commentWriter: CommentWriter,
                         initializer: IssueInitializer,
                         userService: UserService)
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
        case (issue, i) => {

          // changelogs
          val issueWithChangeLogs = issueService.injectChangeLogsToIssue(issue) // API Call

          // comments
          val comments = commentService.issueComments(issueWithChangeLogs)

          // attachments TODO: re
          issueService.downloadAttachments(issueWithChangeLogs)

          // export issue
          val initializedBacklogIssue = initializer.initialize(issueWithChangeLogs)
          issueWriter.write(initializedBacklogIssue, issueWithChangeLogs.createdAt.toDate)

          // export issue comments
          val changeLogs = ChangeLogsPlayer.play(initializedBacklogIssue.categoryNames, issueWithChangeLogs.changeLogs)
          commentWriter.write(initializedBacklogIssue, comments, changeLogs, issueWithChangeLogs.attachments)

          console(i + index.toInt, total.toInt)

          val changeLogUsers = issueWithChangeLogs.changeLogs.map(_.author)

          val collectedUsers = Seq(
            Some(issueWithChangeLogs.creator),
            issueWithChangeLogs.assignee
          ).filter(_.nonEmpty).flatten ++ changeLogUsers ++ users

          val changeLogItemUsers = changeLogs
            .flatMap { changeLog =>
              changeLog.items
                .filter(_.fieldId.contains(AssigneeFieldId)) // ChangeLogItem.FieldId is AssigneeFieldId
            }.flatMap { changeLogItem =>
              Seq(
                collectedUsers.find( u => changeLogItem.from.contains(u.name)) match {
                  case Some(user) => Some(user)
                  case None       => userService.optUserOfKey(changeLogItem.from)
                },
                collectedUsers.find( u => changeLogItem.to.contains(u.name)) match {
                  case Some(user) => Some(user)
                  case None       => userService.optUserOfKey(changeLogItem.to)
                }
              ).flatten
            }

          collectedUsers ++ changeLogItemUsers
        }
      }
      fetchIssue(collected.flatten.toSet, index + collected.length , total, startAt + maxResults, maxResults)
    }
  }


}
