package com.nulabinc.backlog.j2b.issue.writer.convert

import javax.inject.Inject

import com.nulabinc.backlog.migration.common.conf.BacklogConstantValue
import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils.FileUtil
import com.nulabinc.backlog4j.CustomField.FieldType
import com.nulabinc.jira.client.domain.field._
import com.nulabinc.jira.client.domain._

class ChangelogItemWrites @Inject()(fields: Seq[Field]) extends Writes[ChangeLogItem, BacklogChangeLog] {

  override def writes(changeLogItem: ChangeLogItem) =
    BacklogChangeLog(
      field            = field(changeLogItem),
      optOriginalValue = changeLogItem.fieldId match {
        case Some(AssigneeFieldId)              => changeLogItem.from
        case Some(DueDateFieldId)               => changeLogItem.from
        case Some(TimeOriginalEstimateFieldId)  => changeLogItem.from.map( sec => (sec.toInt / 3600).toString)
        case _                                  => changeLogItem.fromDisplayString
      },
      optNewValue = changeLogItem.fieldId match {
        case Some(AssigneeFieldId)              => changeLogItem.to
        case Some(DueDateFieldId)               => changeLogItem.to
        case Some(TimeOriginalEstimateFieldId)  => changeLogItem.to.map( sec => (sec.toInt / 3600).toString)
        case _                                  => changeLogItem.toDisplayString
      },
      optAttachmentInfo   = attachmentInfo(changeLogItem),
      optAttributeInfo    = attributeInfo(changeLogItem),
      optNotificationInfo = None
    )

  private def field(changeLogItem: ChangeLogItem): String = changeLogItem.fieldId match {
    case Some(CustomFieldFieldId(id)) =>
      val optCustomFieldDefinition = fields.find(_.id == id)
      optCustomFieldDefinition match {
        case Some(field) => field.name
        case _           => throw new RuntimeException(s"custom field id not found [${changeLogItem.field}]")
      }
    case Some(AttachmentFieldId)              => BacklogConstantValue.ChangeLog.ATTACHMENT
    case Some(AssigneeFieldId)                => BacklogConstantValue.ChangeLog.ASSIGNER
    case Some(IssueTypeFieldId)               => BacklogConstantValue.ChangeLog.ISSUE_TYPE
    case Some(ComponentFieldId)               => BacklogConstantValue.ChangeLog.COMPONENT
    case Some(DescriptionFieldId)             => BacklogConstantValue.ChangeLog.DESCRIPTION
    case Some(FixVersionFieldId)              => BacklogConstantValue.ChangeLog.MILESTONE
    case Some(PriorityFieldId)                => BacklogConstantValue.ChangeLog.PRIORITY
    case Some(SummaryFieldId)                 => BacklogConstantValue.ChangeLog.SUMMARY
    case Some(StatusFieldId)                  => BacklogConstantValue.ChangeLog.STATUS
    case Some(DueDateFieldId)                 => BacklogConstantValue.ChangeLog.LIMIT_DATE
    case Some(TimeOriginalEstimateFieldId)    => BacklogConstantValue.ChangeLog.ESTIMATED_HOURS
    case Some(TimeEstimateFieldId)            => changeLogItem.field
    case Some(ResolutionFieldId)              => BacklogConstantValue.ChangeLog.RESOLUTION
    case Some(GeneralFieldId(v))              => v
    case _ if changeLogItem.field == "Parent" => BacklogConstantValue.ChangeLog.ISSUE_TYPE
    case None                                 => changeLogItem.field
  }

  private def attachmentInfo(changeLogItem: ChangeLogItem): Option[BacklogAttachment] = changeLogItem.fieldId match {
    case Some(AttachmentFieldId) => Some(
      BacklogAttachment(
        optId = None,
        name = FileUtil.normalize(changeLogItem.toDisplayString.getOrElse(""))
      )
    )
    case _ => None
  }

  private def attributeInfo(changeLogItem: ChangeLogItem): Option[BacklogAttributeInfo] = changeLogItem.fieldId match {
    case Some(CustomFieldFieldId(id)) =>
      val optCustomFieldDefinition = fields.find(_.id == id)
      val optTypeId = optCustomFieldDefinition match {
        case Some(field) =>
          field.schema.map { schema =>
            (schema.schemaType, schema.customType) match {
              case (StatusSchema, Some(Textarea))         => Some(FieldType.TextArea.getIntValue)
              case (StringSchema, _)                      => Some(FieldType.Text.getIntValue)
              case (NumberSchema, _)                      => Some(FieldType.Numeric.getIntValue)
              case (DateSchema, _)                        => Some(FieldType.Date.getIntValue)
              case (DatetimeSchema, _)                    => Some(FieldType.Date.getIntValue)
              case (ArraySchema, _)                       => Some(FieldType.MultipleList.getIntValue)
              case (UserSchema, _)                        => Some(FieldType.Text.getIntValue)
              case (AnySchema, _)                         => Some(FieldType.Text.getIntValue)
              case (OptionSchema, Some(Select))           => Some(FieldType.SingleList.getIntValue)
              case (OptionSchema, Some(MultiCheckBoxes))  => Some(FieldType.MultipleList.getIntValue)
              case (OptionSchema, Some(RadioButtons))     => Some(FieldType.SingleList.getIntValue)
              case (OptionSchema, _)                      => Some(FieldType.Text.getIntValue)
              case (OptionWithChildSchema, _)             => Some(FieldType.MultipleList.getIntValue)
            }
          }
        case _ => throw new RuntimeException(s"custom field id not found [${changeLogItem.field}]")
      }
      optTypeId.map(typeId => BacklogAttributeInfo(optId = None, typeId = typeId.toString))
    case _ => None
  }

}
