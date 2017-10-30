package com.nulabinc.backlog.j2b.issue.writer.convert

import javax.inject.Inject

import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain.BacklogCustomField
import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.backlog4j.CustomField.FieldType
import com.nulabinc.jira.client.domain.field._
import com.nulabinc.jira.client.domain.issue._

class IssueFieldWrites @Inject()(customFieldDefinitions: Seq[Field])
    extends Writes[IssueField, Option[BacklogCustomField]]
    with Logging {

  override def writes(issueField: IssueField) = {
    customFieldDefinitions.find(_.id == issueField.id) match {
      case Some(field) =>
        field.schema.map { schema =>
          (schema.schemaType, schema.customType) match {
            case (StatusSchema, Some(Textarea))         => toTextAreaCustomField(field, issueField.asInstanceOf[StringFieldValue])
            case (StringSchema, _)                      => toTextCustomField(field, issueField.asInstanceOf[StringFieldValue])
            case (NumberSchema, _)                      => toNumberCustomField(field, issueField.asInstanceOf[NumberFieldValue])
            case (DateSchema, _)                        => toDateCustomField(field, issueField.asInstanceOf[StringFieldValue])
            case (DatetimeSchema, _)                    => toDateCustomField(field, issueField.asInstanceOf[StringFieldValue])
            case (ArraySchema, _)                       => toMultipleListCustomField(field, issueField.asInstanceOf[ArrayFieldValue])
            case (UserSchema, _)                        => toTextCustomField(field, issueField.asInstanceOf[StringFieldValue])
            case (AnySchema, _)                         => toTextCustomField(field, issueField.asInstanceOf[StringFieldValue])
            case (OptionSchema, Some(Select))           => toSingleListCustomField(field, issueField.asInstanceOf[StringFieldValue])
            case (OptionSchema, Some(MultiCheckBoxes))  => toMultipleListCustomField(field, issueField.asInstanceOf[ArrayFieldValue])
            case (OptionSchema, Some(RadioButtons))     => toSingleListCustomField(field, issueField.asInstanceOf[StringFieldValue])
            case (OptionSchema, _)                      => toTextCustomField(field, issueField.asInstanceOf[StringFieldValue])
            case (OptionWithChildSchema, _)             => toMultipleListCustomField(field, issueField.asInstanceOf[ArrayFieldValue])
          }
        }
      case _ => None
    }
  }

  private def toTextCustomField(field: Field, issueField: StringFieldValue) =
    BacklogCustomField(
      name = field.name,
      fieldTypeId = FieldType.Text.getIntValue,
      optValue = Some(issueField.value),
      values = Seq.empty[String]
    )

  private def toTextAreaCustomField(field: Field, issueField: StringFieldValue) =
    BacklogCustomField(
      name = field.name,
      fieldTypeId = FieldType.TextArea.getIntValue,
      optValue = Option(issueField.value),
      values = Seq.empty[String]
    )

  private def toNumberCustomField(field: Field, issueField: NumberFieldValue) =
    BacklogCustomField(
      name = field.name,
      fieldTypeId = FieldType.Numeric.getIntValue,
      optValue = Some(issueField.value.toString),
      values = Seq.empty[String]
    )

  private def toDateCustomField(field: Field, issueField: StringFieldValue) =
    BacklogCustomField(
      name = field.name,
      fieldTypeId = FieldType.Date.getIntValue,
      optValue = Option(issueField.value),
      values = Seq.empty[String]
    )

  private def toMultipleListCustomField(field: Field, issueField: ArrayFieldValue) =
    BacklogCustomField(
      name = field.name,
      fieldTypeId = FieldType.MultipleList.getIntValue,
      optValue = None,
      values = issueField.values.map(_.value)
    )

  private def toSingleListCustomField(field: Field, issueField: StringFieldValue) =
    BacklogCustomField(
      name = field.name,
      fieldTypeId = FieldType.SingleList.getIntValue,
      optValue = Option(issueField.value),
      values = Seq.empty[String]
    )
}
