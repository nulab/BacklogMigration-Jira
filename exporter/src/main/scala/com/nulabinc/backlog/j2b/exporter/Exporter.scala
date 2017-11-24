package com.nulabinc.backlog.j2b.exporter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.conf.JiraBacklogPaths
import com.nulabinc.backlog.j2b.jira.domain.{CollectData, JiraProjectKey}
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.j2b.jira.writer._
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging, ProgressBar}
import com.nulabinc.jira.client.domain._
import com.nulabinc.jira.client.domain.changeLog.{AssigneeFieldId, ComponentChangeLogItemField, FixVersion}
import com.nulabinc.jira.client.domain.issue.Issue
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
//  private val issuesInfoProgress = (ProgressBar.progress _)(Messages("common.issues_info"), Messages("message.collecting"), Messages("message.collected"))

  def export(backlogPaths: JiraBacklogPaths): CollectData = {

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
    val users = fetchIssue(Set.empty[User], statuses, categories, versions, 1, total, 0, 100)

    // Output Jira data
    val collectedData = CollectData(users, statuses, priorities)

    collectedData.outputJiraUsersToFile(backlogPaths.jiraUsersJson)
    collectedData.outputJiraPrioritiesToFile(backlogPaths.jiraPrioritiesJson)
    collectedData.outputJiraStatusesToFile(backlogPaths.jiraStatusesJson)

    collectedData
  }

  private def fetchIssue(users: Set[User],
                         statuses: Seq[Status],
                         components: Seq[Component],
                         versions: Seq[Version],
                         index: Long, total: Long, startAt: Long, maxResults: Long): Set[User] = {

    val issues = issueService.issues(startAt, maxResults)

    if (issues.isEmpty) users
    else {
      val collected = issues.zipWithIndex.map {
        case (issue, i) => {

          // Change logs
          val issueWithChangeLogs = issueService.injectChangeLogsToIssue(issue) // API Call

          // comments
          val comments = commentService.issueComments(issueWithChangeLogs)

          // filter comments
          val changeLogsFilteredIssue: Issue = issueWithChangeLogs.copy(
            changeLogs = ChangeLogFilter.filter(components, versions, issueWithChangeLogs.changeLogs)
          )

          // export issue (values are initialized)
          val initializedBacklogIssue = initializer.initialize(changeLogsFilteredIssue, comments)
          issueWriter.write(initializedBacklogIssue, changeLogsFilteredIssue.createdAt.toDate)

          // export issue comments
          val categoryPlayedChangeLogs  = ChangeLogsPlayer.play(ComponentChangeLogItemField, initializedBacklogIssue.categoryNames, changeLogsFilteredIssue.changeLogs)
          val versionPlayedChangeLogs   = ChangeLogsPlayer.play(FixVersion, initializedBacklogIssue.versionNames, categoryPlayedChangeLogs)
          val changeLogs                = ChangeLogStatusConverter.convert(versionPlayedChangeLogs, statuses)
          commentWriter.write(initializedBacklogIssue, comments, changeLogs, changeLogsFilteredIssue.attachments)

          console(i + index.toInt, total.toInt)

          val changeLogUsers = changeLogs.map(_.author)

          val collectedUsers = Seq(
            Some(changeLogsFilteredIssue.creator),
            changeLogsFilteredIssue.assignee
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
      fetchIssue(collected.flatten.toSet, statuses, components, versions, index + collected.length , total, startAt + maxResults, maxResults)
    }
  }


}
