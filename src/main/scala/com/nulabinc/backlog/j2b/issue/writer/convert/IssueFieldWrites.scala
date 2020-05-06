package com.nulabinc.backlog.j2b.issue.writer.convert

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.domain.export._
import com.nulabinc.backlog.j2b.jira.utils.DatetimeToDateFormatter
import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain.BacklogCustomField
import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.backlog4j.CustomField.{FieldType => BacklogFieldType}

class IssueFieldWrites @Inject() (customFieldDefinitions: Seq[Field])
    extends Writes[IssueField, Option[BacklogCustomField]]
    with Logging
    with DatetimeToDateFormatter {

  override def writes(issueField: IssueField): Option[BacklogCustomField] =
    try {
      customFieldDefinitions.find(_.id == issueField.id).map { field =>
        field.schema match {
          case FieldType.TextArea =>
            toTextAreaCustomField(
              field,
              issueField.value.asInstanceOf[StringFieldValue]
            )
          case FieldType.Number =>
            toNumberCustomField(
              field,
              issueField.value.asInstanceOf[NumberFieldValue]
            )
          case FieldType.Date =>
            toDateCustomField(
              field,
              issueField.value.asInstanceOf[StringFieldValue]
            )
          case FieldType.DateTime =>
            toDateTimeCustomField(
              field,
              issueField.value.asInstanceOf[StringFieldValue]
            )
          case FieldType.Checkbox =>
            toCheckBoxCustomField(field, issueField.value)
          case FieldType.Radio =>
            toRadioCustomField(
              field,
              issueField.value.asInstanceOf[OptionFieldValue]
            )
          case FieldType.SingleSelect =>
            toSingleListCustomField(
              field,
              issueField.value.asInstanceOf[OptionFieldValue]
            )
          case FieldType.MultiSelect =>
            toMultipleListCustomField(
              field,
              issueField.value.asInstanceOf[ArrayFieldValue]
            )
          case FieldType.CustomLabels =>
            toMultipleListCustomField(
              field,
              issueField.value.asInstanceOf[ArrayFieldValue]
            )
          case FieldType.Option =>
            toSingleListCustomField(
              field,
              issueField.value.asInstanceOf[OptionFieldValue]
            )
          case FieldType.OptionWithChild =>
            toMultipleListCustomField(
              field,
              issueField.value.asInstanceOf[ArrayFieldValue]
            )
          //        case FieldType.Components       =>
          case FieldType.IssueType =>
            toIssueTypeCustomField(
              field,
              issueField.value.asInstanceOf[IssueTypeFieldValue]
            )
          case FieldType.User =>
            toUserCustomField(
              field,
              issueField.value.asInstanceOf[UserFieldValue]
            )
          case FieldType.Strings =>
            issueField.value match {
              case v: ArrayFieldValue =>
                toMultipleListCustomField(field, v)
              case v: StringFieldValue =>
                toMultipleListCustomField(field, ArrayFieldValue(Seq(v)))
              case v =>
                throw new RuntimeException(
                  s"Unsupported FieldValue type. Field: $issueField Value: $v"
                )
            }
          case FieldType.String =>
            toTextCustomField(
              field,
              issueField.value.asInstanceOf[StringFieldValue]
            )
          case FieldType.Unknown =>
            toTextCustomField(
              field,
              issueField.value.asInstanceOf[StringFieldValue]
            )
        }
      }
    } catch {
      case e: Throwable =>
        logger.error(e.getMessage + " Value: " + issueField.value.toString)
        throw e
    }

  private def toTextCustomField(field: Field, issueField: StringFieldValue) =
    BacklogCustomField(
      name = field.name,
      fieldTypeId = BacklogFieldType.Text.getIntValue,
      optValue = Some(issueField.value),
      values = Seq.empty[String]
    )

  private def toTextAreaCustomField(
      field: Field,
      issueField: StringFieldValue
  ) =
    BacklogCustomField(
      name = field.name,
      fieldTypeId = BacklogFieldType.TextArea.getIntValue,
      optValue = Option(issueField.value),
      values = Seq.empty[String]
    )

  private def toNumberCustomField(field: Field, issueField: NumberFieldValue) =
    BacklogCustomField(
      name = field.name,
      fieldTypeId = BacklogFieldType.Numeric.getIntValue,
      optValue = Some(issueField.value.toString),
      values = Seq.empty[String]
    )

  private def toDateCustomField(field: Field, issueField: StringFieldValue) =
    BacklogCustomField(
      name = field.name,
      fieldTypeId = BacklogFieldType.Date.getIntValue,
      optValue = Option(issueField.value),
      values = Seq.empty[String]
    )

  private def toDateTimeCustomField(
      field: Field,
      issueField: StringFieldValue
  ) =
    BacklogCustomField(
      name = field.name,
      fieldTypeId = BacklogFieldType.Date.getIntValue,
      optValue = Option(dateTimeStringToDateString(issueField.value)),
      values = Seq.empty[String]
    )

  private def toMultipleListCustomField(
      field: Field,
      issueField: ArrayFieldValue
  ) =
    BacklogCustomField(
      name = field.name,
      fieldTypeId = BacklogFieldType.MultipleList.getIntValue,
      optValue = None,
      values = issueField.values.map(_.value)
    )

  private def toCheckBoxCustomField(
      field: Field,
      issueFieldValue: FieldValue
  ) = {
    val values = issueFieldValue match {
      case value: ArrayFieldValue  => value.values.map(_.value)
      case value: StringFieldValue => Seq(value.value)
    }
    BacklogCustomField(
      name = field.name,
      fieldTypeId = BacklogFieldType.CheckBox.getIntValue,
      optValue = None,
      values = values
    )
  }

  private def toSingleListCustomField(
      field: Field,
      issueField: OptionFieldValue
  ) =
    BacklogCustomField(
      name = field.name,
      fieldTypeId = BacklogFieldType.SingleList.getIntValue,
      optValue = Option(issueField.value),
      values = Seq.empty[String]
    )

  private def toRadioCustomField(field: Field, issueField: OptionFieldValue) =
    BacklogCustomField(
      name = field.name,
      fieldTypeId = BacklogFieldType.Radio.getIntValue,
      optValue = Option(issueField.value),
      values = Seq.empty[String]
    )

  private def toUserCustomField(field: Field, issueField: UserFieldValue) =
    BacklogCustomField(
      name = field.name,
      fieldTypeId = BacklogFieldType.SingleList.getIntValue,
      optValue = Option(issueField.value),
      values = Seq.empty[String]
    )

  private def toIssueTypeCustomField(
      field: Field,
      issueField: IssueTypeFieldValue
  ) =
    BacklogCustomField(
      name = field.name,
      fieldTypeId = BacklogFieldType.SingleList.getIntValue,
      optValue = Option(issueField.value),
      values = Seq.empty[String]
    )
}
