package com.nulabinc.backlog.j2b.issue.writer.convert

import com.nulabinc.backlog.j2b.jira.domain.FieldDefinitions
import com.nulabinc.backlog.j2b.jira.domain.export.FieldType
import com.nulabinc.backlog.j2b.jira.domain.mapping.CustomFieldRow
import com.nulabinc.backlog.migration.common.conf.BacklogConstantValue
import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.backlog4j.CustomField.{FieldType => BacklogFieldType}

class FieldWrites extends Writes[FieldDefinitions, Seq[BacklogCustomFieldSetting]] with Logging {

  override def writes(fieldDefinition: FieldDefinitions) = {
    fieldDefinition.fields
      .filter(_.id.startsWith("customfield_"))
      .map { field =>
        BacklogCustomFieldSetting(
          optId                 = Some(field.id.replace("customfield_", "").toLong),
          name                  = field.name,
          description           = "",
          typeId                = field.schema.backlogFieldType.getIntValue,
          required              = false,
          applicableIssueTypes  = Seq.empty[String],
          delete                = false,
          property              = property(fieldDefinition.definitions, field.schema, field.id)
        )
      }
  }

  private [this] def numericProperty(): BacklogCustomFieldNumericProperty =
    BacklogCustomFieldNumericProperty(
      typeId = BacklogConstantValue.CustomField.Numeric,
      optInitialValue = None,
      optUnit = None,
      optMin = None,
      optMax = None
    )

  private [this] def dateProperty(): BacklogCustomFieldDateProperty =
    BacklogCustomFieldDateProperty(
      typeId = BacklogConstantValue.CustomField.Date,
      optInitialDate = None,
      optMin = None,
      optMax = None
    )

  private[this] def multipleProperty(definitions: Seq[CustomFieldRow], id: String): BacklogCustomFieldMultipleProperty =
    BacklogCustomFieldMultipleProperty(
      typeId = BacklogConstantValue.CustomField.MultipleList,
      items = findCustomFieldValues(id, definitions).map(toBacklogItem),
      allowAddItem = true,
      allowInput = false
    )

  private[this] def singleProperty(definitions: Seq[CustomFieldRow], id: String): BacklogCustomFieldMultipleProperty =
    BacklogCustomFieldMultipleProperty(
      typeId = BacklogConstantValue.CustomField.SingleList,
      items = findCustomFieldValues(id, definitions).map(toBacklogItem),
      allowAddItem = true,
      allowInput = false
    )

  private[this] def checkboxProperty(definitions: Seq[CustomFieldRow], id: String): BacklogCustomFieldMultipleProperty =
    BacklogCustomFieldMultipleProperty(
      typeId = BacklogConstantValue.CustomField.CheckBox,
      items = findCustomFieldValues(id, definitions).map(toBacklogItem),
      allowAddItem = true,
      allowInput = false
    )

  private[this] def radioProperty(definitions: Seq[CustomFieldRow], id: String): BacklogCustomFieldMultipleProperty =
    BacklogCustomFieldMultipleProperty(
      typeId = BacklogConstantValue.CustomField.CheckBox,
      items = findCustomFieldValues(id, definitions).map(toBacklogItem),
      allowAddItem = true,
      allowInput = false
    )

  private [this] def property(definitions: Seq[CustomFieldRow], fieldType: FieldType, id: String): BacklogCustomFieldProperty =
    fieldType.backlogFieldType match {
      case BacklogFieldType.Text         => BacklogCustomFieldTextProperty(BacklogConstantValue.CustomField.Text)
      case BacklogFieldType.TextArea     => BacklogCustomFieldTextProperty(BacklogConstantValue.CustomField.TextArea)
      case BacklogFieldType.Numeric      => numericProperty()
      case BacklogFieldType.Date         => dateProperty()
      case BacklogFieldType.MultipleList => multipleProperty(definitions, id)
      case BacklogFieldType.SingleList   => singleProperty(definitions, id)
      case BacklogFieldType.CheckBox     => checkboxProperty(definitions, id)
      case BacklogFieldType.Radio        => radioProperty(definitions, id)
    }

  private[this] def toBacklogItem(name: String): BacklogItem =
    BacklogItem(optId = None, name = name)

  private[this] def findCustomFieldValues(fieldId: String, definitions: Seq[CustomFieldRow]): Seq[String] = definitions
    .find(_.fieldId == fieldId).map(_.values.toSeq)
    .getOrElse(Seq.empty[String])
}
