package com.nulabinc.backlog.j2b.exporter

import com.nulabinc.backlog.j2b.exporter.console.RemainingTimeCalculator
import com.nulabinc.backlog.j2b.jira.conf.JiraBacklogPaths
import com.nulabinc.backlog.j2b.jira.domain.export._
import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingCollectDatabase
import com.nulabinc.backlog.j2b.jira.domain.{CollectData, FieldConverter, IssueFieldConverter, JiraProjectKey}
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.j2b.jira.utils.DateChangeLogConverter
import com.nulabinc.backlog.j2b.jira.writer._
import com.nulabinc.backlog.migration.common.interpreters.JansiConsoleDSL
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.nulabinc.jira.client.domain._
import com.nulabinc.jira.client.domain.changeLog.{AssigneeFieldId, ComponentChangeLogItemField, CustomFieldFieldId, FixVersion}
import com.nulabinc.jira.client.domain.issue._
import com.osinka.i18n.Messages
import javax.inject.Inject
import monix.eval.Task

class Exporter @Inject() (
    projectKey: JiraProjectKey,
    projectService: ProjectService[Task],
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
    mappingCollectDatabase: MappingCollectDatabase
) extends Logging
    with DateChangeLogConverter {

//  private val console            = (ProgressBar.progress _)(Messages("common.issues"), Messages("message.exporting"), Messages("message.exported"))
//  private val issuesInfoProgress = (ProgressBar.progress _)(Messages("common.issues_info"), Messages("message.collecting"), Messages("message.collected"))

  def export(backlogPaths: JiraBacklogPaths): Task[CollectData] = {

    val console = new JansiConsoleDSL()

    for {
      // project
      project <- projectService.getProjectByKey(projectKey)
      _ = projectWriter.write(project)
      _ <- console.boldln(
        Messages(
          "message.executed",
          Messages("common.project"),
          Messages("message.exported")
        ),
        1
      )
      // category
      categories = categoryService.all()
      _          = categoryWriter.write(categories)
      _ <- console.boldln(
        Messages(
          "message.executed",
          Messages("common.category"),
          Messages("message.exported")
        ),
        1
      )
      // version
      versions = versionService.all()
      // issue type
      issueTypes = issueTypeService.all()
      _          = issueTypesWriter.write(issueTypes)
      _ <- console.boldln(
        Messages(
          "message.executed",
          Messages("common.issue_type"),
          Messages("message.exported")
        ),
        1
      )
    } yield {
      // issue
      val statuses   = statusService.all(project.key)
      val total      = issueService.count()
      val calculator = new RemainingTimeCalculator(total)
      val fields     = FieldConverter.toExportField(fieldService.all())

      fetchIssue(
        calculator,
        statuses,
        categories,
        versions,
        fields,
        1,
        total,
        0,
        100
      )

      // version & milestone
      versionsWriter.write(versions, mappingCollectDatabase.milestones)
      ConsoleOut.boldln(
        Messages(
          "message.executed",
          Messages("common.version"),
          Messages("message.exported")
        ),
        1
      )

      // custom field
      fieldWriter.write(mappingCollectDatabase, fields)
      ConsoleOut.boldln(
        Messages(
          "message.executed",
          Messages("common.custom_field"),
          Messages("message.exported")
        ),
        1
      )

      // Output Jira data
      val priorities = priorityService.allPriorities()
      val collectedData =
        CollectData(mappingCollectDatabase.existUsers, statuses, priorities)

      collectedData.outputJiraUsersToFile(backlogPaths.jiraUsersJson)
      collectedData.outputJiraPrioritiesToFile(backlogPaths.jiraPrioritiesJson)
      collectedData.outputJiraStatusesToFile(backlogPaths.jiraStatusesJson)

      collectedData
    }
  }

  private def fetchIssue(
      calculator: RemainingTimeCalculator,
      statuses: Seq[Status],
      components: Seq[Component],
      versions: Seq[Version],
      fields: Seq[Field],
      index: Long,
      total: Long,
      startAt: Long,
      maxResults: Long
  ): Unit = {

    val issues = issueService.issues(startAt, maxResults)

    if (issues.nonEmpty) {
      issues.zipWithIndex.foreach {
        case (issue, i) => {

          // Issue fields
          val issueFields =
            IssueFieldConverter.toExportIssueFields(issue.issueFields)

          // Change logs
          val issueChangeLogs = issueService.changeLogs(issue) // API Call

          // comments
          val comments = commentService.issueComments(issue)

          // milestone
          val milestones = MilestoneExtractor.extract(fields, issueFields)
          milestones.foreach(m => mappingCollectDatabase.addMilestone(m))

          // filter change logs and custom fields
          val filteredIssueFields =
            IssueFieldFilter.filterMilestone(fields, issueFields)
          val issueWithFilteredChangeLogs: Issue = issue.copy(
            changeLogs = {
              val filtered = ChangeLogFilter.filter(fields, components, versions, issueChangeLogs)
              convertDateChangeLogs(filtered, fields)
            }
          )

          def saveIssueFieldValue(id: String, fieldValue: FieldValue): Unit =
            fieldValue match {
              case StringFieldValue(value) =>
                mappingCollectDatabase.addCustomField(id, Some(value))
              case NumberFieldValue(value) =>
                mappingCollectDatabase.addCustomField(id, Some(value.toString))
              case ArrayFieldValue(values) =>
                values.map(v => mappingCollectDatabase.addCustomField(id, Some(v.value)))
              case OptionFieldValue(value) =>
                saveIssueFieldValue(id, value.value)
              case UserFieldValue(user) =>
                mappingCollectDatabase.addCustomField(
                  id,
                  Some(user.identifyKey)
                )
              case other =>
                mappingCollectDatabase.addCustomField(id, Some(other.value))
            }
          filteredIssueFields.foreach(v => saveIssueFieldValue(v.id, v.value))

          // collect custom fields
          issueWithFilteredChangeLogs.changeLogs.foreach { changeLog =>
            changeLog.items.foreach { changeLogItem =>
              (changeLogItem.fieldId, fields.find(_.name == "Sprint")) match {
                case (Some(CustomFieldFieldId(id)), Some(sprintDefinition)) if sprintDefinition.id == id =>
                  ()
                case (Some(CustomFieldFieldId(id)), _) =>
                  mappingCollectDatabase.addCustomField(
                    id,
                    changeLogItem.fromDisplayString
                  )
                  mappingCollectDatabase.addCustomField(
                    id,
                    changeLogItem.toDisplayString
                  )
                case _ => ()
              }
            }
          }

          // export issue (values are initialized)
          val initializedBacklogIssue = initializer.initialize(
            mappingCollectDatabase = mappingCollectDatabase,
            fields = fields,
            milestones = milestones,
            issue = issueWithFilteredChangeLogs,
            issueFields = filteredIssueFields,
            comments = comments
          )
          issueWriter.write(initializedBacklogIssue, issue.createdAt)

          // export issue comments
          val categoryPlayedChangeLogs = ChangeLogsPlayer.play(
            ComponentChangeLogItemField,
            initializedBacklogIssue.categoryNames,
            issueWithFilteredChangeLogs.changeLogs
          )
          val versionPlayedChangeLogs = ChangeLogsPlayer.play(
            FixVersion,
            initializedBacklogIssue.versionNames,
            categoryPlayedChangeLogs
          )
          val statusPlayedChangeLogs =
            ChangeLogStatusConverter.convert(versionPlayedChangeLogs, statuses)
          val changeLogs = ChangeLogIssueLinkConverter.convert(
            statusPlayedChangeLogs,
            initializedBacklogIssue
          )

          commentWriter.write(
            initializedBacklogIssue,
            comments,
            changeLogs,
            issue.attachments
          )

          calculator.progress(i + index.toInt)

          // changelog author
          for {
            changelog <- changeLogs
            author    <- changelog.optAuthor
          } yield mappingCollectDatabase.addUser(
            ExistingMappingUser(
              author.accountId,
              author.displayName,
              author.emailAddress
            )
          )

          // changelog value
          for {
            changelog     <- changeLogs
            changelogItem <- changelog.items
          } yield {
            changelogItem.fieldId match {
              case Some(AssigneeFieldId) =>
                changelogItem.from.foreach { str =>
                  mappingCollectDatabase.addChangeLogUser(
                    ChangeLogMappingUser(
                      str,
                      changelogItem.fromDisplayString.getOrElse("")
                    )
                  )
                }
                changelogItem.to.foreach { str =>
                  mappingCollectDatabase.addChangeLogUser(
                    ChangeLogMappingUser(
                      str,
                      changelogItem.toDisplayString.getOrElse("")
                    )
                  )
                }
              case _ =>
                ()
            }
          }

          mappingCollectDatabase.addUser(
            ExistingMappingUser(
              issue.creator.accountId,
              issue.creator.displayName,
              issue.creator.emailAddress
            )
          )
          issue.assignee.foreach(user =>
            mappingCollectDatabase.addUser(
              ExistingMappingUser(
                user.accountId,
                user.displayName,
                issue.creator.emailAddress
              )
            )
          )
        }
      }
      fetchIssue(
        calculator,
        statuses,
        components,
        versions,
        fields,
        index + issues.length,
        total,
        startAt + maxResults,
        maxResults
      )
    }
  }

}
