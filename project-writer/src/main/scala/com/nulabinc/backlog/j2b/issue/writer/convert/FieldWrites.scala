package com.nulabinc.backlog.j2b.issue.writer.convert

import com.nulabinc.backlog.j2b.jira.domain.FieldDefinitions
import com.nulabinc.backlog.j2b.jira.domain.mapping.CustomFieldRow
import com.nulabinc.backlog.migration.common.conf.BacklogConstantValue
import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.jira.client.domain.field._

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
          typeId                = typeId(field.schema),
          required              = false,
          applicableIssueTypes  = Seq.empty[String],
          delete                = false,
          property              = property(fieldDefinition.definitions, field.schema, field.id)
        )
      }
  }

  private [this] def textProperty(): BacklogCustomFieldTextProperty =
    BacklogCustomFieldTextProperty(BacklogConstantValue.CustomField.Text)

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

  private[this] def multipleProperty(definitions: Seq[CustomFieldRow], isMultiple: Boolean, id: String): BacklogCustomFieldMultipleProperty = {
    def multipleTypeId(isMultiple: Boolean): Int = {
      if (isMultiple) BacklogConstantValue.CustomField.MultipleList
      else BacklogConstantValue.CustomField.SingleList
    }

    def findCustomFieldValues(fieldId: String): Seq[String] = definitions
      .find(_.fieldId == fieldId).map(_.values.toSeq)
      .getOrElse(Seq.empty[String])

//    def possibleValues(redmineCustomFieldDefinition: RedmineCustomFieldDefinition): Seq[String] =
//      redmineCustomFieldDefinition.fieldFormat match {
//        case RedmineConstantValue.FieldFormat.BOOL    => booleanPossibleValues()
//        case _                                        => redmineCustomFieldDefinition.possibleValues
//      }

    def toBacklogItem(name: String): BacklogItem =
      BacklogItem(optId = None, name = name)

//    def booleanPossibleValues() = Seq(Messages("common.no"), Messages("common.yes"))

    BacklogCustomFieldMultipleProperty(
      typeId = multipleTypeId(isMultiple),
      items = findCustomFieldValues(id).map(toBacklogItem),
      allowAddItem = true,
      allowInput = false
    )
  }

  private [this] def typeId(schema: FieldSchema): Int =
    (schema.schemaType, schema.customType) match {
      case (StatusSchema, Some(Textarea))         => FieldType.TextArea.getIntValue
      case (OptionSchema, Some(Select))           => FieldType.SingleList.getIntValue
      case (ArraySchema, Some(MultiCheckBoxes))   => FieldType.CheckBox.getIntValue
      case (OptionSchema, Some(RadioButtons))     => FieldType.Radio.getIntValue
      case (StringSchema, _)                      => FieldType.Text.getIntValue
      case (NumberSchema, _)                      => FieldType.Numeric.getIntValue
      case (DateSchema, _)                        => FieldType.Date.getIntValue
      case (DatetimeSchema, _)                    => FieldType.Date.getIntValue
      case (ArraySchema, _)                       => FieldType.MultipleList.getIntValue
      case (UserSchema, _)                        => FieldType.Text.getIntValue
      case (AnySchema, _)                         => FieldType.Text.getIntValue
      case (OptionSchema, _)                      => FieldType.Text.getIntValue
      case (OptionWithChildSchema, _)             => FieldType.MultipleList.getIntValue
    }


  private [this] def property(definitions: Seq[CustomFieldRow], schema: FieldSchema, id: String): BacklogCustomFieldProperty =
    (schema.schemaType, schema.customType) match {
      case (StatusSchema, Some(Textarea))         => textProperty()
      case (StringSchema, _)                      => textProperty()
      case (NumberSchema, _)                      => numericProperty()
      case (DateSchema, _)                        => dateProperty()
      case (DatetimeSchema, _)                    => dateProperty()
      case (ArraySchema, _)                       => multipleProperty(definitions, true, id)
      case (UserSchema, _)                        => textProperty()
      case (AnySchema, _)                         => textProperty()
      case (OptionSchema, Some(Select))           => multipleProperty(definitions, false, id)
      case (OptionSchema, Some(MultiCheckBoxes))  => multipleProperty(definitions, true, id)
      case (OptionSchema, Some(RadioButtons))     => multipleProperty(definitions, false, id)
      case (OptionSchema, _)                      => textProperty()
      case (OptionWithChildSchema, _)             => multipleProperty(definitions, true, id)
    }

}
