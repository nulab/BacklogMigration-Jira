package com.nulabinc.backlog.j2b.exporter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.issue.writer.convert._
import com.nulabinc.backlog.j2b.jira.converter.PriorityConverter
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.convert.writes.UserWrites
import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils._
import com.nulabinc.jira.client.domain._
import com.nulabinc.jira.client.domain.issue._

class IssueInitializer @Inject()(implicit val issueWrites: IssueWrites,
                                 implicit val attachmentWrites: AttachmentWrites,
                                 implicit val userWrites: UserWrites,
                                 implicit val customFieldWrites: FieldWrites,
                                 implicit val customFieldValueWrites: IssueFieldWrites)
    extends Logging {

  def initialize(issue: Issue): BacklogIssue = {
    //attachments
//    val attachmentFilter    = new AttachmentFilter(journals)
//    val filteredAttachments = attachmentFilter.filter(attachments)
//    val backlogAttachments  = filteredAttachments.map(Convert.toBacklog(_))
//    filteredAttachments.foreach(attachment)

    val backlogIssue = Convert.toBacklog(issue)

    backlogIssue.copy(
      summary = summary(issue),
//      optParentIssueId = parentIssueId(issue),
      description = description(issue),
//      optStartDate = startDate(issue),
//      optDueDate = dueDate(issue),
//      optEstimatedHours = estimatedHours(issue),
//      optIssueTypeName = issueTypeName(issue),
//      categoryNames = categoryNames(issue),
//      milestoneNames = milestoneNames(issue),
      priorityName = priorityName(issue),
//      optAssignee = assignee(issue),
//      customFields = issue.issueFields.flatMap(customField),
//      attachments = backlogAttachments,
      notifiedUsers = Seq.empty[BacklogUser]
    )
  }

  private def summary(issue: Issue): BacklogIssueSummary = {
    val issueInitialValue = new IssueInitialValue(ChangeLogItem.FieldType.JIRA, SummaryFieldId)
    issueInitialValue.findJournalDetail(issue.changeLogs) match {
      case Some(detail) => BacklogIssueSummary(value = detail.from.getOrElse(""), original = issue.summary)
      case None         => BacklogIssueSummary(value = issue.summary, original = issue.summary)
    }
  }

//  private def parentIssueId(issue: Issue): Option[Long] = {
//    val issueInitialValue = new IssueInitialValue(RedmineConstantValue.ATTR, RedmineConstantValue.Attr.PARENT)
//    issueInitialValue.findJournalDetail(journals) match {
//      case Some(detail) =>
//        Option(detail.getOldValue) match {
//          case Some(value) if (value.nonEmpty) =>
//            StringUtil.safeStringToInt(value) match {
//              case Some(intValue) => Some(intValue)
//              case _              => None
//            }
//          case _ => None
//        }
//      case None => Option(issue.getParentId).map(_.intValue())
//    }
//  }

  private def description(issue: Issue): String = {
    val issueInitialValue = new IssueInitialValue(ChangeLogItem.FieldType.JIRA, DescriptionFieldId)
    issueInitialValue.findJournalDetail(issue.changeLogs) match {
      case Some(detail) => detail.from.getOrElse("")
      case None         => issue.description.getOrElse("")
    }
  }

//  private def startDate(issue: Issue): Option[String] = {
//    val issueInitialValue = new IssueInitialValue(RedmineConstantValue.ATTR, RedmineConstantValue.Attr.START_DATE)
//    issueInitialValue.findJournalDetail(journals) match {
//      case Some(detail) => Option(detail.getOldValue)
//      case None         => Option(issue.getStartDate).map(DateUtil.dateFormat)
//    }
//  }
//
//  private def dueDate(issue: Issue): Option[String] = {
//    val issueInitialValue = new IssueInitialValue(RedmineConstantValue.ATTR, RedmineConstantValue.Attr.DUE_DATE)
//    issueInitialValue.findJournalDetail(journals) match {
//      case Some(detail) => Option(detail.getOldValue)
//      case None         => Option(issue.getDueDate).map(DateUtil.dateFormat)
//    }
//  }
//
//  private def estimatedHours(issue: Issue): Option[Float] = {
//    val issueInitialValue = new IssueInitialValue(RedmineConstantValue.ATTR, RedmineConstantValue.Attr.ESTIMATED_HOURS)
//    issueInitialValue.findJournalDetail(journals) match {
//      case Some(detail) => Option(detail.getOldValue).filter(_.nonEmpty).map(_.toFloat)
//      case None         => Option(issue.getEstimatedHours).map(_.toFloat)
//    }
//  }
//
//  private def issueTypeName(issue: Issue): Option[String] = {
//    val issueInitialValue = new IssueInitialValue(RedmineConstantValue.ATTR, RedmineConstantValue.Attr.TRACKER)
//    issueInitialValue.findJournalDetail(journals) match {
//      case Some(detail) =>
//        exportContext.propertyValue.trackerOfId(Option(detail.getOldValue)).map(_.getName)
//      case None => Option(issue.getTracker).map(_.getName)
//    }
//  }
//
//  private def categoryNames(issue: Issue): Seq[String] = {
//    val issueInitialValue = new IssueInitialValue(RedmineConstantValue.ATTR, RedmineConstantValue.Attr.CATEGORY)
//    val optDetails        = issueInitialValue.findJournalDetails(journals)
//    optDetails match {
//      case Some(details) =>
//        details.flatMap { detail =>
//          exportContext.propertyValue.categoryOfId(Option(detail.getOldValue)).map(_.getName)
//        }
//      case _ => Option(issue.getCategory).map(_.getName).toSeq
//    }
//  }
//
//  private def milestoneNames(issue: Issue): Seq[String] = {
//    val issueInitialValue = new IssueInitialValue(RedmineConstantValue.ATTR, RedmineConstantValue.Attr.VERSION)
//    val optDetails        = issueInitialValue.findJournalDetails(journals)
//    optDetails match {
//      case Some(details) =>
//        details.flatMap { detail =>
//          exportContext.propertyValue.versionOfId(Option(detail.getOldValue)).map(_.getName)
//        }
//      case _ => Option(issue.getTargetVersion).map(_.getName).toSeq
//    }
//  }

  private def priorityName(issue: Issue): String = {
    val issueInitialValue = new IssueInitialValue(ChangeLogItem.FieldType.JIRA, PriorityFieldId)
    issueInitialValue.findJournalDetail(issue.changeLogs) match {
      case Some(detail) => detail.from.getOrElse("")
      case None         => issue.priority.name
    }
  }

//  private def assignee(issue: Issue): Option[BacklogUser] = {
//    val issueInitialValue = new IssueInitialValue(RedmineConstantValue.ATTR, RedmineConstantValue.Attr.ASSIGNED)
//    issueInitialValue.findJournalDetail(journals) match {
//      case Some(detail) =>
//        exportContext.propertyValue.userOfId(Option(detail.getOldValue)).map(Convert.toBacklog(_))
//      case None => Option(issue.getAssignee).map(Convert.toBacklog(_))
//    }
//  }
//
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

