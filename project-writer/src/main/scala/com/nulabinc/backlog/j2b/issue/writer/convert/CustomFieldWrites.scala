package com.nulabinc.backlog.j2b.issue.writer.convert

import javax.inject.Inject

import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain.BacklogCustomField
import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.backlog4j.CustomField.FieldType
import com.nulabinc.jira.client.domain._
import com.nulabinc.jira.client.domain.field.{Field, NumberSchema, StringSchema}
import com.nulabinc.jira.client.domain.issue.{IssueField, NumberFieldValue, StringFieldValue}

private [writer] class CustomFieldWrites @Inject()(customFieldDefinition: Seq[Field])
    extends Writes[IssueField, Option[BacklogCustomField]]
    with Logging {

  override def writes(issueField: IssueField) = {
    customFieldDefinition.find(_.id == issueField.id) match {
      case Some(field) =>
        field.schema.map { schema =>
          schema.schemaType match {
            case StringSchema => toTextCustomField(field.name, issueField.value.asInstanceOf[StringFieldValue])
            case NumberSchema => toNumberCustomField(field.name, issueField.value.asInstanceOf[NumberFieldValue])

          }
        }
    }
  }

  private [this] def toTextCustomField(name: String, issueField: StringFieldValue) =
    BacklogCustomField(
      name = name,
      fieldTypeId = FieldType.Text.getIntValue,
      optValue = Some(issueField.value),
      values = Seq.empty[String]
    )

  private [this] def toNumberCustomField(name: String, issueField: NumberFieldValue) =
    BacklogCustomField(
      name = name,
      fieldTypeId = FieldType.Numeric.getIntValue,
      optValue = Some(issueField.value.toString),
      values = Seq.empty[String]
    )
}
