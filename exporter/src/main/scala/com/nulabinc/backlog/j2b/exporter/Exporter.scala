package com.nulabinc.backlog.j2b.exporter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.conf.JiraBacklogPaths
import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingCollectDatabase
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
                         userService: UserService,
                         mappingCollectDatabase: MappingCollectDatabase)
    extends Logging {

  private val console            = (ProgressBar.progress _)(Messages("common.issues"), Messages("message.exporting"), Messages("message.exported"))
//  private val issuesInfoProgress = (ProgressBar.progress _)(Messages("common.issues_info"), Messages("message.collecting"), Messages("message.collected"))

  def export(backlogPaths: JiraBacklogPaths): CollectData = {

    // project
    val project = projectService.getProjectByKey(projectKey)
    projectWriter.write(project)
    ConsoleOut.boldln(Messages("message.executed", Messages("common.project"), Messages("message.exported")), 1)

    // category
    val categories = categoryService.all()
    categoryWriter.write(categories)
    ConsoleOut.boldln(Messages("message.executed", Messages("common.category"), Messages("message.exported")), 1)

    // version
    val versions = versionService.all()
    versionsWriter.write(versions)
    ConsoleOut.boldln(Messages("message.executed", Messages("common.version"), Messages("message.exported")), 1)

    // issue type
    val issueTypes = issueTypeService.all()
    issueTypesWriter.write(issueTypes)
    ConsoleOut.boldln(Messages("message.executed", Messages("common.issue_type"), Messages("message.exported")), 1)

    // custom field
    val fields = fieldService.all()
    fieldWriter.write(fields)
    ConsoleOut.boldln(Messages("message.executed", Messages("common.custom_field"), Messages("message.exported")), 1)

    // issue
    val statuses = statusService.all()
    val total = issueService.count()
    fetchIssue(statuses, categories, versions, 1, total, 0, 100)

    // Output Jira data
    val priorities    = priorityService.allPriorities()
    val collectedData = CollectData(mappingCollectDatabase.existUsers, statuses, priorities)

    collectedData.outputJiraUsersToFile(backlogPaths.jiraUsersJson)
    collectedData.outputJiraPrioritiesToFile(backlogPaths.jiraPrioritiesJson)
    collectedData.outputJiraStatusesToFile(backlogPaths.jiraStatusesJson)

    collectedData
  }

  private def fetchIssue(statuses: Seq[Status],
                         components: Seq[Component],
                         versions: Seq[Version],
                         index: Long, total: Long, startAt: Long, maxResults: Long): Unit = {

    val issues = issueService.issues(startAt, maxResults)

    if (issues.nonEmpty) {
      issues.zipWithIndex.foreach {
        case (issue, i) => {

          // Change logs
          val issueChangeLogs = issueService.changeLogs(issue) // API Call

          // comments
          val comments = commentService.issueComments(issue)

          // filter change logs
          val issueWithFilteredChangeLogs: Issue = issue.copy(
            changeLogs = ChangeLogFilter.filter(components, versions, issueChangeLogs)
          )

          // export issue (values are initialized)
          val initializedBacklogIssue = initializer.initialize(
            mappingCollectDatabase  = mappingCollectDatabase,
            issue                   = issueWithFilteredChangeLogs,
            comments                = comments
          )
          issueWriter.write(initializedBacklogIssue, issue.createdAt.toDate)

          // export issue comments
          val categoryPlayedChangeLogs  = ChangeLogsPlayer.play(ComponentChangeLogItemField, initializedBacklogIssue.categoryNames, issueWithFilteredChangeLogs.changeLogs)
          val versionPlayedChangeLogs   = ChangeLogsPlayer.play(FixVersion, initializedBacklogIssue.versionNames, categoryPlayedChangeLogs)
          val changeLogs                = ChangeLogStatusConverter.convert(versionPlayedChangeLogs, statuses)
          commentWriter.write(initializedBacklogIssue, comments, changeLogs, issue.attachments)

          console(i + index.toInt, total.toInt)

          changeLogs.foreach( changeLog => mappingCollectDatabase.add(changeLog.author))
          mappingCollectDatabase.add(issue.creator)
          mappingCollectDatabase.add(issue.assignee)

          changeLogs.foreach { changeLog =>
            changeLog.items
              .filter(_.fieldId.contains(AssigneeFieldId))
              .foreach { changeLogItem =>
                List(changeLogItem.from, changeLogItem.to)
                  .foreach { maybeUserName =>
                    if ( ! mappingCollectDatabase.existsByName(maybeUserName)) {
                      userService.optUserOfKey(maybeUserName) match {
                        case Some(u) => mappingCollectDatabase.add(u)
                        case None    => mappingCollectDatabase.add(maybeUserName)
                      }
                    }
                  }
              }
          }
        }
      }
      fetchIssue(statuses, components, versions, index + mappingCollectDatabase.existUsers.size , total, startAt + maxResults, maxResults)
    }
  }


}
