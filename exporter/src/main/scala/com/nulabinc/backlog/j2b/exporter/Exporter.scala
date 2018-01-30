package com.nulabinc.backlog.j2b.exporter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.exporter.console.RemainingTimeCalculator
import com.nulabinc.backlog.j2b.jira.conf.JiraBacklogPaths
import com.nulabinc.backlog.j2b.jira.domain.export.{Field, FieldType}
import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingCollectDatabase
import com.nulabinc.backlog.j2b.jira.domain.{CollectData, JiraProjectKey}
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.j2b.jira.utils.DateChangeLogConverter
import com.nulabinc.backlog.j2b.jira.writer._
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging, ProgressBar}
import com.nulabinc.jira.client.domain._
import com.nulabinc.jira.client.domain.changeLog.{AssigneeFieldId, ComponentChangeLogItemField, CustomFieldFieldId, FixVersion}
import com.nulabinc.jira.client.domain.field.FieldSchema
import com.nulabinc.jira.client.domain.issue._
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
    extends Logging
    with DateChangeLogConverter {

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

    // issue type
    val issueTypes = issueTypeService.all()
    issueTypesWriter.write(issueTypes)
    ConsoleOut.boldln(Messages("message.executed", Messages("common.issue_type"), Messages("message.exported")), 1)

    // issue
    val statuses = statusService.all()
    val total = issueService.count()
    val calculator = new RemainingTimeCalculator(total)
    val fields = for {
      field  <- fieldService.all()
      schema <- field.schema
    } yield {
      Field(
        id = field.id,
        name = field.name,
        schema = FieldType(
          schemaType = schema.`type`,
          schemaSystem = schema.system,
          schemaItems = schema.items,
          schemaCustom = schema.custom
        )
      )
    }

    fetchIssue(calculator, statuses, categories, versions, fields, 1, total, 0, 100)

    // version & milestone
    versionsWriter.write(versions, mappingCollectDatabase.milestones)
    ConsoleOut.boldln(Messages("message.executed", Messages("common.version"), Messages("message.exported")), 1)

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

  private def fetchIssue(calculator: RemainingTimeCalculator,
                         statuses: Seq[Status],
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

          // milestone
          val milestones = MilestoneExtractor.extract(fields, issue.issueFields)
          milestones.foreach(m => mappingCollectDatabase.addMilestone(m))

          // filter change logs and custom fields
          val issueWithFilteredChangeLogs: Issue = issue.copy(
            changeLogs = {
              val filtered = ChangeLogFilter.filter(fields, components, versions, issueChangeLogs)
              convertDateChangeLogs(filtered, fields)
            },
            issueFields = IssueFieldFilter.filterMilestone(fields, issue.issueFields)
          )

          def saveIssueFieldValue(id: String, fieldValue: FieldValue): Unit = fieldValue match {
            case StringFieldValue(value) => mappingCollectDatabase.addCustomField(id, Some(value))
            case NumberFieldValue(value) => mappingCollectDatabase.addCustomField(id, Some(value.toString))
            case ArrayFieldValue(values) => values.map(v => mappingCollectDatabase.addCustomField(id, Some(v.value)))
            case OptionFieldValue(value) => saveIssueFieldValue(id, value.value)
            case AnyFieldValue(value)    => mappingCollectDatabase.addCustomField(id, Some(value))
            case UserFieldValue(user)    => mappingCollectDatabase.addCustomField(id, Some(user.identifyKey))
          }

          issueWithFilteredChangeLogs.issueFields.foreach(v => saveIssueFieldValue(v.id, v.value))

          // collect custom fields
          issueWithFilteredChangeLogs.changeLogs.foreach { changeLog =>
            changeLog.items.foreach { changeLogItem =>
              (changeLogItem.fieldId, fields.find(_.name == "Sprint")) match {
                case (Some(CustomFieldFieldId(id)), Some(sprintDefinition)) if sprintDefinition.id == id => ()
                case (Some(CustomFieldFieldId(id)), _) =>
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
            milestones              = milestones,
            issue                   = issueWithFilteredChangeLogs,
            comments                = comments
          )
          issueWriter.write(initializedBacklogIssue, issue.createdAt.toDate)

          // export issue comments
          val categoryPlayedChangeLogs  = ChangeLogsPlayer.play(ComponentChangeLogItemField, initializedBacklogIssue.categoryNames, issueWithFilteredChangeLogs.changeLogs)
          val versionPlayedChangeLogs   = ChangeLogsPlayer.play(FixVersion, initializedBacklogIssue.versionNames, categoryPlayedChangeLogs)
          val statusPlayedChangeLogs    = ChangeLogStatusConverter.convert(versionPlayedChangeLogs, statuses)
          val changeLogs                = ChangeLogIssueLinkConverter.convert(statusPlayedChangeLogs, initializedBacklogIssue)

          commentWriter.write(initializedBacklogIssue, comments, changeLogs, issue.attachments)

//          console(i + index.toInt, total.toInt)

          calculator.progress(i + index.toInt, total.toInt, issue.summary)

          val changeLogUsers     = changeLogs.map(u => Some(u.author.name))
          val changeLogItemUsers = changeLogs.flatMap { changeLog =>
            changeLog.items.flatMap { changeLogItem =>
              changeLogItem.fieldId match {
                case Some(AssigneeFieldId) => Set(changeLogItem.from, changeLogItem.to)
                case _                     => Set.empty[Option[String]]
              }
            }
          }

          def assignToDB(user: User): Unit = {
            if (!mappingCollectDatabase.userExistsFromAllUsers(Some(user.identifyKey))) {
              (user.key, user.name) match {
                case (Some(key), _) =>
                  userService.optUserOfKey(Some(key)) match {
                    case Some(u) if Some(key).contains(u.name) => mappingCollectDatabase.add(u)
                    case Some(_)                               => mappingCollectDatabase.add(Some(key))
                    case None                                  => mappingCollectDatabase.addIgnoreUser(Some(key))
                  }
                case (None, name) =>
                  userService.optUserOfName(Some(name)) match {
                    case Some(u) if Some(name).contains(u.name) => mappingCollectDatabase.add(u)
                    case Some(_)                               => mappingCollectDatabase.add(Some(name))
                    case None                                  => mappingCollectDatabase.addIgnoreUser(Some(name))
                  }
              }
            }
          }

          assignToDB(issue.creator)
          issue.assignee.foreach(assignToDB)

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
      fetchIssue(calculator, statuses, components, versions, fields, index + issues.length , total, startAt + maxResults, maxResults)
    }
  }


}
