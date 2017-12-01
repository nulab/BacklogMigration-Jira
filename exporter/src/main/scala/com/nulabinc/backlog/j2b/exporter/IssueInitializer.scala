package com.nulabinc.backlog.j2b.exporter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.issue.writer.convert._
import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingCollectDatabase
import com.nulabinc.backlog.j2b.jira.service.{IssueService, UserService}
import com.nulabinc.backlog.j2b.jira.utils._
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils._
import com.nulabinc.jira.client.domain.{Comment, User}
import com.nulabinc.jira.client.domain.changeLog._
import com.nulabinc.jira.client.domain.field.{DatetimeSchema, Field}
import com.nulabinc.jira.client.domain.issue._

class IssueInitializer @Inject()(implicit val issueWrites: IssueWrites,
                                 implicit val attachmentWrites: AttachmentWrites,
                                 implicit val userWrites: UserWrites,
                                 implicit val customFieldWrites: FieldWrites,
                                 implicit val customFieldValueWrites: IssueFieldWrites,
                                 userService: UserService,
                                 issueService: IssueService)
    extends Logging
    with SecondToHourFormatter
    with DatetimeToDateFormatter {

  def initialize(mappingCollectDatabase: MappingCollectDatabase, fields: Seq[Field], issue: Issue, comments: Seq[Comment]): BacklogIssue = {
    //attachments
//    val attachmentFilter    = new AttachmentFilter(issue.changeLogs)
//    val filteredAttachments = attachmentFilter.filter(issue.attachments)

    val filteredIssue = AttachmentFilter.filteredIssue(issue, comments)

    val backlogIssue = Convert.toBacklog(filteredIssue)

    backlogIssue.copy(
      summary           = summary(filteredIssue),
      optParentIssueId = parentIssueId(issue),
      description       = description(filteredIssue),
      optDueDate        = dueDate(filteredIssue),
      optEstimatedHours = estimatedHours(filteredIssue),
      optIssueTypeName  = issueTypeName(filteredIssue),
      categoryNames     = categoryNames(filteredIssue),
//      milestoneNames  = milestoneNames(issue),
      versionNames      = milestoneNames(filteredIssue),
      priorityName      = priorityName(filteredIssue),
      optAssignee       = assignee(mappingCollectDatabase, filteredIssue),
      customFields      = filteredIssue.issueFields.flatMap(f => customField(fields, f, filteredIssue.changeLogs)),
      attachments       = attachmentNames(filteredIssue),
      optActualHours    = actualHours(filteredIssue),
      notifiedUsers     = Seq.empty[BacklogUser]
    )
  }

  private def summary(issue: Issue): BacklogIssueSummary = {
    val issueInitialValue = new IssueInitialValue(ChangeLogItem.FieldType.JIRA, SummaryFieldId)
    issueInitialValue.findChangeLogItem(issue.changeLogs) match {
      case Some(detail) => BacklogIssueSummary(value = detail.fromDisplayString.getOrElse(""), original = issue.summary)
      case None         => BacklogIssueSummary(value = issue.summary, original = issue.summary)
    }
  }

  private def parentIssueId(issue: Issue): Option[Long] = {
    val currentValues = issue.parent match {
      case Some(parentIssue) => Seq(parentIssue.id.toString)
      case _                 => Seq.empty[String]
    }
    ChangeLogsPlayer.reversePlay(ParentChangeLogItemField, currentValues, issue.changeLogs).headOption.map(_.toLong)
  }

  private def description(issue: Issue): String = {
    val issueInitialValue = new IssueInitialValue(ChangeLogItem.FieldType.JIRA, DescriptionFieldId)
    issueInitialValue.findChangeLogItem(issue.changeLogs) match {
      case Some(detail) => detail.fromDisplayString.getOrElse("")
      case None         => issue.description.getOrElse("")
    }
  }

  private def dueDate(issue: Issue): Option[String] = {
    val issueInitialValue = new IssueInitialValue(ChangeLogItem.FieldType.JIRA, DueDateFieldId)
    issueInitialValue.findChangeLogItem(issue.changeLogs) match {
      case Some(detail) => detail.from
      case None         => issue.dueDate.map(DateUtil.dateFormat)
    }
  }

  private def estimatedHours(issue: Issue): Option[Float] = {
    val initialValues = issue.timeTrack.flatMap(t => t.originalEstimateSeconds) match {
      case Some(second) => Seq(second.toString)
      case _            => Seq.empty[String]
    }
    val initializedEstimatedSeconds = ChangeLogsPlayer.reversePlay(TimeOriginalEstimateChangeLogItemField, initialValues, issue.changeLogs).headOption
    initializedEstimatedSeconds.map(sec => secondsToHours(sec.toInt))
  }

  private def issueTypeName(issue: Issue): Option[String] =
    ChangeLogsPlayer.reversePlay(IssueTypeChangeLogItemField, Seq(issue.issueType.name), issue.changeLogs).headOption

  private def categoryNames(issue: Issue): Seq[String] =
    ChangeLogsPlayer.reversePlay(ComponentChangeLogItemField, issue.components.map(_.name), issue.changeLogs)

  private def milestoneNames(issue: Issue): Seq[String] =
    ChangeLogsPlayer.reversePlay(FixVersion, issue.fixVersions.map(_.name), issue.changeLogs)

  private def attachmentNames(issue: Issue): Seq[BacklogAttachment] = {
    val histories = ChangeLogsPlayer.reversePlay(AttachmentChangeLogItemField, issue.attachments.map(_.fileName), issue.changeLogs)
    histories.map { h =>
      BacklogAttachment(issue.attachments.find(_.fileName == h).map(_.id), h)
    }
  }

  private def priorityName(issue: Issue): String = {
    val issueInitialValue = new IssueInitialValue(ChangeLogItem.FieldType.JIRA, PriorityFieldId)
    issueInitialValue.findChangeLogItem(issue.changeLogs) match {
      case Some(detail) => detail.fromDisplayString.getOrElse("")
      case None         => issue.priority.name
    }
  }

  private def assignee(mappingCollectDatabase: MappingCollectDatabase, issue: Issue): Option[BacklogUser] = {
    val issueInitialValue = new IssueInitialValue(ChangeLogItem.FieldType.JIRA, AssigneeFieldId)
    issueInitialValue.findChangeLogItem(issue.changeLogs) match {
      case Some(detail) =>
        if (mappingCollectDatabase.userExistsFromAllUsers(detail.from)) {
          mappingCollectDatabase.findByName(detail.from).map( u => Convert.toBacklog(User(u.name, u.displayName)))
        } else {
          val optUser = userService.optUserOfKey(detail.from) match {
            case Some(u) => Some(mappingCollectDatabase.add(u))
            case None    => mappingCollectDatabase.add(detail.from); None
          }
          optUser.map(Convert.toBacklog(_))
        }
      case None => issue.assignee.map(Convert.toBacklog(_))
    }
  }

  private def actualHours(issue: Issue): Option[Float] = {
    val initialValues = issue.timeTrack.flatMap(t => t.timeSpentSeconds) match {
      case Some(second) => Seq(second.toString)
      case _            => Seq.empty[String]
    }
    val initializedTimeSpentSeconds = ChangeLogsPlayer.reversePlay(TimeSpentChangeLogItemField, initialValues, issue.changeLogs).headOption
    initializedTimeSpentSeconds.map(sec => secondsToHours(sec.toInt))
  }

  private def customField(fields: Seq[Field], issueField: IssueField, changeLogs: Seq[ChangeLog]): Option[BacklogCustomField] = {
    val fieldDefinition = fields.find(_.id == issueField.id).get // TODO Fix get
    val currentValues = issueField.value match {
      case ArrayFieldValue(values) => values.map(_.value)
      case value                   => fieldDefinition.schema match {
        case Some(v) => v.schemaType match {
          case DatetimeSchema => Seq(dateTimeStringToDateString(value.value))
          case _              => Seq(value.value)
        }
        case None => Seq(value.value)
      }
    }

    val initialValues = ChangeLogsPlayer.reversePlay(DefaultField(fieldDefinition.name), currentValues, changeLogs)
    Convert.toBacklog(issueField).map { converted =>
      issueField.value match {
        case ArrayFieldValue(_) => converted.copy(values = initialValues)
        case _                  => converted.copy(optValue = initialValues.headOption)
      }
    }
  }
}

