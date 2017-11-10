package com.nulabinc.jira.client.domain

import org.joda.time.DateTime

case class ChangeLog(
  id: Long,
  author: User,
  createdAt: DateTime,
  items: Seq[ChangeLogItem]
)

case class ChangeLogItem(
  field: String,
  fieldType: String,
  fieldId: Option[FieldId],
  from: Option[String],
  to: Option[String]
)

object ChangeLogItem {
  object FieldType {
    val JIRA = "jira"
    val PRIORITY = "priority"
    val CUSTOM = "custom"
  }
}

case class ChangeLogResult(
  total: Long,
  isLast: Boolean,
  values: Seq[ChangeLog]
)

sealed abstract class FieldId(val value: String)

case object AttachmentFieldId extends FieldId("attachment")
case object AssigneeFieldId extends FieldId("assignee")
case object IssueTypeFieldId extends FieldId("issuetype")
case object ComponentFieldId extends FieldId("components")
case object DescriptionFieldId extends FieldId("description")
case object FixVersionFieldId extends FieldId("fixVersions")
case object PriorityFieldId extends FieldId("priority")
case object SummaryFieldId extends FieldId("summary")
case object StatusFieldId extends FieldId("status")
case object DueDateFieldId extends FieldId("duedate")
case object TimeOriginalEstimateFieldId extends FieldId("timeoriginalestimate")
case object TimeEstimateFieldId extends FieldId("timeestimate")
case object ResolutionFieldId extends FieldId("resolution")
case class CustomFieldFieldId(id: String) extends FieldId(id)
case class GeneralFieldId(id: String) extends FieldId(id)

object FieldId {

  def parse(value: String): FieldId = value match {
    case AttachmentFieldId.value           => AttachmentFieldId
    case AssigneeFieldId.value             => AssigneeFieldId
    case IssueTypeFieldId.value            => IssueTypeFieldId
    case ComponentFieldId.value            => ComponentFieldId
    case DescriptionFieldId.value          => DescriptionFieldId
    case FixVersionFieldId.value           => FixVersionFieldId
    case PriorityFieldId.value             => PriorityFieldId
    case SummaryFieldId.value              => SummaryFieldId
    case StatusFieldId.value               => StatusFieldId
    case DueDateFieldId.value              => DueDateFieldId
    case TimeOriginalEstimateFieldId.value => TimeOriginalEstimateFieldId
    case TimeEstimateFieldId.value         => TimeEstimateFieldId
    case ResolutionFieldId.value           => ResolutionFieldId
    case v if v.startsWith("customfield_") => CustomFieldFieldId(v)
    case v                                 => GeneralFieldId(v)
  }
}