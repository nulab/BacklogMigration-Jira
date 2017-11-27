package com.nulabinc.backlog.j2b.exporter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.issue.writer.convert._
import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingCollectDatabase
import com.nulabinc.backlog.j2b.jira.service.{IssueService, UserService}
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils._
import com.nulabinc.jira.client.domain.{Comment, User}
import com.nulabinc.jira.client.domain.changeLog._
import com.nulabinc.jira.client.domain.issue._

class IssueInitializer @Inject()(implicit val issueWrites: IssueWrites,
                                 implicit val attachmentWrites: AttachmentWrites,
                                 implicit val userWrites: UserWrites,
                                 implicit val customFieldWrites: FieldWrites,
                                 implicit val customFieldValueWrites: IssueFieldWrites,
                                 userService: UserService,
                                 issueService: IssueService)
    extends Logging {

  def initialize(mappingCollectDatabase: MappingCollectDatabase, issue: Issue, comments: Seq[Comment]): BacklogIssue = {
    //attachments
//    val attachmentFilter    = new AttachmentFilter(issue.changeLogs)
//    val filteredAttachments = attachmentFilter.filter(issue.attachments)

    val filteredIssue = AttachmentFilter.filteredIssue(issue, comments)

    val backlogIssue = Convert.toBacklog(filteredIssue)

    backlogIssue.copy(
      summary           = summary(filteredIssue),
//      optParentIssueId = parentIssueId(issue),
      description       = description(filteredIssue),
      optDueDate        = dueDate(filteredIssue),
      optEstimatedHours = estimatedHours(filteredIssue),
      optIssueTypeName  = issueTypeName(filteredIssue),
      categoryNames     = categoryNames(filteredIssue),
//      milestoneNames  = milestoneNames(issue),
      versionNames      = milestoneNames(filteredIssue),
      priorityName      = priorityName(filteredIssue),
      optAssignee       = assignee(mappingCollectDatabase, filteredIssue),
//      customFields = issue.issueFields.flatMap(customField),
      attachments       = attachmentNames(filteredIssue),
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

  private def description(issue: Issue): String = {
    val issueInitialValue = new IssueInitialValue(ChangeLogItem.FieldType.JIRA, DescriptionFieldId)
    issueInitialValue.findChangeLogItem(issue.changeLogs) match {
      case Some(detail) => detail.fromDisplayString.getOrElse("")
      case None         => issue.description.getOrElse("")
    }
  }

  private def dueDate(issue: Issue): Option[String] = {
    val initialValues = issue.dueDate.map(d => DateUtil.dateFormat(d)) match {
      case Some(dateString) => Seq(dateString + " 00:00:00.0")  // player reads "display string"
      case _                => Seq.empty[String]
    }
    ChangeLogsPlayer.reversePlay(DueDateChangeLogItemField, initialValues, issue.changeLogs).headOption
  }

  private def estimatedHours(issue: Issue): Option[Float] = {
    val issueInitialValue = new IssueInitialValue(ChangeLogItem.FieldType.JIRA, TimeEstimateFieldId)
    issueInitialValue.findChangeLogItem(issue.changeLogs) match {
      case Some(detail) => detail.fromDisplayString.filter(_.nonEmpty).map(_.toFloat / 3600)
      case None         => issue.timeTrack.flatMap(_.originalEstimateSeconds.map(_.toFloat / 3600))
    }
  }

  private def issueTypeName(issue: Issue): Option[String] = {
    val issueInitialValue = new IssueInitialValue(ChangeLogItem.FieldType.JIRA, IssueTypeFieldId)
    issueInitialValue.findChangeLogItem(issue.changeLogs) match {
      case Some(detail) => detail.fromDisplayString
      case None         => Option(issue.issueType.name)
    }
  }

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
        if (mappingCollectDatabase.existsByName(detail.from)) {
          mappingCollectDatabase.findByName(detail.from).map( u => Convert.toBacklog(User(u.name, u.displayName)))
        } else {
          val optUser = userService.optUserOfKey(detail.from) match {
            case Some(u) => Some(mappingCollectDatabase.add(u))
            case None    => mappingCollectDatabase.add(detail.from); None
          }
          optUser.map(Convert.toBacklog(_))
        }
      case None         => issue.assignee.map(Convert.toBacklog(_))
    }
  }

//  private def customField(customField: IssueField): Option[BacklogCustomField] = {
//    val optCustomFieldDefinition = exportContext.propertyValue.customFieldDefinitionOfName(customField.getName)
//    optCustomFieldDefinition match {
//      case Some(customFieldDefinition) =>
//        if (customFieldDefinition.isMultiple) multipleCustomField(customField, customFieldDefinition)
//        else singleCustomField(customField, customFieldDefinition)
//      case _ => None
//    }
//  }
//
//  private def multipleCustomField(issueField: IssueField, field: Field, changeLogs: Seq[ChangeLog]): Option[BacklogCustomField] = {
//    val issueInitialValue = new IssueInitialValue(ChangeLogItem.FieldType.CUSTOM, field.id)
//    val optDetails        = issueInitialValue.findJournalDetails(changeLogs)
//    val initialValues     = optDetails match {
//      case Some(details) => details.flatMap(detail => Convert.toBacklog((issueField.id, detail.from)))
//      case _             => issueField.value.asInstanceOf[ArrayFieldValue].values
//    }
//    Convert.toBacklog(issueField) match {
//      case Some(backlogCustomField) => Some(backlogCustomField.copy(values = initialValues))
//      case _                        => None
//    }
//  }
//
//  private def singleCustomField(issueField: IssueField, field: Field, changeLogs: Seq[ChangeLog]): Option[BacklogCustomField] = {
//    val issueInitialValue = new IssueInitialValue(ChangeLogItem.FieldType.CUSTOM, field.id)
//    val initialValue: Option[String] =
//      issueInitialValue.findJournalDetail(changeLogs) match {
//        case Some(detail) => Convert.toBacklog((issueField.id, detail.from))
//        case _            => Convert.toBacklog((issueField.id, Option(issueField.value.value)))
//      }
//    Convert.toBacklog(issueField) match {
//      case Some(backlogCustomField) => Some(backlogCustomField.copy(optValue = initialValue))
//      case _                        => None
//    }
//  }

}

