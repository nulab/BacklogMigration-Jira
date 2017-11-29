package com.nulabinc.backlog.j2b.exporter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.conf.JiraBacklogPaths
import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingCollectDatabase
import com.nulabinc.backlog.j2b.jira.domain.{CollectData, JiraProjectKey}
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.j2b.jira.writer._
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging, ProgressBar}
import com.nulabinc.jira.client.domain._
import com.nulabinc.jira.client.domain.changeLog.{AssigneeFieldId, ComponentChangeLogItemField, CustomFieldFieldId, FixVersion}
import com.nulabinc.jira.client.domain.field.Field
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

    // issue
    val statuses  = statusService.all()
    val total     = issueService.count()
    val fields    = fieldService.all()
    fetchIssue(statuses, categories, versions, fields, 1, total, 0, 100)

    // custom field
    fieldWriter.write(mappingCollectDatabase, fields)
    ConsoleOut.boldln(Messages("message.executed", Messages("common.custom_field"), Messages("message.exported")), 1)

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
                         fields: Seq[Field],
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
            changeLogs = ChangeLogFilter.filter(components, versions, fields, issueChangeLogs)
          )

          // collect custom fields
          issueWithFilteredChangeLogs.changeLogs.foreach { changeLog =>
            changeLog.items.foreach { changeLogItem =>
              changeLogItem.fieldId match {
                case Some(CustomFieldFieldId(id)) =>
                  mappingCollectDatabase.addCustomField(id, changeLogItem.fromDisplayString)
                  mappingCollectDatabase.addCustomField(id, changeLogItem.toDisplayString)
                case _ => ()
              }
            }
          }

          // export issue (values are initialized)
          val initializedBacklogIssue = initializer.initialize(
            mappingCollectDatabase  = mappingCollectDatabase,
            fields                  = fields,
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

          val changeLogUsers     = changeLogs.map(u => Some(u.author.name))
          val changeLogItemUsers = changeLogs.flatMap { changeLog =>
            changeLog.items.flatMap { changeLogItem =>
              changeLogItem.fieldId match {
                case Some(AssigneeFieldId) => Set(changeLogItem.from, changeLogItem.to)
                case _                     => Set.empty[Option[String]]
              }
            }
          }

          Set(
            Some(issue.creator.name),
            issue.assignee.map(_.name)
          ).foreach { maybeKey =>
            if (!mappingCollectDatabase.userExistsFromAllUsers(maybeKey)) {
              userService.optUserOfKey(maybeKey) match {
                case Some(u) if maybeKey.contains(u.name)  => mappingCollectDatabase.add(u)
                case Some(_)                               => mappingCollectDatabase.add(maybeKey)
                case None                                  => mappingCollectDatabase.addIgnoreUser(maybeKey)
              }
            }
          }

          (changeLogUsers ++ changeLogItemUsers).foreach { maybeUserName =>
            if (!mappingCollectDatabase.userExistsFromAllUsers(maybeUserName)) {
              userService.optUserOfName(maybeUserName) match {
                case Some(u) if maybeUserName.contains(u.name)  => mappingCollectDatabase.add(u)
                case Some(_)                                    => mappingCollectDatabase.add(maybeUserName)
                case None                                       => mappingCollectDatabase.add(maybeUserName)
              }
            }
          }
        }
      }
      fetchIssue(statuses, components, versions, fields, index + mappingCollectDatabase.existUsers.size , total, startAt + maxResults, maxResults)
    }
  }


}
