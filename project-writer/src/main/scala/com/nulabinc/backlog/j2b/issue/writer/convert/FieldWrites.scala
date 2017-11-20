package com.nulabinc.backlog.j2b.issue.writer.convert

import com.nulabinc.backlog.migration.common.conf.BacklogConstantValue
import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.backlog4j.CustomField.FieldType
import com.nulabinc.jira.client.domain.field._
import com.osinka.i18n.Messages

class FieldWrites extends Writes[Seq[Field], Seq[BacklogCustomFieldSetting]] with Logging {

  override def writes(fields: Seq[Field]) = {
    fields
      .filter(_.schema.isDefined)
      .filter(_.id.startsWith("customfield_"))
      .map { field =>
        BacklogCustomFieldSetting(
          optId                 = Some(field.id.replace("customfield_", "").toLong),
          name                  = field.name,
          description           = "",
          typeId                = typeId(field.schema.get),
          required              = false,
          applicableIssueTypes  = Seq.empty[String],
          delete                = false,
          property              = property(field.schema.get)
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

  private[this] def multipleProperty(isMultiple: Boolean): BacklogCustomFieldMultipleProperty = {
    def multipleTypeId(isMultiple: Boolean): Int = {
      if (isMultiple) BacklogConstantValue.CustomField.MultipleList
      else BacklogConstantValue.CustomField.SingleList
    }

//    def possibleValues(redmineCustomFieldDefinition: RedmineCustomFieldDefinition): Seq[String] =
//      redmineCustomFieldDefinition.fieldFormat match {
//        case RedmineConstantValue.FieldFormat.BOOL    => booleanPossibleValues()
//        case _                                        => redmineCustomFieldDefinition.possibleValues
//      }

//    def toBacklogItem(name: String): BacklogItem =
//      BacklogItem(optId = None, name = name)

//    def booleanPossibleValues() = Seq(Messages("common.no"), Messages("common.yes"))

    BacklogCustomFieldMultipleProperty(
      typeId = multipleTypeId(isMultiple),
      items = Seq.empty[BacklogItem],
      // TODO: get possible values from jira api
//      items = possibleValues(redmineCustomFieldDefinition).map(toBacklogItem),
      allowAddItem = true,
      allowInput = false
    )
  }

  private [this] def typeId(schema: FieldSchema): Int =
    (schema.schemaType, schema.customType) match {
      case (StatusSchema, Some(Textarea))         => FieldType.TextArea.getIntValue
      case (StringSchema, _)                      => FieldType.Text.getIntValue
      case (NumberSchema, _)                      => FieldType.Numeric.getIntValue
      case (DateSchema, _)                        => FieldType.Date.getIntValue
      case (DatetimeSchema, _)                    => FieldType.Date.getIntValue
      case (ArraySchema, _)                       => FieldType.MultipleList.getIntValue
      case (UserSchema, _)                        => FieldType.Text.getIntValue
      case (AnySchema, _)                         => FieldType.Text.getIntValue
      case (OptionSchema, Some(Select))           => FieldType.SingleList.getIntValue
      case (OptionSchema, Some(MultiCheckBoxes))  => FieldType.MultipleList.getIntValue
      case (OptionSchema, Some(RadioButtons))     => FieldType.Radio.getIntValue
      case (OptionSchema, _)                      => FieldType.Text.getIntValue
      case (OptionWithChildSchema, _)             => FieldType.MultipleList.getIntValue
    }


  private [this] def property(schema: FieldSchema): BacklogCustomFieldProperty =
    (schema.schemaType, schema.customType) match {
      case (StatusSchema, Some(Textarea))         => textProperty()
      case (StringSchema, _)                      => textProperty()
      case (NumberSchema, _)                      => numericProperty()
      case (DateSchema, _)                        => dateProperty()
      case (DatetimeSchema, _)                    => dateProperty()
      case (ArraySchema, _)                       => multipleProperty(true)
      case (UserSchema, _)                        => textProperty()
      case (AnySchema, _)                         => textProperty()
      case (OptionSchema, Some(Select))           => multipleProperty(false)
      case (OptionSchema, Some(MultiCheckBoxes))  => multipleProperty(true)
      case (OptionSchema, Some(RadioButtons))     => multipleProperty(false)
      case (OptionSchema, _)                      => textProperty()
      case (OptionWithChildSchema, _)             => multipleProperty(true)
    }

}
